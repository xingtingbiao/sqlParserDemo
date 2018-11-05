package lineage;

import business.ActionRoute;
import business.AtomicRouteTSelect;
import business.BloodRelation;
import exception.QuantityException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import main.ParseASTNode;
import org.apache.hadoop.hive.ql.parse.ParseException;
import parser.base.*;
import parser.statement.TCustomSqlStatement;
import parser.statement.function.TFunParams;
import parser.statement.function.TFunctionCall;
import parser.statement.select.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分析sql血缘关系类, 虚表-->物理表(1::N), 虚列-->物理列(1::N)
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor(staticName = "of")
public class Lineage {

    private TCustomSqlStatement statement;

    private List<ActionRoute> actionRoutes;

    private List<ColumnLineage> columnLineages;

    private List<ActionRoute> filterRoutes;

    private void initStatement(String sql) throws ParseException {
        this.statement = ParseASTNode.parseNode(sql);
    }

    /**
     * 程序的入口, 最后判断this.filterRoutes不为null或者size大于0, ActionRoute里面的columnLineages就是最外层的字段血缘图
     * @param sql
     * @throws ParseException
     * @throws QuantityException
     */
    public void columnLineage(String sql) throws ParseException, QuantityException {
        this.initActionRoutes(sql);
        if (null != this.actionRoutes && this.actionRoutes.size() > 0) {
            this.analysisActionRoutes();
        }
    }

    private void initActionRoutes(String sql) throws ParseException {
        List<ActionRoute> routes = new ArrayList<>();
        this.initStatement(sql);
        if (this.statement instanceof TSelectSqlStatement) {
            BloodRelation relation = new BloodRelation((TSelectSqlStatement) statement);
            relation.analysisBlood();
            List<AtomicRouteTSelect> atomicRouteTSelects = relation.getAtomicRouteTSelects();
            if (null != atomicRouteTSelects && atomicRouteTSelects.size() > 0) {
                for (AtomicRouteTSelect atomic : atomicRouteTSelects) {
                    ActionRoute actionRoute = relation.dealRouteWithLateralView(atomic.getActionRoute());
                    if (null != actionRoute) routes.add(actionRoute);
                }
            }
        }
        if (routes.size() > 0) this.actionRoutes = routes;
    }

    private void analysisActionRoutes() throws QuantityException {
        // TODO 这里就是最主要的分析函数, 分析完需要返回最外层每个列的所有血缘关系1::N
        if (this.filterRoutes == null) this.filterRoutes = new ArrayList<>();
        for (ActionRoute route : this.actionRoutes) {
            ActionRoute filter = this.filter(route, null);
            if (null != filter) this.filterRoutes.add(filter);
        }
    }


    /**
     * 根据分析好的血缘关系图, 以及给定的敏感字段参数, 计算整个sql的敏感字段信息图谱
     // * @param 1mapFields 指定敏感源的map类型参数 key为表名, value是表名对应的敏感字段的集合
     * @return 返回计算完敏感信息后的血缘关系图谱
     */
    ActionRoute filter(ActionRoute route, List<ColumnLineage> columnLineages) throws QuantityException {
        //TODO null == mapFields的时候就是默认所有字段,即解析所有字段的血缘关系
        TResultColumnList columnList = route.getColumnList();
        if (null != columnList && columnList.size() > 0) {
            if (null == columnLineages) {
                // 此为第一次调用的时候,需要初始化一次
                columnLineages = this.initState(columnList, route);//处理初始状态
            } else if (columnLineages.size() > 0){
                // 此为非第一次调用的时候
                columnLineages = this.analysisColumnLineages(route, columnLineages);
            } else {
                return null;
            }
            if (route.getcRoute() != null) {
                return this.filter(route.getcRoute(), columnLineages);
            } else {
                return route;
            }
        } else {
            // throw new QuantityException("Table field parsing is incorrect. Please check carefully!");
            return null;
        }
//
//
//        List<String> filterCols = new ArrayList<>();
//        if (null != route.getTableName() || null != route.getAlias()) {
//            List<String> colNames;
//            String tName = backTabName();
//            colNames = mapFields.get(tName);
//            if (null != route.getAlias()) tName = route.getAlias().getName();  //如果表名和别名同时存在的情况
//
//            if (null != colNames && colNames.size() > 0 && null != columnList && columnList.size() > 0) {
//                this.checkAllCol(colNames, tName); // select * from t
//                filterCols = this.packageFilterCols(tName, filterCols, colNames, funMap);
//            }
//        }
//
//        Map<String, List<String>> map;
//        if (null != this.cRoute && filterCols.size() > 0) {
//            Alias alias = this.cRoute.getAlias();
//            TableName tableName = this.cRoute.getTableName();
//            map = backNewMap(filterCols, null, alias, tableName);
//            route = this.cRoute.filter(map, funMap);
//        }
//        if (null == this.cRoute && null != this.columnList && this.columnList.size() > 0) {
//            route = this;
//        }
    }

    private List<ColumnLineage> analysisColumnLineages(ActionRoute route, List<ColumnLineage> columnLineages) {
        // columnLineages不会为null且size大于0, 但是在语法树解析时如果字段未能成功解析出来, 此时size为0
        // 判断是否是select * from
        TResultColumnList columnList = route.getColumnList();
        if (null == columnList || columnList.size() == 0) {
            // throw new QuantityException("Table field parsing is incorrect. Please check carefully!");
            return new ArrayList<>(); // 呼应上面的方法, 表字段未能成功解析, 其实是要抛出异常, 这里暂不处理, 以免阻塞
        }
        if (columnLineages.get(0).getColumn().isAllColumn()) {
            return analysisSelectAll(route, columnLineages);
        } else { // 非select * from 情况
            return analysisSelects(route, columnLineages);
        }
    }

    /**
     * 分析上一层为select * from的情况
     * @param route 当前层的route
     * @param columnLineages 上一层的columnLineages
     * @return
     */
    private List<ColumnLineage> analysisSelectAll(ActionRoute route, List<ColumnLineage> columnLineages) {
        List<TResultColumn> columnList = route.getColumnList().getColList();
        List<ColumnLineage> cls = new ArrayList<>();
        if (columnList.get(0).isAllColumn()){ // 当前层也是select * from的情况
            ColumnLineage columnLineage = ColumnLineage.of()
                    .setTableName(route.getTableName())
                    .setAlias(route.getAlias())
                    .setLibraryName(route.getLibraryName())
                    .setColumn(columnList.get(0))
                    .setNextColumnLineage(columnLineages.get(0));
            cls.add(columnLineage);
        } else {
            for (TResultColumn col : columnList) {
                ColumnLineage columnLineage = ColumnLineage.of()
                        .setTableName(route.getTableName())
                        .setAlias(route.getAlias())
                        .setLibraryName(route.getLibraryName())
                        .setColumn(col)
                        .setNextColumnLineage(columnLineages.get(0));
                cls.add(columnLineage);
            }
        }
        route.setColumnLineages(cls);
        return cls;
    }

    /**
     * 分析上一层与当前层的字段一一对应关系, 非常重要
     * @param route 当前层的route
     * @param columnLineages 上一层的columnLineages
     * @return
     */
    private List<ColumnLineage> analysisSelects(ActionRoute route, List<ColumnLineage> columnLineages) {
        List<TResultColumn> columnList = route.getColumnList().getColList();
        List<ColumnLineage> cls = new ArrayList<>();
        if (columnList.get(0).isAllColumn()){ // 当前层是select * from的情况
            for (ColumnLineage cl : columnLineages) {
                ColumnLineage columnLineage = ColumnLineage.of()
                        .setTableName(route.getTableName())
                        .setAlias(route.getAlias())
                        .setLibraryName(route.getLibraryName())
                        .setColumn(columnList.get(0))
                        .setNextColumnLineage(cl);
                cls.add(columnLineage);
            }
        } else {
            for (TResultColumn col : columnList) {
                // 具体比较上下层的字段是否是对应关系
                ColumnLineage columnLineage = this.equalsColumnLineage(col, columnLineages);
                if (null != columnLineage) {
                    columnLineage.setTableName(route.getTableName()).setAlias(route.getAlias()).setLibraryName(route.getLibraryName());
                    cls.add(columnLineage);
                }
            }
        }
        route.setColumnLineages(cls);
        return cls;
    }

    /**
     * 具体分析某个字段与上一层的所有字段的一一对应关系
     * @param column 当前层的某一个字段
     * @param columnLineages 上一层所有字段(以及他们的血缘关系)
     * @return
     */
    private ColumnLineage equalsColumnLineage(TResultColumn column, List<ColumnLineage> columnLineages) {
        for (ColumnLineage cl : columnLineages) {
            boolean check = false;
            String tName = getTabName(cl);
            if (null != tName) {
                if (null == column.gettNameOrAlias()) {
                    check = this.equalsWithoutTab(column, cl);
                } else if (column.gettNameOrAlias().getName().equalsIgnoreCase(tName)){
                    check = this.equalsWithoutTab(column, cl);
                }
                if (check) {
                    return ColumnLineage.of().setColumn(column).setNextColumnLineage(cl);
                }
            } else {
                // todo 理论上一定会有
            }
        }
        return null;
    }

    private boolean equalsWithoutTab(TResultColumn column, ColumnLineage cl) {
        String colNameStr = backColName(cl.getColumn());
        ColName colName = column.getColName();
        TFunctionCall functionCall = column.getFunctionCall();
        TArrayExp tArrayExp = column.gettArrayExp();
        if (null != colName && null != colNameStr) {
            return colName.getName().equalsIgnoreCase(colNameStr);
        } else if (null != functionCall && null != colNameStr) {
            return equalsFun(functionCall, colNameStr);
        }
        if (null != tArrayExp) tArrayExp = tArrayExp.getAtomicArray();
        if (null != tArrayExp && null != tArrayExp.getColumn()) {
            return equalsWithoutTab(tArrayExp.getColumn(), cl);
        }
        return false;
    }

    private List<ColumnLineage> initState(TResultColumnList columnList, ActionRoute route) {
        List<ColumnLineage> columnLineages = new ArrayList<>();
        if (columnList.getColumn(0).isAllColumn()) {
            ColumnLineage columnLineage = ColumnLineage.of()
                    .setTableName(route.getTableName())
                    .setAlias(route.getAlias())
                    .setLibraryName(route.getLibraryName())
                    .setColumn(new TResultColumn(true));
            columnLineages.add(columnLineage);
        } else {
            for (int i = 0; i < columnList.size(); ++i) {
                TResultColumn column = columnList.getColumn(i);
                ColumnLineage columnLineage = ColumnLineage.of()
                        .setTableName(route.getTableName())
                        .setAlias(route.getAlias())
                        .setLibraryName(route.getLibraryName())
                        .setColumn(column);
                columnLineages.add(columnLineage);
            }
        }
        route.setColumnLineages(columnLineages);
        return columnLineages;
    }

    private boolean equalsFun(TFunctionCall functionCall, String sensitiveField) {
        if (null != functionCall.getFunParams()) {
            return this.isIllegalFunParam(functionCall.getFunParams(), sensitiveField);
        }
        return false;
    }

    private boolean isIllegalFunParam(TFunParams funParams, String sensitiveField) {
        boolean isNotLegalFunParam = isNotLegalFunParam(funParams, sensitiveField);
        if (isNotLegalFunParam) {
            return true;
        } else {
            List<TFunctionCall> functionCalls = funParams.getFunctionCalls();
            if (null != functionCalls && functionCalls.size() > 0) {
                for (TFunctionCall fun : functionCalls) {
                    if (equalsFun(fun, sensitiveField)) return true;
                }
            }
        }
        return false;
    }

    private boolean isNotLegalFunParam(TFunParams funParams, String sensitiveField) {
        boolean isNotLegalFunParam = false;
        List<TResultColumn> resultColumns = funParams.getResultColumns();
        if (null != resultColumns && resultColumns.size() > 0) {
            for (TResultColumn column : resultColumns) {
                ColName colName = column.getColName();
                if (null != colName && sensitiveField.equalsIgnoreCase(colName.getName().toLowerCase())) {
                    isNotLegalFunParam = true;
                    break;
                }
            }
        }
        return isNotLegalFunParam;
    }

    private String getTabName(ColumnLineage cl) {
        String tName = null;
        LibraryName libraryName = cl.getLibraryName(); // 上一层字段所属表的库名
        TableName tableName = cl.getTableName(); // 上一层字段所属表的表名
        Alias alias = cl.getAlias();  // 上一层字段所属表的表别名
        if (null != alias) {
            tName = alias.getName();
        } else if (null != tableName) {
            tName = tableName.getName();
        }
        return tName;
    }

    private String backColName(TResultColumn column) {
        String colName = null;
        if (null != column.getAlias()) {
            colName = column.getAlias().getName();
        } else if (null != column.getColName()){
            colName = column.getColName().getName();
        }
        return colName;
    }

}

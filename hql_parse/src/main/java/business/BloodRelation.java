package business;

import parser.base.*;
import parser.statement.function.TFunctionCall;
import parser.statement.select.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BloodRelation {
  /**
   * 最外层的sql查询语句
   */
  private TSelectSqlStatement firstTSelect;

  /**
   * 最里层所有子查询的物理表的集合以及血缘关系图谱封装类ActionRoute
   */
  private List<AtomicRouteTSelect> atomicRouteTSelects;
  /**
   * 过滤后的血缘关系图
   */
  private List<ActionRoute> filterRoutes;

  public BloodRelation() {
  }

  public BloodRelation(TSelectSqlStatement firstTSelect) {
    this.firstTSelect = firstTSelect;
  }

  public List<AtomicRouteTSelect> initAtomicRouteTSelects() {
    this.atomicRouteTSelects = new ArrayList<>();
    return this.atomicRouteTSelects;
  }

  public TSelectSqlStatement getFirstTSelect() {
    return firstTSelect;
  }

  public void setFirstTSelect(TSelectSqlStatement firstTSelect) {
    this.firstTSelect = firstTSelect;
  }

  public List<AtomicRouteTSelect> getAtomicRouteTSelects() {
    return atomicRouteTSelects;
  }

  public void setAtomicRouteTSelects(List<AtomicRouteTSelect> atomicRouteTSelects) {
    this.atomicRouteTSelects = atomicRouteTSelects;
  }

  public List<ActionRoute> getFilterRoutes() {
    return filterRoutes;
  }

  public void setFilterRoutes(List<ActionRoute> filterRoutes) {
    this.filterRoutes = filterRoutes;
  }

  /**
   * 重点: 分析血缘关系的最主要方法
   */
  public void analysisBlood() {
    if (null != this.firstTSelect) {
      this.initAtomicRouteTSelects();
      this.fun_generate_ARTS(this.firstTSelect, new ActionRoute());
    }
  }

  private void fun_generate_ARTS(TSelectSqlStatement firstTSelect, ActionRoute actionRoute) {
    TTable table = firstTSelect.getTable();
    TJoin joins = firstTSelect.getJoins();
    TCTEExpression cte = firstTSelect.getCteExpression();
    if (null != table) {
      this.fun_cte(table, firstTSelect, actionRoute, cte);
    }
    if (null != joins) {  // joins和table属于不能同时存在类型?
      this.fun_joins(joins, firstTSelect, actionRoute, cte);
    }
  }

  private void fun_cte(TTable table, TSelectSqlStatement firstTSelect, ActionRoute actionRoute, TCTEExpression cte) {
    if (null != cte) {
      TTable newTable = this.fun_compare_cte(cte, table);
      if (null == newTable) {
        this.fun_table(table, firstTSelect, actionRoute, cte);  //fun_table -> union
      } else {
        this.fun_table(newTable, firstTSelect, actionRoute, cte); //fun_table -> subQuery
      }
    } else {
      this.fun_table(table, firstTSelect, actionRoute, null); //fun_table -> tableName
    }
  }

  private TTable fun_compare_cte(TCTEExpression cte, TTable table) {
    TableName tableName = table.getTableName();
    List<TTable> cteTables = cte.getCteTables(); //代码逻辑这里cteTables size > 0 , 代码检查加上非空判断
    Map<String, TTable> tableMap = null;
    if (null != cteTables && cteTables.size() > 0) {
      tableMap = cteTables.stream().filter((x) -> x.getTableName() != null).collect(Collectors.toMap(TTable::notNullTabName, Function.identity()));
    }
    if (null != tableName && (null != tableMap && tableMap.size() > 0)) {
      TTable tTable = tableMap.get(tableName.getName());
      if (null == tTable) {
        return null;
      } else {
        return tTable;
      }
    } else {
      return null;
    }
  }

  private void fun_table(TTable table, TSelectSqlStatement firstTSelect, ActionRoute actionRoute, TCTEExpression cte) {
    TableName tableName = table.getTableName();
    LibraryName libraryName = table.getLibraryName();
    Alias alias = table.getAlias();
    TResultColumnList columnList = firstTSelect.getColumnList();
    actionRoute.setLateralViews(firstTSelect.getLateralViews());
    TSelectSqlStatement subQuery = table.getSubQuery();
    TUnion union = table.getUnion();
    if (null != alias) actionRoute.setAlias(alias);
    if (null != columnList && columnList.size() > 0) actionRoute.setColumnList(columnList);
    if (null != tableName) {
      actionRoute.setTableName(tableName);
      actionRoute.setLibraryName(libraryName);
      this.atomicRouteTSelects.add(new AtomicRouteTSelect(firstTSelect, actionRoute));
    } else if (null != subQuery) {
      this.fun_generate_ARTS(subQuery, new ActionRoute(actionRoute));
    } else if (null != union) {
      this.fun_union(union, actionRoute, cte);
    }
  }

  private void fun_union(TUnion union, ActionRoute actionRoute, TCTEExpression cte) {
    TSelectSqlStatement rightQuery = union.getRightQuery();
    TSelectSqlStatement leftQuery = union.getLeftQuery();
    TUnion leftUnion = union.getLeftUnion();
    if (null != rightQuery) {
      if (null == rightQuery.getCteExpression() && null != cte) rightQuery.setCteExpression(cte);
      this.fun_generate_ARTS(rightQuery, new ActionRoute(actionRoute));
    }
    if (null != leftQuery) {
      if (null == leftQuery.getCteExpression() && null != cte) leftQuery.setCteExpression(cte);
      this.fun_generate_ARTS(leftQuery, new ActionRoute(actionRoute));
    } else if (null != leftUnion) {
      this.fun_union(leftUnion, actionRoute, cte);
    }
  }

  private void fun_joins(TJoin joins, TSelectSqlStatement firstTSelect, ActionRoute ar, TCTEExpression cte) {
    TTable rightTable = joins.getRightTable();
    TTable leftTable = joins.getLeftTable();
    TJoin leftJoin = joins.getLeftJoin();
    ActionRoute rightRoute = new ActionRoute(ar.getAlias(), ar.getTableName(), ar.getColumnList(), ar.getcRoute(), ar.getLateralViews());
    ActionRoute leftRoute = new ActionRoute(ar.getAlias(), ar.getTableName(), ar.getColumnList(), ar.getcRoute(), ar.getLateralViews());
    if (null != rightTable) {
      this.fun_cte(rightTable, firstTSelect, rightRoute, cte);
    }
    if (null != leftTable) {
      this.fun_cte(leftTable, firstTSelect, leftRoute, cte);
    } else if (null != leftJoin) {
      this.fun_joins(leftJoin, firstTSelect, leftRoute, cte);
    }
  }

  public void initFilterRoutes(Map<String, List<String>> mapFields, Map<String, Integer> funMap) {
    if (null != this.firstTSelect) {
      this.analysisBlood();
      this.filterFirstSelectCols(mapFields, funMap);
    }
  }

  public Set<TResultColumn> filterSensitiveFields(Map<String, List<String>> mapFields, Map<String, Integer> funMap) {
    this.initFilterRoutes(mapFields, funMap);
    Set<TResultColumn> illegalCols = new HashSet<>();
    if (null != this.filterRoutes && this.filterRoutes.size() > 0) {
      for (ActionRoute route : this.filterRoutes) {
        if (null != route.getFilterColumns() && route.getFilterColumns().size() > 0) {
          illegalCols.addAll(route.getFilterColumns());
        }
      }
    }
    return illegalCols;
  }

  private void filterFirstSelectCols(Map<String, List<String>> mapFields, Map<String, Integer> funMap) {
    //层层逆推, 最底层往外递推, 得出结果
    if (null == this.filterRoutes) this.filterRoutes = new ArrayList<>();
    if (null != this.atomicRouteTSelects && this.atomicRouteTSelects.size() > 0) {
      for (AtomicRouteTSelect atomic : this.atomicRouteTSelects) {
        ActionRoute actionRoute = this.dealRouteWithLateralView(atomic.getActionRoute());
        ActionRoute filterRoute = actionRoute.filter(mapFields, funMap);
        if (null != filterRoute) this.filterRoutes.add(filterRoute);
      }
    }
  }

  public ActionRoute dealRouteWithLateralView(ActionRoute actionRoute) {
    if (null != actionRoute) {
      // 都不会存在null值元素
      List<TResultColumn> colList = actionRoute.getColumnList().getColList();
      ActionRoute cRoute = actionRoute.getcRoute();
      if (null != colList && colList.size() > 0 && null != cRoute && null != cRoute.getLateralViews() && cRoute.getLateralViews().size() > 0) {
        List<TLateralView> lateralViews = cRoute.getLateralViews();
        List<TResultColumn> newColumns = new ArrayList<>();
        for (TResultColumn column : colList) {
          ColName colName = column.getColName();
          Alias alias = column.getAlias();
          Map<Integer, String> map = this.generateColName(colName, alias);
          if (null != map.get(0)) {
            Set<String> strings = this.filterLateralViews(map.get(0), lateralViews);
            for (String col : strings) {
              column.getAlias().setName(col);
              TResultColumn resultColumn = new TResultColumn(column.getColName(), column.gettNameOrAlias(), column.getColType(), column.getColComment(), column.getAlias(), column.getFunctionCall(), column.isAllColumn(), column.getSensitive(), column.getSourceColumn(), column.gettArrayExp());
              newColumns.add(resultColumn);
            }
          } else if (null != map.get(1)) {
            Set<String> strings = this.filterLateralViews(map.get(1), lateralViews);
            for (String col : strings) {
              column.getColName().setName(col);
              TResultColumn resultColumn = new TResultColumn(column.getColName(), column.gettNameOrAlias(), column.getColType(), column.getColComment(), column.getAlias(), column.getFunctionCall(), column.isAllColumn(), column.getSensitive(), column.getSourceColumn(), column.gettArrayExp());
              newColumns.add(resultColumn);
            }
          }
        }
        actionRoute.getColumnList().setColList(newColumns);
      }
      dealRouteWithLateralView(actionRoute.getcRoute());
    }
    return actionRoute;
  }

  private Set<String> filterLateralViews(String name, List<TLateralView> lateralViews) {
    Set<String> columns = new HashSet<>();
    for (TLateralView lv : lateralViews) {
      TResultColumn resultColumn = lv.getResultColumn();
      resultColumn = this.filterLateralViewColumn(name, resultColumn);
      if (null != resultColumn && null != resultColumn.getAlias()) columns.add(resultColumn.getAlias().getName());
    }
    return columns;
  }

  private TResultColumn filterLateralViewColumn(String name, TResultColumn resultColumn) {
    boolean check = false;
    if (resultColumn != null) {
      ColName colName = resultColumn.getColName();
      if (colName != null && colName.getName().equalsIgnoreCase(name)) return resultColumn;
      TFunctionCall functionCall = resultColumn.getFunctionCall();
      if (null != functionCall && null != functionCall.getFunParams() && null != functionCall.getFunParams().getResultColumns() && functionCall.getFunParams().getResultColumns().size() > 0) {
        List<TResultColumn> columns = functionCall.getFunParams().getResultColumns();
        for (TResultColumn col : columns) {
          TResultColumn column = filterLateralViewColumn(name, col);
          if (null != column) {
            check = true;
            break;
          }
        }
      }
      if (check) return resultColumn;
    }
    return null;
  }

  private Map<Integer, String> generateColName(ColName colName, Alias alias) {
    Map<Integer, String> map = new HashMap<>();
    if (alias != null) {
      map.put(0, alias.getName());
    } else if (colName != null) {
      map.put(0, colName.getName());
    }
    return map;
  }
}

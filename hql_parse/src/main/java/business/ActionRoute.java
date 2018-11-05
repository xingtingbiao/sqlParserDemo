
package business;

import java.util.*;

import lineage.ColumnLineage;
import lombok.Data;
import parser.base.*;
import parser.statement.function.TFunParams;
import parser.statement.function.TFunctionCall;
import parser.statement.select.TLateralView;

public class ActionRoute {
  /**
   * 行动路线的表别名
   */
  private Alias alias;
  /**
   * 行动路线的实际表名
   */
  private TableName tableName;
  /**
   * 行动路线实际表名前面的库名
   */
  private LibraryName libraryName;
  /**
   * 每一层行动路线对应的查询语句的当前字段集合
   */
  private TResultColumnList columnList;
  /**
   * 过滤后的字段集合
   */
  private Set<TResultColumn> filterColumns;
//  private ActionRoute pRoute;
  /**
   * 重点：嵌套的ActionRoute实现整个行动路线
   */
  private ActionRoute cRoute;
  /**
   * 针对LateralView语法提供的解决方案
   */
  private List<TLateralView> lateralViews;

  private List<ColumnLineage> columnLineages;

  ActionRoute() {
  }

  ActionRoute(Alias alias, TableName tableName, TResultColumnList columnList, ActionRoute route, List<TLateralView> lateralViews) {
    this.alias = alias;
    this.tableName = tableName;
    this.columnList = columnList;
    this.cRoute = route;
    this.lateralViews = lateralViews;
  }

  ActionRoute(ActionRoute actionRoute) {
    this.cRoute = actionRoute;
  }

  ActionRoute initActionRoute() {
    this.cRoute = new ActionRoute();
    return this.cRoute;
  }

  Set<TResultColumn> initFilterColumns() {
    this.filterColumns = new HashSet<>();
    return this.filterColumns;
  }

  /**
   * 根据分析好的血缘关系图, 以及给定的敏感字段参数, 计算整个sql的敏感字段信息图谱
   * @param mapFields mapFields 指定敏感源的map类型参数 key为表名, value是表名对应的敏感字段的集合
   * @param funMap 指定白名单的函数, 即无需做脱敏的函数, 比如count()计数函数
   * @return 返回计算完敏感信息后的血缘关系图谱
   */
  ActionRoute filter(Map<String, List<String>> mapFields, Map<String, Integer> funMap) {
    //TODO null == mapFields的时候就是默认所有字段,即解析所有字段的血缘关系
    ActionRoute route = null;
    List<String> filterCols = new ArrayList<>();
    // this.initFilterColumns();
    if (null != this.tableName || null != this.alias) {
      List<String> colNames;
      String tName = backTabName();
      colNames = mapFields.get(tName);
      if (null != this.alias) tName = this.alias.getName();  //如果表名和别名同时存在的情况
      if (null != colNames && colNames.size() > 0 && null != this.columnList && this.columnList.size() > 0) {
        this.checkAllCol(colNames, tName); // select * from t
        filterCols = this.packageFilterCols(tName, filterCols, colNames, funMap);
      }
    }

    Map<String, List<String>> map;
    if (null != this.cRoute && filterCols.size() > 0) {
      Alias alias = this.cRoute.getAlias();
      TableName tableName = this.cRoute.getTableName();
      map = backNewMap(filterCols, null, alias, tableName);
      route = this.cRoute.filter(map, funMap);
    }
    if (null == this.cRoute && null != this.columnList && this.columnList.size() > 0) {
      route = this;
    }
    return route;
  }

  private Map<String, List<String>> backNewMap(List<String> filterCols, Map<String, List<String>> map, Alias alias, TableName tableName) {
    if (null != alias) {
      map = new HashMap<>();
      map.put(alias.getName(), filterCols);
    } else if (null != tableName) {
      map = new HashMap<>();
      map.put(tableName.getName(), filterCols);
    }
    return map;
  }

  private String backTabName() {
    String tName;
    if (null != this.tableName) {
      tName = this.tableName.getName();
    } else {
      tName = this.alias.getName();
    }
    return tName;
  }

  private List<String> packageFilterCols(String tName, List<String> filterCols, List<String> colNames, Map<String, Integer> funMap) {
    for (int i = 0; i < this.columnList.size(); ++i) {
      TResultColumn column = this.columnList.getColumn(i);
      String check = null;
      if (null == column.gettNameOrAlias()) {
        check = this.isIllegal(tName, column, colNames, funMap).get(true);
      } else {
        if (column.gettNameOrAlias().getName().equals(tName))
          check = this.isIllegal(tName, column, colNames, funMap).get(true);
      }
      if (null != check) {
        String[] split = check.substring(2, check.length()).split(Operation.UDS2);
        generateFilterColumns(column, split);
        if (null != column.getAlias()) {
          filterCols.add(column.getAlias().getName() + check);
        } else if (null != column.getColName()) {
          filterCols.add(column.getColName().getName() + check);
        }
      }
    }
    return filterCols;
  }

  private void generateFilterColumns(TResultColumn column, String[] split) {
    if (this.cRoute == null) {
      if (split.length > 1) { // 理论应该等于2, 除非字段名称含有'#@'
        column.setSourceColumn(new TResultColumn(new ColName(split[1]), new TNameOrAlias(split[0])));
      }
      if (this.filterColumns == null) this.initFilterColumns();
      //todo sensitive maybe have different levels
      column.setSensitive(1);
      this.filterColumns.add(column);
    }
  }

  private void checkAllCol(List<String> colNames, String tName) {
    if (this.columnList.getColumn(0).isAllColumn()) {
      this.columnList.getColList().clear();
        for (String val : colNames) {
        int index = val.indexOf(Operation.UDS1);  //@#分隔  #@分隔表名和字段
        String cName;
        if (index >= 0) {
          cName = val.substring(0, index);
        } else {
          cName = val;
        }
        this.columnList.addColumn(new TResultColumn(new ColName(cName), new TNameOrAlias(tName)));
      }
    }
  }

  private Map<Boolean, String> isIllegal(String tName, TResultColumn column, List<String> colNames, Map<String, Integer> funMap) {
    Map<Boolean, String> isIllegalMap = new HashMap<>();
    String splice = "";
    for (String sField : colNames) {
      int index = sField.indexOf(Operation.UDS1);
      if (index >= 0) splice = sField.substring(index, sField.length());
      isIllegalMap = this.analysisIsIllegal(tName, isIllegalMap, splice, column, sField, funMap);
      if (null != isIllegalMap.get(true)) break;
    }
    return isIllegalMap;
  }

  private Map<Boolean, String> analysisIsIllegal(String tName, Map<Boolean, String> isIllegalMap, String splice, TResultColumn column, String sensitiveField, Map<String, Integer> funMap) {
    ColName colName = column.getColName();
    TFunctionCall functionCall = column.getFunctionCall();
    TArrayExp tArrayExp = column.gettArrayExp();
    deaSensitiveField(tName, isIllegalMap, splice, sensitiveField, funMap, colName, functionCall);
    if (null != tArrayExp) tArrayExp = tArrayExp.getAtomicArray();
    if (null != tArrayExp && null != tArrayExp.getColumn()) analysisIsIllegal(tName, isIllegalMap, splice, tArrayExp.getColumn(), sensitiveField, funMap);
    return isIllegalMap;
  }

  private void deaSensitiveField(String tName, Map<Boolean, String> isIllegalMap, String splice, String sensitiveField, Map<String, Integer> funMap, ColName colName, TFunctionCall functionCall) {
    if (null != colName && sensitiveField.equalsIgnoreCase(colName.getName().toLowerCase() + splice)) {
      if ("".equals(splice)) {
        isIllegalMap.put(true, Operation.UDS1 + tName + Operation.UDS2 + colName.getName());
      } else {
        isIllegalMap.put(true, splice);
      }
    } else if (null != functionCall && null != functionCall.getName()) { //name 判空是因为函数没能完全解析
      String check = this.isLegalFun(tName, functionCall, sensitiveField, funMap).get(false);
      if (null != check) isIllegalMap.put(true, check);
    }
  }

  private Map<Boolean, String> isLegalFun(String tName, TFunctionCall functionCall, String sensitiveField, Map<String, Integer> funMap) {
    boolean isLegalFunName = false;
    Map<Boolean, String> isLegalFunMap = new HashMap<>();
    if (null != funMap.get(functionCall.getName().toLowerCase())) {
      isLegalFunName = true;
    }
    if (!isLegalFunName && null != functionCall.getFunParams()) {
      String check = this.isIllegalFunParam(tName, functionCall.getFunParams(), sensitiveField, funMap).get(true);
      if (null != check) {
        isLegalFunMap.put(false, check); // 非法
      }
    }
    return isLegalFunMap;
  }

  private Map<Boolean, String> isIllegalFunParam(String tName, TFunParams funParams, String sensitiveField, Map<String, Integer> funMap) {
    Map<Boolean, String> isIllegalFunParamMap = new HashMap<>();
    String check = null;
    boolean isNotLegalFunParam = isNotLegalFunParam(tName, funParams, sensitiveField, isIllegalFunParamMap);
    List<TFunctionCall> functionCalls = funParams.getFunctionCalls();
    if (!isNotLegalFunParam && null != functionCalls && functionCalls.size() > 0) {
      for (TFunctionCall fun : functionCalls) {
        if (null != fun.getName())
          check = this.isLegalFun(tName, fun, sensitiveField, funMap).get(false);//todo fun.getName 函数解析不全
        if (null != check) { //非法
          isIllegalFunParamMap.put(true, check);
          break;
        }
      }
    }
    return isIllegalFunParamMap;
  }

  private boolean isNotLegalFunParam(String tName, TFunParams funParams, String sensitiveField, Map<Boolean, String> isIllegalFunParamMap) {
    boolean isNotLegalFunParam = false;
    List<TResultColumn> resultColumns = funParams.getResultColumns();
    if (null != resultColumns && resultColumns.size() > 0) {
      for (TResultColumn column : resultColumns) {
        ColName colName = column.getColName();
        int index = sensitiveField.indexOf(Operation.UDS1);  //@#分隔  #@分隔表名和字段
        String splice = "";
        if (index > 0) splice = sensitiveField.substring(index, sensitiveField.length());
        if (null != colName && sensitiveField.equalsIgnoreCase(colName.getName().toLowerCase() + splice)//todo 注意大小写
            && (null == column.gettNameOrAlias()
            || (null != column.gettNameOrAlias() && column.gettNameOrAlias().getName().equals(tName)))) {
          isNotLegalFunParam = true;
          if ("".equals(splice)) {
            isIllegalFunParamMap.put(true, Operation.UDS1 + tName + Operation.UDS2 + colName.getName());
          } else {
            isIllegalFunParamMap.put(true, splice); // 非法
          }
          break;
        }
      }
    }
    return isNotLegalFunParam;
  }

  public Alias getAlias() {
    return alias;
  }

  public void setAlias(Alias alias) {
    this.alias = alias;
  }

  public TableName getTableName() {
    return tableName;
  }

  public void setTableName(TableName tableName) {
    this.tableName = tableName;
  }

  public LibraryName getLibraryName() {
    return libraryName;
  }

  public void setLibraryName(LibraryName libraryName) {
    this.libraryName = libraryName;
  }

  public TResultColumnList getColumnList() {
    return columnList;
  }

  public void setColumnList(TResultColumnList columnList) {
    this.columnList = columnList;
  }

  public Set<TResultColumn> getFilterColumns() {
    return filterColumns;
  }

  public void setFilterColumns(Set<TResultColumn> filterColumns) {
    this.filterColumns = filterColumns;
  }

  public ActionRoute getcRoute() {
    return cRoute;
  }

  public void setcRoute(ActionRoute cRoute) {
    this.cRoute = cRoute;
  }

  public List<TLateralView> getLateralViews() {
    return lateralViews;
  }

  public void setLateralViews(List<TLateralView> lateralViews) {
    this.lateralViews = lateralViews;
  }

  public List<ColumnLineage> getColumnLineages() {
    return columnLineages;
  }

  public void setColumnLineages(List<ColumnLineage> columnLineages) {
    this.columnLineages = columnLineages;
  }
}

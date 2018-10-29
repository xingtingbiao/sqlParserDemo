package business;

import business.entity.TableandColumns;
import business.entity.TempColumn;
import business.entity.TempTable;
import exception.QuantityException;
import exception.SQLFormatException;
import main.ParseASTNode;
import org.apache.hadoop.hive.ql.parse.ParseException;
import parser.base.*;
import parser.statement.TCustomSqlStatement;
import parser.statement.alter.TAlterSqlStatement;
import parser.statement.create.TCreateSqlStatement;
import parser.statement.insert.TInsertSqlStatement;
import parser.statement.select.TJoin;
import parser.statement.select.TSelectSqlStatement;
import parser.statement.select.TTable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Business {
  /**
   * 通过语法树ASTNode生成自定义的数据结构TCustomSqlStatement作为Business的属性
   */
  private TCustomSqlStatement statement;
  /**
   * 根据脱敏原参数最终过滤出最外层查询语句的敏感字段
   */
  private Set<TResultColumn> sensitiveColumns;

  public Business() {
  }

  public TCustomSqlStatement getStatement() {
    return statement;
  }

  public void setStatement(TCustomSqlStatement statement) {
    this.statement = statement;
  }

  public Set<TResultColumn> getSensitiveColumns() {
    return sensitiveColumns;
  }

  public void setSensitiveColumns(Set<TResultColumn> sensitiveColumns) {
    this.sensitiveColumns = sensitiveColumns;
  }

  private void initStatement(String sql) throws ParseException {
    this.statement = ParseASTNode.parseNode(sql);
  }

  public List<TypeOne> getAtomics(String sql) throws ParseException {
    List<TypeOne> typeOnes = new ArrayList<>();
    TCustomSqlStatement statement = ParseASTNode.parseNode(sql);
    if (null != statement) {
      TSelectSqlStatement selectSqlStatement;
      switch (statement.getType()) {
        case Operation.CREATE:
          selectSqlStatement = ((TCreateSqlStatement) statement).getSelectSqlStatement();
          if (null == selectSqlStatement) {
            TypeOne<TCreateSqlStatement> typeOne = new TypeOne<>((TCreateSqlStatement) statement);
            typeOnes.add(typeOne);
          } else {
            this.getSelectAtomics(typeOnes, selectSqlStatement);
          }
          break;
        case Operation.INSERT:
          selectSqlStatement = ((TInsertSqlStatement) statement).getSelectSqlStatement();
          if (null == selectSqlStatement) {
            TypeOne<TInsertSqlStatement> typeOne = new TypeOne<>((TInsertSqlStatement) statement);
            typeOnes.add(typeOne);
          } else {
            this.getSelectAtomics(typeOnes, selectSqlStatement);
          }
          break;
        case Operation.SELECT:
          this.getSelectAtomics(typeOnes, (TSelectSqlStatement) statement);
          break;
        case Operation.ALTER:
          TypeOne<TAlterSqlStatement> typeOne = new TypeOne<>((TAlterSqlStatement) statement);
          typeOnes.add(typeOne);
          break;
        default:
          break;
      }
    }
    return typeOnes;
  }


  /**
   * 获取一个sql中所有出现的物理table
   * @param sql 原始sql
   * @return TTable对象的list集合
   * @throws ParseException 解析异常
   */
  public List<TTable> getAllTable(String sql) throws ParseException {
    List<TTable> allTable = null;
    TCustomSqlStatement statement = ParseASTNode.parseNode(sql);
    if (null != statement) {
      TTable table = statement.getTable();
      allTable = new ArrayList<TTable>();
      TSelectSqlStatement selectSqlStatement;
      switch (statement.getType()) {
        case Operation.CREATE:
          packTableMap(allTable, table, Operation.CREATE);
          selectSqlStatement = ((TCreateSqlStatement) statement).getSelectSqlStatement();
          if (null != selectSqlStatement) {
            this.packSelectAtomicTables(allTable, selectSqlStatement);
          }
          break;
        case Operation.INSERT:
          packTableMap(allTable, table, Operation.INSERT);
          selectSqlStatement = ((TInsertSqlStatement) statement).getSelectSqlStatement();
          if (null != selectSqlStatement) {
            this.packSelectAtomicTables(allTable, selectSqlStatement);
          }
          break;
        case Operation.SELECT:
          packTableMap(allTable, table, Operation.SELECT);
          this.packSelectAtomicTables(allTable, (TSelectSqlStatement) statement);
          break;
        case Operation.DROP:
          packTableMap(allTable, table, Operation.DROP);
          break;
        default:
          break;
      }
    }
    return allTable;
  }

  private void packTableMap(List<TTable> allTable, TTable table, int type) {
    if (null != table) {
      table.setType(type);
      allTable.add(table);
    }
  }

  public List<TCustomSqlStatement> getParentWithChild(String sql) throws ParseException {
    Set<TCustomSqlStatement> parentWithChild = null;
    TCustomSqlStatement statement = ParseASTNode.parseNode(sql);
    if (null != statement) {
      parentWithChild = new HashSet<TCustomSqlStatement>();
      parentWithChild.add(statement);
      TSelectSqlStatement selectSqlStatement;
      switch (statement.getType()) {
        case Operation.CREATE:
          selectSqlStatement = ((TCreateSqlStatement) statement).getSelectSqlStatement();
          if (null != selectSqlStatement) {
            this.packSelectAtomicList(parentWithChild, selectSqlStatement);
          }
          break;
        case Operation.INSERT:
          selectSqlStatement = ((TInsertSqlStatement) statement).getSelectSqlStatement();
          if (null != selectSqlStatement) {
            this.packSelectAtomicList(parentWithChild, selectSqlStatement);
          }
          break;
        case Operation.SELECT:
          this.packSelectAtomicList(parentWithChild, (TSelectSqlStatement) statement);
          break;
        default:
          break;
      }
    }
    if (null != parentWithChild) return new ArrayList<>(parentWithChild);
    return null;
  }

  private void packSelectAtomicList(Set<TCustomSqlStatement> parentWithChild, TSelectSqlStatement selectSqlStatement) {
    BloodRelation bloodRelation = new BloodRelation(selectSqlStatement);
    bloodRelation.analysisBlood();
    List<AtomicRouteTSelect> atomicRouteTSelects = bloodRelation.getAtomicRouteTSelects();
    for (AtomicRouteTSelect atomic : atomicRouteTSelects) {
      TSelectSqlStatement atomicTSelect = atomic.getAtomicTSelect();
      if (null != atomicTSelect) {
        parentWithChild.add(atomicTSelect);
      }
    }
  }

  private void packSelectAtomicTables(List<TTable> allTable, TSelectSqlStatement statement) {
    BloodRelation bloodRelation = new BloodRelation(statement);
    bloodRelation.analysisBlood();
    List<AtomicRouteTSelect> atomicRouteTSelects = bloodRelation.getAtomicRouteTSelects();
    for (AtomicRouteTSelect atomic : atomicRouteTSelects) {
      TSelectSqlStatement atomicTSelect = atomic.getAtomicTSelect();
      ActionRoute actionRoute = atomic.getActionRoute();
      if (null != atomicTSelect && null != actionRoute) {
        TTable table = atomicTSelect.getTable();
        if (null != table) {
          table.setType(Operation.SELECT);
          allTable.add(table);
        } else {
          allTable.add(new TTable(actionRoute.getTableName(), actionRoute.getAlias(), actionRoute.getLibraryName(), Operation.SELECT));
        }
      }
    }
  }

  private void getSelectAtomics(List<TypeOne> typeOnes, TSelectSqlStatement statement) {
    BloodRelation bloodRelation = new BloodRelation(statement);
    bloodRelation.analysisBlood();
    List<AtomicRouteTSelect> atomicRouteTSelects = bloodRelation.getAtomicRouteTSelects();
    for (AtomicRouteTSelect atomic : atomicRouteTSelects) {
      TSelectSqlStatement atomicTSelect = atomic.getAtomicTSelect();
      if (null != atomicTSelect) {
        TypeOne<TSelectSqlStatement> typeOne = new TypeOne<>(atomicTSelect);
        typeOnes.add(typeOne);
      }
    }
  }


  /**
   * 过滤出敏感字段
   * @param sql 原始sql
   * @param mapFields 指定敏感源的map类型参数 key为表名, value是表名对应的敏感字段的集合
   * @param funMap 指定白名单的函数, 即无需做脱敏的函数, 比如count()计数函数
   * @throws ParseException 解析异常
   */
  public void filterSensitiveFields(String sql, Map<String, List<String>> mapFields, Map<String, Integer> funMap) throws ParseException {
    this.initStatement(sql);
    if (this.statement instanceof TSelectSqlStatement) {
      this.sensitiveColumns = new BloodRelation((TSelectSqlStatement) statement).filterSensitiveFields(mapFields, funMap);
    }
  }

  private TCreateSqlStatement backCreateState(String sql) throws ParseException {
    TCreateSqlStatement createState = null;
    this.initStatement(sql);
    if (this.statement instanceof TCreateSqlStatement) {
      createState = (TCreateSqlStatement) this.statement;
    }
    return createState;
  }

  private Map<Boolean, TCreateSqlStatement> isCreateAsSelect(String sql) throws ParseException {
    Map<Boolean, TCreateSqlStatement> createAsSelectMap = new HashMap<>();
    TCreateSqlStatement createState = this.backCreateState(sql);
    if (null != createState && null != createState.getSelectSqlStatement()) {
      createAsSelectMap.put(true, createState);
    } else if (null != createState) {
      createAsSelectMap.put(false, createState);
    }
    return createAsSelectMap;
  }

  private TCreateSqlStatement isCreateTemp(String sql, String tmp, Map<String, List<String>> mapFields, Map<String, Integer> funMap) throws ParseException, SQLFormatException {
    Map<Boolean, TCreateSqlStatement> map = this.isCreateAsSelect(sql);
    TCreateSqlStatement createAsSelect = map.get(true);
    TCreateSqlStatement createWithOutSelect = map.get(false);
    if (null != createAsSelect
        && (null != createAsSelect.getTable().getLibraryName()
        && createAsSelect.getTable().getLibraryName().getName().equals(tmp))) {

      TSelectSqlStatement selectState = createAsSelect.getSelectSqlStatement();
      this.checkFormat(selectState);
      BloodRelation bloodRelation = new BloodRelation(selectState);
      bloodRelation.initFilterRoutes(mapFields, funMap);
      if (null != bloodRelation.getFilterRoutes() && bloodRelation.getFilterRoutes().size() > 0) {
        createAsSelect.setColumnList(bloodRelation.getFilterRoutes().get(0).getColumnList());
      } else {
        createAsSelect.setColumnList(selectState.getColumnList());
      }
      return createAsSelect;
    } else if (null != createWithOutSelect && (null != createWithOutSelect.getTable().getLibraryName()
        && createWithOutSelect.getTable().getLibraryName().getName().equals(tmp))) {
      return createWithOutSelect;
    } else {
      return null;
    }
  }

  /**
   * 通过AdHoc界面创建临时表时, 记录表名和字段及相应的信息
   * @param sql 原始sql
   * @param tmp 临时表所属的库名称: temp, adhoctemp
   * @param mapFields 指定敏感源的map类型参数 key为表名, value是表名对应的敏感字段的集合
   * @param funMap 指定白名单的函数, 即无需做脱敏的函数, 比如count()计数函数
   * @return TempTable 临时表封装对象
   * @throws SQLFormatException sql格式异常
   * @throws ParseException 解析异常
   */
  public TempTable saveTempTable(String sql, String tmp, Map<String, List<String>> mapFields, Map<String, Integer> funMap) throws SQLFormatException, ParseException {
    TCreateSqlStatement createTemp = this.isCreateTemp(sql, tmp, mapFields, funMap);
    if (null != createTemp) {
      TTable table = createTemp.getTable();
      // 上一个方法已经做了非空校验, 所以这里不做校验了
      TempTable tempTable = new TempTable(table.getLibraryName().getName(), table.getTableName().getName());
      TResultColumnList columnList = createTemp.getColumnList();
      if (null == columnList) throw new SQLFormatException("Analyze an exception, please contact us!");
      List<TempColumn> tempColumns = new ArrayList<>();
      for (int i = 0; i < columnList.size(); i++) {
        TResultColumn column = columnList.getColumn(i);
        ColName colName = column.getColName();
        Alias alias = column.getAlias();
        if (colName != null && null == alias) {
          tempColumns.add(new TempColumn(colName.getName(), column.getSensitive(), i));
        } else if (alias != null) {
          tempColumns.add(new TempColumn(alias.getName(), column.getSensitive(), i));
        } else {
          // 语法正常的情况下,出现此异常说明代码解析异常
          throw new SQLFormatException("Field can not be empty, Analyze an exception, please contact us!");
        }
      }
      tempTable.setTempColumns(tempColumns);
      return tempTable;
    } else {
      return null;
    }
  }

  private void checkFormat(TSelectSqlStatement selectState) throws SQLFormatException {
    TResultColumnList columnList = selectState.getColumnList();
    if (null == columnList || columnList.size() == 0 || (columnList.size() > 0 && columnList.getColumn(0).isAllColumn())) {
      throw new SQLFormatException("Field can not be empty, query field can not use '*' after create temporary table!");
    } else {
      List<TResultColumn> colList = columnList.getColList();
      for (TResultColumn column : colList) {
        if (column.getAlias() == null) {
          throw new SQLFormatException("The following query statement field must use an alias when creating a temporary table!");
        }
      }
    }
  }

  public TInsertSqlStatement isInsertTemp(String sql, String tmp, Map<String, List<String>> mapFields, Map<String, Integer> funMap) throws ParseException, QuantityException, SQLFormatException {
    TInsertSqlStatement insertAsSelect = this.isInsertAsSelect(sql);
    if (null != insertAsSelect
        && (null != insertAsSelect.getTable().getLibraryName()
        && insertAsSelect.getTable().getLibraryName().getName().equals(tmp))) {

      TSelectSqlStatement selectState = insertAsSelect.getSelectSqlStatement();
      this.checkFormat(selectState);
      TResultColumnList columnList = insertAsSelect.getColumnList();
      if (null != columnList && columnList.size() > 0) {
        if (columnList.size() != selectState.getColumnList().size()) {
          throw new QuantityException("The number of fields that should be inserted into the table statement does not correspond to the actual number. Please check carefully!");
        }
      }
      return insertAsSelect;
    } else {
      return null;
    }
  }

  /**
   * 根据insert语句判断是否是插入的临时表, 如果是, 根据后面的跟的select语句更新临时表字段的敏感性
   * @param tempTable  这里的tempTable 是从数据库中查询返回的, 查询条件依赖上一个方法isInsertTemp() 返回的TInsertSqlStatement 对象中的TTable中的库名和表名
   * @param insertState 上一个方法isInsertTemp() 返回的TInsertSqlStatement 对象
   * @param mapFields 指定敏感源的map类型参数 key为表名, value是表名对应的敏感字段的集合
   * @param funMap 指定白名单的函数, 即无需做脱敏的函数, 比如count()计数函数
   * @return TempTable 临时表封装对象
   */
  public TempTable updateTempTable(TempTable tempTable, TInsertSqlStatement insertState, Map<String, List<String>> mapFields, Map<String, Integer> funMap) {
    TResultColumnList columnList = insertState.getColumnList();
    TSelectSqlStatement selectState = insertState.getSelectSqlStatement();
    BloodRelation bloodRelation = new BloodRelation(selectState);
    bloodRelation.initFilterRoutes(mapFields, funMap);
    if (null != bloodRelation.getFilterRoutes() && bloodRelation.getFilterRoutes().size() > 0) {
      insertState.setSelectColumns(bloodRelation.getFilterRoutes().get(0).getColumnList().getColList());
    } else {
      insertState.setSelectColumns(selectState.getColumnList().getColList());
    }
    List<TempColumn> tempColumns = tempTable.getTempColumns();
    List<TResultColumn> selectColumns = insertState.getSelectColumns();
    List<TempColumn> newTempColumns = backNewTempColumns(columnList, tempColumns, selectColumns);
    if (newTempColumns.size() > 0) {
      tempTable.setTempColumns(newTempColumns);
    } else {
      tempTable.setTempColumns(null);
    }
    return tempTable;
  }

  private List<TempColumn> backNewTempColumns(TResultColumnList columnList, List<TempColumn> tempColumns, List<TResultColumn> selectColumns) {
    List<TempColumn> newTempColumns = new ArrayList<>();
    if (null == columnList || columnList.size() == 0) {
      for (int i = 0; i < selectColumns.size(); i++) {
        TempColumn tempColumn = tempColumns.get(i);
        TResultColumn column = selectColumns.get(i);
        if (tempColumn.getSensitive() == 0 && column.getSensitive() > 0) {
          tempColumn.setSensitive(column.getSensitive());
          newTempColumns.add(tempColumn);
        }
      }
    } else {
      Map<String, TempColumn> tempColumnMap = tempColumns.stream().collect(Collectors.toMap(TempColumn::getColName, Function.identity()));
      for (int i = 0; i < columnList.size(); i++) {
        TResultColumn column = columnList.getColumn(i);
        column.setSensitive(selectColumns.get(i).getSensitive());
        TempColumn tempColumn = tempColumnMap.get(column.getColName().getName());
        if (null != tempColumn && tempColumn.getSensitive() == 0 && column.getSensitive() > 0) {
          tempColumn.setSensitive(column.getSensitive());
          newTempColumns.add(tempColumn);
        }
      }
    }
    return newTempColumns;
  }

  private TInsertSqlStatement isInsertAsSelect(String sql) throws ParseException {
    TInsertSqlStatement insertState = this.backInsertState(sql);
    if (null != insertState && null != insertState.getSelectSqlStatement()) {
      return insertState;
    } else {
      return null;
    }
  }

  private TInsertSqlStatement backInsertState(String sql) throws ParseException {
    TInsertSqlStatement insertState = null;
    this.initStatement(sql);
    if (this.statement instanceof TInsertSqlStatement) {
      insertState = (TInsertSqlStatement) this.statement;
    }
    return insertState;
  }

  private static List<TCustomSqlStatement> backAtomicSelects(List<TCustomSqlStatement> selects) {
    if (selects.size() == 1 && selects.get(0) instanceof TSelectSqlStatement) {
      return selects;
    } else if (selects.size() > 1) {
      return selects.subList(1, selects.size());
    } else {
      return null;
    }
  }

  public static Map<Boolean, String> usrHTableAuthority(List<TCustomSqlStatement> states, String dbName, List<TableandColumns> hiveTables) throws QuantityException {
    //true: 存在权限问题; false: 不存在权限问题
    List<TCustomSqlStatement> selects = backAtomicSelects(states);
    if (null != selects) {
      for (TCustomSqlStatement select : selects) {
        TResultColumnList columnList = select.getColumnList();
        // 这里是所有的最底层的物理表的select语句(select .. from t和select .. from a join b on ..)
        TTable table = select.getTable(); // select .. from t
        TJoin joins = select.getJoins();  // select .. from a join b on ..
        Map<Boolean, String> tMap = getTableMap(dbName, hiveTables, columnList, table);
        if (tMap != null) return tMap;
        Map<Boolean, String> jMap = getJoinMap(dbName, hiveTables, columnList, joins);
        if (jMap != null) return jMap;
      }
    }
    return new HashMap<Boolean, String>();
  }

  private static Map<Boolean, String> getTableMap(String dbName, List<TableandColumns> hiveTables, TResultColumnList columnList, TTable table) throws QuantityException {
    if (null != table) {
      TableName tableName = table.getTableName();
      if (null == tableName) {  //理论上tableName不会为空, 防止代码异常, 抛出此异常
        throw new QuantityException("The table name resolution is abnormal. Please contact us!");
      }
      LibraryName libraryName = table.getLibraryName();
      if (checkLibraryName(libraryName, dbName)) {
        Map<Boolean, String> map = checkTableAndColumn(tableName, columnList, hiveTables);
        if (null != map.get(true)) { //这里检查为false继续检查, 为true直接返回, 跳出循环
          return map;
        }
      }
    }
    return null;
  }

  private static Map<Boolean, String> getJoinMap(String dbName, List<TableandColumns> hiveTables, TResultColumnList columnList, TJoin joins) throws QuantityException {
    if (null != joins) {
      // 因为是物理查询语句, 所以所有joins里面的都应该是物理表, 由于是左右结构, 所以需要一个递归函数去获取所有的TTable
      List<TTable> tables = new ArrayList<>();
      getAllAtomicTableWithJoins(joins, tables);
      if (tables.size() > 0) {
        for (TTable table : tables) {
          Map<Boolean, String> tMap = getTableMap(dbName, hiveTables, columnList, table);
          if (tMap != null) return tMap;
        }
      }
    }
    return null;
  }

  private static void getAllAtomicTableWithJoins(TJoin joins, List<TTable> tables) {
    tables.add(joins.getRightTable());
    TJoin leftJoin = joins.getLeftJoin();
    TTable leftTable = joins.getLeftTable();
    if (null != leftTable) tables.add(leftTable);
    if (null != leftJoin) getAllAtomicTableWithJoins(leftJoin, tables);
  }

  private static Map<Boolean, String> checkTableAndColumn(TableName tableName, TResultColumnList columnList, List<TableandColumns> hiveTables) throws QuantityException {
    Map<Boolean, String> map = new HashMap<>();
    Map<String, TableandColumns> tabColMap = hiveTables.stream().collect(Collectors.toMap(TableandColumns::getTableName, Function.identity()));
    TableandColumns tabWithCols = tabColMap.get(tableName.getName());
    if (null != tabWithCols) {
      List<String> userColumns = tabWithCols.getUserColumns();
      if (!userColumns.get(0).equals("*")) { //传入的userColumns必然是大于0的
        for (int i = 0; i < columnList.size(); i++) {
          TResultColumn column = columnList.getColumn(i);
          if (column.isAllColumn()) {
            map.put(true, getAuthorityMessage(tabWithCols));
            break;
          } else {
            ColName colName = column.getColName();
            if (null == colName) {  //理论上tableName不会为空, 防止代码异常, 抛出此异常
              throw new QuantityException("The column name resolution is abnormal. Please contact us!");
            }
            if (!userColumns.contains(colName.getName())) {
              map.put(true, getAuthorityMessage(tabWithCols));
              break;
            }
          }
        }
      }
    }
    return map;
  }

  private static String getAuthorityMessage(TableandColumns tabWithCols) {
    List<String> columns = tabWithCols.getUserColumns();
    StringBuilder builder = new StringBuilder();
    builder.append("Permission restrictions reference: DATABASE: ").append(tabWithCols.getDatabaseName()).append(". table: ").append(tabWithCols.getTableName()).append(". columns(");
    for (String col : columns) {
      builder.append(col).append(" ");
    }
    return builder.append(").").toString();
  }

  private static boolean checkLibraryName(LibraryName libraryName, String dbName) {
    return null == libraryName || libraryName.getName().equals(dbName);
  }

}

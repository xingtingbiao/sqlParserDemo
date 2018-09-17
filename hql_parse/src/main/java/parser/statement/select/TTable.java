package parser.statement.select;

import business.entity.AutoRoute;
import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.Alias;
import parser.base.LibraryName;
import parser.base.Operation;
import parser.base.TableName;
import parser.statement.create.TPartition;
import parser.statement.insert.TInsertSqlStatement;

import java.util.ArrayList;
import java.util.List;

public class TTable {

  /**
   * sql中表名对应的封装类
   */
  private TableName tableName;
  /**
   * sql中表名注释对应的封装类
   */
  private TableComment tableComment;
  /**
   * sql中表名的别名对应的封装类
   */
  private Alias alias;
  /**
   * sql中表名的前缀库名对应的封装类
   */
  private LibraryName libraryName;
  /**
   * sql中from后跟的子查询的封装类
   */
  private TSelectSqlStatement subQuery;
  /**
   * sql中union all的封装类
   */
  private TUnion union;
  /**
   * hive sql中分区的集合
   */
  private List<TPartition> partitions;
  /**
   * 表的类型：创建, 查询...
   */
  private int type;
  /**
   * 需要做的路由封装类
   */
  private AutoRoute route;

  public TTable() {
  }

  public TTable(TableName tableName, Alias alias, LibraryName libraryName, int type) {
    this.tableName = tableName;
    this.alias = alias;
    this.libraryName = libraryName;
    this.type = type;
  }

  private TableName initTableName() {
    this.tableName = new TableName();
    return this.tableName;
  }

  public TableComment initTableComment() {
    this.tableComment = new TableComment();
    return this.tableComment;
  }

  public List<TPartition> getPartitions() {
    return partitions;
  }

  public void setPartitions(List<TPartition> partitions) {
    this.partitions = partitions;
  }

  private Alias initAlias() {
    this.alias = new Alias();
    return this.alias;
  }

  private LibraryName initLibraryName() {
    this.libraryName = new LibraryName();
    return this.libraryName;
  }

  public TSelectSqlStatement initSubQuery() {
    this.subQuery = new TSelectSqlStatement(Operation.SELECT);
    return this.subQuery;
  }

  public TUnion initUnion() {
    this.union = new TUnion();
    return this.union;
  }

  public TableName getTableName() {
    return tableName;
  }

  public void setTableName(TableName tableName) {
    this.tableName = tableName;
  }

  public TableComment getTableComment() {
    return tableComment;
  }

  public void setTableComment(TableComment tableComment) {
    this.tableComment = tableComment;
  }

  public LibraryName getLibraryName() {
    return libraryName;
  }

  public void setLibraryName(LibraryName libraryName) {
    this.libraryName = libraryName;
  }

  public void setAlias(Alias alias) {
    this.alias = alias;
  }

  public Alias getAlias() {
    return alias;
  }

  public TSelectSqlStatement getSubQuery() {
    return subQuery;
  }

  public void setSubQuery(TSelectSqlStatement subQuery) {
    this.subQuery = subQuery;
  }

  public TUnion getUnion() {
    return union;
  }

  public void setUnion(TUnion union) {
    this.union = union;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public AutoRoute getRoute() {
    return route;
  }

  public void setRoute(AutoRoute route) {
    this.route = route;
  }

  void fun_TOK_TABREF(ASTNode node_TTable) {
    ArrayList<Node> child_ttable = node_TTable.getChildren();
    ASTNode node_tabName = (ASTNode) child_ttable.get(0);
    this.fun_TOK_TABNAME(node_tabName);
    if (child_ttable.size() > 1) {
      fun_identifier_tn(child_ttable, this); //todo 具体封装
    }
  }

  public void fun_TOK_TABNAME(ASTNode node_tabName) {
    ASTNode node_identifier_tn = null;
    ASTNode node_identifier_ln = null;
    if (node_tabName.getToken().getType() == HiveParser.TOK_TABNAME && !childIsNil(node_tabName)) {
      if (node_tabName.getChildren().size() == 1) {
        node_identifier_tn = (ASTNode) node_tabName.getChildren().get(0);
      } else if (node_tabName.getChildren().size() == 2) {
        node_identifier_ln = (ASTNode) node_tabName.getChildren().get(0);
        node_identifier_tn = (ASTNode) node_tabName.getChildren().get(1);
      }
      if (null != node_identifier_tn && node_identifier_tn.getToken().getType() == HiveParser.Identifier) {
        CommonToken token = (CommonToken) node_identifier_tn.getToken();
        //todo token中含有原始语句以及 这个节点对应的名称和它所在原始语句中的位置(int start, int stop) 例：这里是 table name
        this.initTableName().initToken(token);
      }
      if (null != node_identifier_ln && node_identifier_ln.getToken().getType() == HiveParser.Identifier) {
        this.initLibraryName().initToken((CommonToken) node_identifier_ln.getToken());
      }
    }
  }

  public void fun_TOK_SubQUERY(ASTNode node_TTable) {
    ArrayList<Node> child_subQuery = node_TTable.getChildren();
    ASTNode node_qORu = (ASTNode) child_subQuery.get(0);
    if (child_subQuery.size() == 2) {
      fun_identifier_tn(child_subQuery, this); //todo 具体封装表别名
    }
    switch (node_qORu.getToken().getType()) {
      case HiveParser.TOK_QUERY:
        this.initSubQuery().doParse(node_qORu);  //递归node_query
        break;
      case HiveParser.TOK_UNIONALL:
      case HiveParser.TOK_UNIONDISTINCT:
      case HiveParser.TOK_UNIONTYPE:
        this.initUnion().fun_TOK_UNION(node_qORu);
        //todo
        break;
      default:
        break;
    }
  }

  private void fun_identifier_tn(ArrayList<Node> child_ttable, TTable table) {
    ASTNode node_identifier_alias = (ASTNode) child_ttable.get(1);
    if (node_identifier_alias.getToken().getType() == HiveParser.Identifier) {
      table.initAlias().initToken((CommonToken) node_identifier_alias.getToken());
    }
  }

  private boolean childIsNil(ASTNode node) {
    return node.getChildren() == null || node.getChildren().isEmpty();
  }

  public void fun_TOK_INSERT_DESTINATION(ASTNode node, TInsertSqlStatement insertSqlStatement) {
    if (!childIsNil(node)) {
      ASTNode node_destination = (ASTNode) node.getChildren().get(0);
      switch (node_destination.getToken().getType()) {
        case HiveParser.TOK_INSERT_INTO: //追加
          //TODO 区分下面的
          this.fun_INSERT_INTO_DESTINATION(node_destination, insertSqlStatement);
          break;
        case HiveParser.TOK_DESTINATION: //重写 overwrite
          //todo 区分上面的
          this.fun_INSERT_INTO_DESTINATION(node_destination, insertSqlStatement);
          break;
        default:
          break;
      }
    }
  }

  private void fun_INSERT_INTO_DESTINATION(ASTNode node_insert, TInsertSqlStatement insertSqlStatement) {
    if (!childIsNil(node_insert)) {
      ArrayList<Node> childes = node_insert.getChildren();
      for (int i = 0; i < childes.size(); i++) {
        ASTNode node_tab_col = (ASTNode) childes.get(i);
        this.fun_TAB_COL(node_tab_col, insertSqlStatement);
      }
    }
  }

  private void fun_TAB_COL(ASTNode node, TInsertSqlStatement insertSqlStatement) {
    switch (node.getToken().getType()) {
      case HiveParser.TOK_TAB:
        this.fun_TOK_TAB(node);
        break;
      case HiveParser.TOK_TABCOLNAME:
        //todo
        insertSqlStatement.initColumnList().fun_TOK_TABCOLNAME(node);
        break;
      default:
        break;
    }
  }

  private void fun_TOK_TAB(ASTNode node_tab) {
    if (!childIsNil(node_tab)) {
      List<Node> tab_child = node_tab.getChildren();
      for (int i = 0; i < tab_child.size(); i++) {
        ASTNode node = (ASTNode) tab_child.get(i);
        this.fun_tab_part(node);
      }
    }
  }

  private void fun_tab_part(ASTNode node) {
    switch (node.getToken().getType()) {
      case HiveParser.TOK_TABNAME:
        if (null == this.tableName) this.fun_TOK_TABNAME(node);  //重复insert是在分区的时候出现的
        break;
      case HiveParser.TOK_PARTSPEC:
        // todo 分区表以及多次插入的情况
        break;
      default:
        break;
    }
  }

  public String notNullTabName() {
    return this.tableName != null ? this.tableName.getName() : null;
  }
}

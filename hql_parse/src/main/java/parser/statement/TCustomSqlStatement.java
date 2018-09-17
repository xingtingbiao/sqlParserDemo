package parser.statement;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.*;
import parser.statement.alter.TAlterSqlStatement;
import parser.statement.create.TCreateSqlStatement;
import parser.statement.drop.TDropSqlStatement;
import parser.statement.insert.TInsertSqlStatement;
import parser.statement.others.TSetSqlStatement;
import parser.statement.select.TJoin;
import parser.statement.select.TSelectSqlStatement;
import parser.statement.select.TTable;
import parser.statement.select.TWhereClause;

import java.util.List;

public class TCustomSqlStatement implements BaseStatement {
  /**
   * sql中对应的表的抽象类
   */
  private TTable table;
  /**
   * sql中对应的所有列的抽象类
   */
  private TResultColumnList columnList;
  /**
   * sql中对应的where条件的抽象类
   */
  private TWhereClause whereClause;
  /**
   * sql中对应join类型的抽象类
   */
  private TJoin joins;
  /**
   * 具体的操作类型: parser.base.Operation
   */
  private int type;

  public TCustomSqlStatement() {
  }

  public TCustomSqlStatement(int type) {
    this.type = type;
  }

  public TTable initTable() {
    this.table = new TTable();
    return this.table;
  }

  public TJoin initJoins() {
    this.joins = new TJoin();
    return this.joins;
  }

  public TResultColumnList initColumnList() {
    this.columnList = new TResultColumnList();
    return this.columnList;
  }

  public TWhereClause initWhereClause() {
    this.whereClause = new TWhereClause();
    return this.whereClause;
  }

  public TTable getTable() {
    return table;
  }

  public void setTable(TTable table) {
    this.table = table;
  }

  public TResultColumnList getColumnList() {
    return columnList;
  }

  public void setColumnList(TResultColumnList columnList) {
    this.columnList = columnList;
  }

  public TWhereClause getWhereClause() {
    return whereClause;
  }

  public void setWhereClause(TWhereClause whereClause) {
    this.whereClause = whereClause;
  }

  public TJoin getJoins() {
    return joins;
  }

  public void setJoins(TJoin joins) {
    this.joins = joins;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  @Override
  public void doParse(ASTNode node) {}

  /**
   * 具体的解析语法树的方法, 根据不同的操作类型语法片, 实例化TCustomSqlStatement的子类重写父类的doParser方法实现多态
   * @param node 参数为ASTNode语法树(所有以后注释中提到的语法树节点都是ASTNode的java类对象)
   * @return 返回TCustomSqlStatement对象
   */
  public static TCustomSqlStatement parseASTNode(ASTNode node) {
    TCustomSqlStatement statement = null;
    if (node.isNil() && !childIsNil(node)) {
      ASTNode child_o = (ASTNode) node.getChildren().get(0);
      switch (child_o.getToken().getType()) {
        case HiveParser.TOK_QUERY:
          statement = fun_TOK_QUERY(child_o);
          break;
        case HiveParser.TOK_CREATETABLE:
          statement = new TCreateSqlStatement(Operation.CREATE);
          statement.doParse(child_o);
          break;
        case HiveParser.TOK_ALTERTABLE:
          statement = new TAlterSqlStatement(Operation.ALTER);
          statement.doParse(child_o);
          break;
        case HiveParser.TOK_DROPTABLE:
          statement = new TDropSqlStatement(Operation.DROP);
          statement.doParse(child_o);
          break;
        case HiveParser.TOK_SHOW_SET_ROLE: //比较特殊
          statement = new TSetSqlStatement(Operation.SET);
          statement.doParse(child_o);
          break;
        default:
          break;
      }
    }
    return statement;
  }

  /**
   * 处理TOK_QUERY这个语法词片的方法
   * @param node_query TOK_QUERY这个词片的语法树节点
   * @return 返回TCustomSqlStatement对象
   */
  private static TCustomSqlStatement fun_TOK_QUERY(ASTNode node_query) {
    if (!childIsNil(node_query)) {
      List<Node> childes_query = node_query.getChildren();
      for (Node aChildes_query : childes_query) {
        ASTNode child_q = (ASTNode) aChildes_query;
        if (child_q.getToken().getType() == HiveParser.TOK_INSERT) {
          return backStatement(child_q);
        }
      }
    }
    return null;
  }

  /**
   * 根据TOK_DIR和TOK_TAB来区分是insert操作还是select操作
   * @param node_insert 参数为TOK_INSERT语法词片的语法树节点
   * @return 返回TCustomSqlStatement对象
   */
  private static TCustomSqlStatement backStatement(ASTNode node_insert) {
    TCustomSqlStatement statement = null;
    ASTNode node_tabOrDir = (ASTNode) node_insert.getChildren().get(0).getChildren().get(0);
    switch (node_tabOrDir.getToken().getType()) {
      case HiveParser.TOK_DIR:
        statement = new TSelectSqlStatement(Operation.SELECT);
        statement.doParse((ASTNode) node_insert.getParent());
        break;
      case HiveParser.TOK_TAB:
        statement = new TInsertSqlStatement(Operation.INSERT);
        statement.doParse((ASTNode) node_insert.getParent());
        break;
      //...如还有其他情况直接追加case
      default:
        break;
    }
    return statement;
  }

  public static boolean childIsNil(ASTNode node) {
    return node.getChildren() == null || node.getChildren().isEmpty();
  }
}

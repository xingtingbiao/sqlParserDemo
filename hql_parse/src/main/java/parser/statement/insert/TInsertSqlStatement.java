package parser.statement.insert;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.tools.ant.taskdefs.optional.Cab;
import parser.base.Operation;
import parser.base.TResultColumn;
import parser.statement.TCustomSqlStatement;
import parser.statement.select.TSelectSqlStatement;

import java.util.List;

public class TInsertSqlStatement extends TCustomSqlStatement {

  private TSelectSqlStatement selectSqlStatement;
  private List<TResultColumn> selectColumns;

  public TInsertSqlStatement() {
    super();
  }

  public TInsertSqlStatement(int type) {
    super(type);
  }

  public TSelectSqlStatement initSelectSqlStatement() {
    this.selectSqlStatement = new TSelectSqlStatement(Operation.SELECT);
    return this.selectSqlStatement;
  }

  public TSelectSqlStatement getSelectSqlStatement() {
    return selectSqlStatement;
  }

  public void setSelectSqlStatement(TSelectSqlStatement selectSqlStatement) {
    this.selectSqlStatement = selectSqlStatement;
  }

  public List<TResultColumn> getSelectColumns() {
    return selectColumns;
  }

  public void setSelectColumns(List<TResultColumn> selectColumns) {
    this.selectColumns = selectColumns;
  }

  @Override
  public void doParse(ASTNode node) {
    if (!childIsNil(node)) {
      List<Node> node_child = node.getChildren();
      for (int i = 0; i < node_child.size(); i++) {
        ASTNode node_from_insert = (ASTNode) node_child.get(i);
        this.fun_from_insert(node_from_insert);
      }
    }
  }

  private void fun_from_insert(ASTNode node_from_insert) {
    switch (node_from_insert.getToken().getType()) {
      case HiveParser.TOK_FROM:
        this.fun_TOK_FROM(node_from_insert);
        break;
      case HiveParser.TOK_INSERT:
        this.fun_TOK_INSERT(node_from_insert);
        break;
      default:
        break;
    }
  }

  private void fun_TOK_INSERT(ASTNode node) {
    if (null == this.getTable()) {  // 重复insert是在分区表出现的
      this.initTable().fun_TOK_INSERT_DESTINATION(node, this);
    } else {
      this.getTable().fun_TOK_INSERT_DESTINATION(node, this);
    }
  }

  private void fun_TOK_FROM(ASTNode node) {
    if (!childIsNil(node)) {
      ASTNode node_value_tabref = (ASTNode) node.getChildren().get(0);
      this.fun_VALUE_TABREF(node_value_tabref);
    }
  }

  private void fun_VALUE_TABREF(ASTNode node) {
    switch (node.getToken().getType()) {
      case HiveParser.TOK_VIRTUAL_TABLE:
        //todo
        this.fun_value(node);
        break;
      case HiveParser.TOK_TABREF:
      case HiveParser.TOK_JOIN:
      case HiveParser.TOK_LEFTOUTERJOIN:
      case HiveParser.TOK_LEFTSEMIJOIN:
      case HiveParser.TOK_RIGHTOUTERJOIN:
      case HiveParser.TOK_FULLOUTERJOIN:
      case HiveParser.TOK_LATERAL_VIEW:
      case HiveParser.TOK_LATERAL_VIEW_OUTER:
      case HiveParser.TOK_SUBQUERY:
        this.initSelectSqlStatement().doParse((ASTNode) (node.getParent()).getParent());
        break;
      default:
        break;
    }
  }

  private void fun_value(ASTNode node) {
    if (!childIsNil(node)) {
      List<Node> childes = node.getChildren();
      for (int i = 0; i < childes.size(); i++) {
        ASTNode node_c = (ASTNode) childes.get(i);
        this.fun_value_child(node_c);
      }
    }
  }

  private void fun_value_child(ASTNode node) {
    switch (node.getToken().getType()) {
      case HiveParser.TOK_VIRTUAL_TABREF:
        break;
      case HiveParser.TOK_VALUES_TABLE:
        if (!childIsNil(node)) {
          ASTNode node_value_row = (ASTNode) node.getChildren().get(0);
          if (node_value_row.getToken().getType() == HiveParser.TOK_VALUE_ROW) {
            //todo values
          }
        }
        break;
      default:
        break;
    }
  }
}

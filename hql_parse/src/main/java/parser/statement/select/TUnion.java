package parser.statement.select;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;

public class TUnion {
  /**
   * 左边的查询封装类
   */
  private TSelectSqlStatement leftQuery;
  /**
   * 左边的union封装类
   */
  private TUnion leftUnion;
  /**
   * 右边的查询封装类
   */
  private TSelectSqlStatement rightQuery;

  private String type; //enum

  public TUnion() {
  }

  public TSelectSqlStatement initLeftQuery() {
    this.leftQuery = new TSelectSqlStatement();
    return this.leftQuery;
  }

  public TUnion initLeftUnion() {
    this.leftUnion = new TUnion();
    return this.leftUnion;
  }

  public TSelectSqlStatement initRightQuery() {
    this.rightQuery = new TSelectSqlStatement();
    return this.rightQuery;
  }

  public TSelectSqlStatement getLeftQuery() {
    return leftQuery;
  }

  public void setLeftQuery(TSelectSqlStatement leftQuery) {
    this.leftQuery = leftQuery;
  }

  public TUnion getLeftUnion() {
    return leftUnion;
  }

  public void setLeftUnion(TUnion leftUnion) {
    this.leftUnion = leftUnion;
  }

  public TSelectSqlStatement getRightQuery() {
    return rightQuery;
  }

  public void setRightQuery(TSelectSqlStatement rightQuery) {
    this.rightQuery = rightQuery;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void fun_TOK_UNION(ASTNode node_union) {
    ArrayList<Node> child_union = node_union.getChildren();  //child_union2.size() == 2  Theoretically
    ASTNode child_node_uOrq = (ASTNode) child_union.get(0);
    switch (child_node_uOrq.getToken().getType()) {
      case HiveParser.TOK_QUERY:
        this.initLeftQuery().doParse(child_node_uOrq);
        break;
      case HiveParser.TOK_UNIONALL:
        this.initLeftUnion().fun_TOK_UNION(child_node_uOrq);
        break;
      case HiveParser.TOK_UNIONDISTINCT:
        this.initLeftUnion().fun_TOK_UNION(child_node_uOrq);
        break;
      case HiveParser.TOK_UNIONTYPE:
        break;
    }

    this.initRightQuery().doParse((ASTNode) child_union.get(1));
  }
}

package parser.statement.select;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;

public class TJoin {
  /**
   * 右边表封装类
   */
  private TTable rightTable;
  /**
   * 左边表封装类
   */
  private TTable leftTable;
  /**
   * 多表join时左边的join
   */
  private TJoin leftJoin;
  private TJoinOnClause onClause;
  private String type; //type可设计成枚举，定义为join的类型，如:left_out_join

  public TJoin() {
  }

  public TTable initRightTable() {
    this.rightTable = new TTable();
    return this.rightTable;
  }

  public TTable initLeftTable() {
    this.leftTable = new TTable();
    return this.leftTable;
  }

  public TJoin initLeftJoin() {
    this.leftJoin = new TJoin();
    return this.leftJoin;
  }

  public TTable getRightTable() {
    return rightTable;
  }

  public void setRightTable(TTable rightTable) {
    this.rightTable = rightTable;
  }

  public TTable getLeftTable() {
    return leftTable;
  }

  public void setLeftTable(TTable leftTable) {
    this.leftTable = leftTable;
  }

  public TJoin getLeftJoin() {
    return leftJoin;
  }

  public void setLeftJoin(TJoin leftJoin) {
    this.leftJoin = leftJoin;
  }

  public TJoinOnClause getOnClause() {
    return onClause;
  }

  public void setOnClause(TJoinOnClause onClause) {
    this.onClause = onClause;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void fun_TOK_JOIN(ASTNode node_TTable) {
    ArrayList<Node> child_join = node_TTable.getChildren();
    if (null != child_join && child_join.size() == 2) {
      join_child2(child_join, this);
    } else if (null != child_join && child_join.size() == 3) {
      join_child2(child_join, this);
      //todo 这里和上面的情况基本一致，只是多了on条件的解析，最后将与where一起解析

    }
  }

  private void join_child2(ArrayList<Node> child_join, TJoin joins) {
    ASTNode leftNode = (ASTNode) child_join.get(0);
    ASTNode rightNode = (ASTNode) child_join.get(1);
    switch (leftNode.getToken().getType()) {
      case HiveParser.TOK_TABREF:
        joins.initLeftTable().fun_TOK_TABREF(leftNode);
        break;
      case HiveParser.TOK_SUBQUERY:
        joins.initLeftTable().fun_TOK_SubQUERY(leftNode);
        break;
      case HiveParser.TOK_JOIN:
        joins.initLeftJoin().fun_TOK_JOIN(leftNode);
        break;
      case HiveParser.TOK_LEFTOUTERJOIN:
        joins.initLeftJoin().fun_TOK_JOIN(leftNode);
        break;
      case HiveParser.TOK_LEFTSEMIJOIN:
        joins.initLeftJoin().fun_TOK_JOIN(leftNode);
        break;
      case HiveParser.TOK_RIGHTOUTERJOIN:
        joins.initLeftJoin().fun_TOK_JOIN(leftNode);
        break;
    }
    switch (rightNode.getToken().getType()) {
      case HiveParser.TOK_TABREF:
        joins.initRightTable().fun_TOK_TABREF(rightNode);
        break;
      case HiveParser.TOK_SUBQUERY:
        joins.initRightTable().fun_TOK_SubQUERY(rightNode);
        break;
    }
  }


}

package parser.statement.select;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;

import java.util.ArrayList;

public class ANDORClause {
  private TLeftExpression leftExpression;
  private TRightExpression rightExpression;
  public ANDORClause() {
  }

  public TLeftExpression initLeftExpression() {
    this.leftExpression = new TLeftExpression();
    return this.leftExpression;
  }

  public TRightExpression initRightExpression() {
    this.rightExpression = new TRightExpression();
    return this.rightExpression;
  }

  public TLeftExpression getLeftExpression() {
    return leftExpression;
  }

  public void setLeftExpression(TLeftExpression leftExpression) {
    this.leftExpression = leftExpression;
  }

  public TRightExpression getRightExpression() {
    return rightExpression;
  }

  public void setRightExpression(TRightExpression rightExpression) {
    this.rightExpression = rightExpression;
  }

  public void fun_LR_andOrClause(ASTNode node_switch) {
    ArrayList<Node> child_orNode = node_switch.getChildren(); // size == 2 Theoretically
    if (child_orNode.size() == 2) {
      ASTNode left_child = (ASTNode) child_orNode.get(0);
      ASTNode right_child = (ASTNode) child_orNode.get(1);
      this.initLeftExpression().fun_left_expression(left_child);
      this.initRightExpression().fun_right_expression(right_child);
    }
  }
}

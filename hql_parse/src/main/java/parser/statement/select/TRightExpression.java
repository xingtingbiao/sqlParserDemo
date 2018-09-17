package parser.statement.select;

import org.apache.hadoop.hive.ql.parse.ASTNode;

public class TRightExpression extends TWhereClause{
  public TRightExpression() {
    super();
  }

  public void fun_right_expression(ASTNode right_child) {
    super.fun_andOr_node(right_child);
  }
}

package parser.statement.select;

import org.apache.hadoop.hive.ql.parse.ASTNode;

public class TLeftExpression extends TWhereClause {
  public TLeftExpression() {
    super();
  }

  public void fun_left_expression(ASTNode left_child) {
    super.fun_andOr_node(left_child);
  }
}

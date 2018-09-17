package parser.base;

import org.apache.hadoop.hive.ql.parse.ASTNode;

public class BaseUtil {
  public static boolean childIsNil(ASTNode node) {
    return node.getChildren() == null || node.getChildren().isEmpty();
  }
}

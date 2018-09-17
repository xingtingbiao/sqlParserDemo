package parser.base;

import org.apache.hadoop.hive.ql.parse.ASTNode;

public interface BaseStatement {

  void doParse(ASTNode node);
}

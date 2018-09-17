package main;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import parser.statement.TCustomSqlStatement;

public class ParseASTNode {

  public ParseASTNode() {
  }

  /**
   * 将语法树ASTNode根据自定义的数据结构TCustomSqlStatement进行遍历解析
   * @param val 传入的具体sql
   * @return TCustomSqlStatement对象
   * @throws ParseException 解析异常
   */
  public static TCustomSqlStatement parseNode(String val) throws ParseException {
    ASTNode node = new ParseDriver().parse(val);
    return TCustomSqlStatement.parseASTNode(node);
  }
}

package parser.statement.drop;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.statement.TCustomSqlStatement;

import java.util.ArrayList;

public class TDropSqlStatement extends TCustomSqlStatement {
  private boolean ifExists;

  public TDropSqlStatement() {
    super();
  }

  public TDropSqlStatement(int type) {
    super(type);
  }

  @Override
  public void doParse(ASTNode node) {
    ArrayList<Node> child_alter = node.getChildren();
    for (int i = 0; i < child_alter.size(); i++) {
      this.fun_drop((ASTNode) child_alter.get(i));
    }
  }

  private void fun_drop(ASTNode node) {
    switch (node.getToken().getType()) {
      case HiveParser.TOK_TABNAME:
        this.initTable().fun_TOK_TABNAME(node);
        break;
      case HiveParser.TOK_IFEXISTS:
        this.ifExists = true;
        break;
      default:
        break;
    }
  }

  public boolean isIfExists() {
    return ifExists;
  }

  public void setIfExists(boolean ifExists) {
    this.ifExists = ifExists;
  }
}

package parser.statement.others;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.statement.TCustomSqlStatement;

import java.util.ArrayList;

public class TSetSqlStatement extends TCustomSqlStatement {

  public TSetSqlStatement() {
    super();
  }

  public TSetSqlStatement(int type) {
    super(type);
  }

  private HiveUserRole userRole;

  public HiveUserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(HiveUserRole userRole) {
    this.userRole = userRole;
  }

  private HiveUserRole initUserRole() {
    this.userRole = new HiveUserRole();
    return this.userRole;
  }

  @Override
  public void doParse(ASTNode node) {
    if (!childIsNil(node)) {
      ArrayList<Node> child_set = node.getChildren();
      if (((ASTNode) child_set.get(0)).getToken().getType() == HiveParser.Identifier) {
        CommonToken token = (CommonToken) ((ASTNode) child_set.get(0)).getToken();
        this.initUserRole().initToken(token);
      }
    }
  }

  public static boolean childIsNil(ASTNode node) {
    return node.getChildren() == null || node.getChildren().isEmpty();
  }

}

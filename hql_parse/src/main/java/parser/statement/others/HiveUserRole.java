package parser.statement.others;

import org.antlr.runtime.CommonToken;
import parser.base.StateToken;

public class HiveUserRole extends StateToken{
  private String role;

  public HiveUserRole() {
    super();
  }

  @Override
  public void initToken(CommonToken token) {
    super.initToken(token);
    this.role = token.getText();
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}

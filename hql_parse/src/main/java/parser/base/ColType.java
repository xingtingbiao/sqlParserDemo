package parser.base;

import org.antlr.runtime.CommonToken;

public class ColType extends StateToken {
  private String name;

  public ColType() {
    super();
  }

  @Override
  public void initToken(CommonToken token) {
    super.initToken(token);
    this.name = token.getText();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}

package parser.base;

import org.antlr.runtime.CommonToken;

public class ColName extends StateToken {
  private String name;

  public ColName() {
    super();
  }

  public ColName(String name) {
    super();
    this.name = name;
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

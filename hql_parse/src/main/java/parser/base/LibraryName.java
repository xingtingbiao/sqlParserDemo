package parser.base;

import org.antlr.runtime.CommonToken;

public class LibraryName extends StateToken{
  private String name;

  public LibraryName() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void initToken(CommonToken token) {
    super.initToken(token);
    this.name = token.getText();
  }
}

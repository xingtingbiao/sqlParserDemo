package parser.statement.select;

import org.antlr.runtime.CommonToken;
import parser.base.StateToken;

public class TableComment extends StateToken{
  private String comment;

  public TableComment() {
    super();
  }

  @Override
  public void initToken(CommonToken token) {
    super.initToken(token);
    this.comment = token.getText();
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}

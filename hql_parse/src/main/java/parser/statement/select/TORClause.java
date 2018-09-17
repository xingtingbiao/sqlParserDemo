package parser.statement.select;

public class TORClause extends ANDORClause {

  private String type = "or";

  public TORClause() {
    super();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

}

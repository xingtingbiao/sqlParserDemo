package parser.statement.select;

public class TANDClause extends ANDORClause {
  private String type = "and";

  public TANDClause() {
    super();
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

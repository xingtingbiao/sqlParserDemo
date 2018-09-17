package parser.statement.function;

import parser.base.TLiteral;

public class WhenValue {
  private TLiteral literal; //字面量
  private parser.statement.function.TFunctionCall TFunctionCall;

  public WhenValue() {
    super();
  }

  public TLiteral initLiteral() {
    this.literal = new TLiteral();
    return this.literal;
  }

  public TLiteral getLiteral() {
    return literal;
  }

  public void setLiteral(TLiteral literal) {
    this.literal = literal;
  }

  public TFunctionCall getTFunctionCall() {
    return TFunctionCall;
  }

  public void setTFunctionCall(TFunctionCall TFunctionCall) {
    this.TFunctionCall = TFunctionCall;
  }
}

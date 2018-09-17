package parser.base;

import org.antlr.runtime.CommonToken;

import java.util.regex.Pattern;

public class TLiteral extends StateToken {
  private String stringLiteral;
  private String numberLiteral; //由于数值类型也有多种:int float, double, 所以统一字符串处理, 变量名只做区分

  public TLiteral() {
    super();
  }

  @Override
  public void initToken(CommonToken token) {
    super.initToken(token);
  }

  public void initString(CommonToken token) {
    super.initToken(token);
    this.stringLiteral = this.getText();
  }

  public void initNumber(CommonToken token) {
    super.initToken(token);
    this.numberLiteral = this.getText();
  }

  private boolean isInteger(String str) {
    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
    return pattern.matcher(str).matches();
  }

  public String getStringLiteral() {
    return stringLiteral;
  }

  public void setStringLiteral(String stringLiteral) {
    this.stringLiteral = stringLiteral;
  }

  public String getNumberLiteral() {
    return numberLiteral;
  }

  public void setNumberLiteral(String numberLiteral) {
    this.numberLiteral = numberLiteral;
  }
}

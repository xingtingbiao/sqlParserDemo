package parser.base;

import org.antlr.runtime.CommonToken;

public class StateToken {
  private String input; //可做优化
  private String text;
  private int start;
  private int stop;
  private String route;

  public StateToken() {
  }

  public void initToken(CommonToken token) {
    if (null != token.getInputStream()) this.input = token.getInputStream().toString();
    this.text = token.getText();
    this.start = token.getStartIndex();
    this.stop = token.getStopIndex();
  }

  public String getInput() {
    return input;
  }

  public void setInput(String input) {
    this.input = input;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getStop() {
    return stop;
  }

  public void setStop(int stop) {
    this.stop = stop;
  }

  public String getRoute() {
    return route;
  }

  public void setRoute(String route) {
    this.route = route;
  }
}

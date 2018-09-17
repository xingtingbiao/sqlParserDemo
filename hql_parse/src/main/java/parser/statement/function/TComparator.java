package parser.statement.function;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.StateToken;
import parser.base.TLiteral;
import parser.base.TResultColumn;

import java.util.ArrayList;

public class TComparator extends StateToken {
  private TFunctionCall leftFC;
  private TResultColumn leftRC;
  private TFunctionCall rightFC;
  private TLiteral rightTL;
  private String type;

  public TComparator() {
    super();
  }

  @Override
  public void initToken(CommonToken token) {
    super.initToken(token);
    this.type = token.getText();
  }

  public TFunctionCall initLeftFC() {
    this.leftFC = new TFunctionCall();
    return this.leftFC;
  }

  public TResultColumn initLeftRC() {
    this.leftRC = new TResultColumn();
    return this.leftRC;
  }

  public TFunctionCall initRightFC(){
    this.rightFC = new TFunctionCall();
    return this.rightFC;
  }

  public TLiteral initRightTL() {
    this.rightTL = new TLiteral();
    return this.rightTL;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public TFunctionCall getLeftFC() {
    return leftFC;
  }

  public void setLeftFC(TFunctionCall leftFC) {
    this.leftFC = leftFC;
  }

  public TResultColumn getLeftRC() {
    return leftRC;
  }

  public void setLeftRC(TResultColumn leftRC) {
    this.leftRC = leftRC;
  }

  public TFunctionCall getRightFC() {
    return rightFC;
  }

  public void setRightFC(TFunctionCall rightFC) {
    this.rightFC = rightFC;
  }

  public TLiteral getRightTL() {
    return rightTL;
  }

  public void setRightTL(TLiteral rightTL) {
    this.rightTL = rightTL;
  }

  public void fun_comparator(ASTNode node_switch) {
    this.initToken((CommonToken) node_switch.getToken());
    ArrayList<Node> child_node_cp = node_switch.getChildren(); // size == 2 Theoretically
    if (child_node_cp.size() == 2) {
      ASTNode node_left = (ASTNode) child_node_cp.get(0);
      ASTNode node_right = (ASTNode) child_node_cp.get(1);
      this.fun_cp_left(node_left);
      this.fun_cp_right(node_right);
    }
  }

  private void fun_cp_left(ASTNode node_left) {
    switch (node_left.getToken().getType()) {
      case HiveParser.TOK_TABLE_OR_COL:
        this.initLeftRC().fun_TOK_TABLE_OR_COL(node_left);
        break;
      case HiveParser.DOT:
        this.initLeftRC().fun_col_DOT(node_left);
        break;
      case HiveParser.TOK_FUNCTION: //between and ...
        this.initLeftFC().fun_TOK_FUNCTION(node_left);
        break;
      // ...以后还有其他情况直接追加case
    }
  }

  private void fun_cp_right(ASTNode node_right) {
    switch (node_right.getToken().getType()) {
      case HiveParser.TOK_FUNCTION:  //函数内部嵌套函数
        this.initRightFC().fun_TOK_FUNCTION(node_right);
        break;
      case HiveParser.Number:
      case HiveParser.NumberLiteral:
        this.initRightTL().initNumber((CommonToken) node_right.getToken());
        break;
      case HiveParser.StringLiteral:
        this.initRightTL().initString((CommonToken) node_right.getToken());
        break;
      // ...以后还有其他情况直接追加case
    }
  }

}

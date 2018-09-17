package parser.base;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;


public class TArrayExp {
  private TArrayExp tArrayExp;
  private TResultColumn column;
  private TLiteral index;

  public TArrayExp() {
  }

  public TArrayExp gettArrayExp() {
    return tArrayExp;
  }

  public void settArrayExp(TArrayExp tArrayExp) {
    this.tArrayExp = tArrayExp;
  }

  public TResultColumn getColumn() {
    return column;
  }

  public void setColumn(TResultColumn column) {
    this.column = column;
  }

  public TLiteral getIndex() {
    return index;
  }

  public void setIndex(TLiteral index) {
    this.index = index;
  }

  public boolean arrayExpIsNull() {
    return this.tArrayExp == null;
  }

  public TArrayExp initTArrayExp() {
    this.tArrayExp = new TArrayExp();
    return this.tArrayExp;
  }

  public TResultColumn initColumn() {
    this.column = new TResultColumn();
    return this.column;
  }

  public TLiteral initTLiteral() {
    this.index = new TLiteral();
    return this.index;
  }

  public void fun_lSquare(ASTNode node_col) {
    if (!BaseUtil.childIsNil(node_col)) {
      for (Node node : node_col.getChildren()) {
        dealArrayExp((ASTNode) node);
      }
    }
  }

  private void dealArrayExp(ASTNode node) {
    switch (node.getToken().getType()) {
      case HiveParser.LSQUARE:
        this.initTArrayExp().fun_lSquare(node);
        break;
      case HiveParser.TOK_TABLE_OR_COL:
        this.initColumn().fun_TOK_TABLE_OR_COL(node);
        break;
      case HiveParser.TOK_FUNCTION:
        this.initColumn().initFunctionCall().fun_TOK_FUNCTION(node);
        break;
      case HiveParser.Number:
      case HiveParser.NumberLiteral:
        this.initTLiteral().initNumber((CommonToken) node.getToken());
        break;
      default:
        break;
    }
  }

  public TArrayExp getAtomicArray() {
    if (arrayExpIsNull()) {
      return this;
    } else {
      return this.tArrayExp.getAtomicArray();
    }
  }

}

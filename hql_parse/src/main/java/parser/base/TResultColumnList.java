package parser.base;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.List;

public class TResultColumnList {

  private List<TResultColumn> colList = new ArrayList<>();

  public TResultColumnList() {
  }

  public final int size() {
    return this.colList.size();
  }

  public void removeResultColumn(int var1) {
    if (this.size() > var1) {
      this.colList.remove(var1);
    }
  }

  public void addColumn(TResultColumn column) {
    this.colList.add(column);
  }

  public TResultColumn getColumn(int var1) {
    return var1 < this.size() ? this.colList.get(var1) : null;
  }

  public List<TResultColumn> getColList() {
    return colList;
  }

  public void setColList(List<TResultColumn> colList) {
    this.colList = colList;
  }

  public void fun_TOK_SELECT(ASTNode node_SELECT) {
    if (!BaseUtil.childIsNil(node_SELECT)) {
      ArrayList<Node> child_SELECT = node_SELECT.getChildren();
      for (Node aChild_SELECT : child_SELECT) {
        ASTNode node_SELEXPR = (ASTNode) aChild_SELECT;
        TResultColumn resultColumn = new TResultColumn();
        resultColumn.fun_TOK_SELEXPR(node_SELEXPR);
        this.addColumn(resultColumn);
      }
    }
  }

  public void fun_TOK_TABCOLNAME(ASTNode node) {
    if (!BaseUtil.childIsNil(node)) {
      ArrayList<Node> childes = node.getChildren();
      for (Node childe : childes) {
        ASTNode node_col = (ASTNode) childe;
        switch (node_col.getToken().getType()) {
          case HiveParser.Identifier:
            TResultColumn column = new TResultColumn();
            column.fun_col_name(node_col);
            this.addColumn(column);
            break;
          default:
            break;
        }
      }
    }
  }

  public void fun_TOK_TABCOLLIST(ASTNode node_colList) {
    if (!BaseUtil.childIsNil(node_colList)) {
      for (Node node : node_colList.getChildren()) {
        this.fun_TOK_TABCOL(((ASTNode) node));
      }
    }
  }

  private void fun_TOK_TABCOL(ASTNode node) {
    if (node.getToken().getType() == HiveParser.TOK_TABCOL) {
      this.fun_TOK_TABCOLNAME(node);
    }
  }
}

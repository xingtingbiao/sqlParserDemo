package parser.statement.select;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.Alias;
import parser.base.TResultColumn;

import java.util.ArrayList;
import java.util.List;

public class TLateralView {
  private Alias tabAlias;  //别名
  private TResultColumn resultColumn;

  public TLateralView() {
  }

  public void initResultColumn() {
    if (null == this.resultColumn) this.resultColumn = new TResultColumn();
  }

  public void fun_lateralView_child(ASTNode node_lv, TSelectSqlStatement selectSqlStatement) {
    List<Node> child_lv = node_lv.getChildren();
    if (null != child_lv && child_lv.size() > 0) {
      for (Node child : child_lv) {
        ASTNode node = (ASTNode) child;
        switch (node.getToken().getType()) {
          case HiveParser.TOK_SELECT:
            node = (ASTNode) node.getChildren().get(0);
            if (node.getToken().getType() == HiveParser.TOK_SELEXPR) {
              this.fun_TOK_SELEXPR(node);
            }
            break;
          case HiveParser.TOK_SUBQUERY:
            selectSqlStatement.initTable().fun_TOK_SubQUERY(node);
            break;
          default:
            break;
        }
      }
    }
  }

  private void fun_TOK_SELEXPR(ASTNode node) {
    ArrayList<Node> children = node.getChildren();
    if (null != children && children.size() > 0) {
      for (Node child : children) {
        ASTNode child_n = (ASTNode) child;
        this.fun_selexpr_child(child_n);
      }
    }
  }

  private void fun_selexpr_child(ASTNode node) {
    CommonToken token;
    switch (node.getToken().getType()) {
      case HiveParser.TOK_FUNCTION:
        this.initResultColumn();
        this.resultColumn.initFunctionCall().fun_TOK_FUNCTION(node);
        break;
      case HiveParser.Identifier:
        this.initResultColumn();
        token = (CommonToken) node.getToken();
        this.resultColumn.initAlias().initToken(token);
        break;
      case HiveParser.TOK_TABALIAS:
        ArrayList<Node> children = node.getChildren();
        if (null != children && children.size() > 0) {
          ASTNode astNode = (ASTNode) children.get(0);
          if (astNode.getToken().getType() == HiveParser.Identifier) {
            token = (CommonToken) astNode.getToken();
            this.initTabAlias().initToken(token);
          }
        }
        break;
      default:
        break;
    }
  }

  private Alias initTabAlias() {
    this.tabAlias = new Alias();
    return this.tabAlias;
  }

  public Alias getTabAlias() {
    return tabAlias;
  }

  public void setTabAlias(Alias tabAlias) {
    this.tabAlias = tabAlias;
  }

  public TResultColumn getResultColumn() {
    return resultColumn;
  }

  public void setResultColumn(TResultColumn resultColumn) {
    this.resultColumn = resultColumn;
  }
}

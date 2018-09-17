package parser.statement.select;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.List;

public class TCTEExpression {
  private List<TTable> cteTables;

  public TCTEExpression() {
  }

  public List<TTable> initCteTables() {
    this.cteTables = new ArrayList<TTable>();
    return this.cteTables;
  }

  public List<TTable> getCteTables() {
    return cteTables;
  }

  public void setCteTables(List<TTable> cteTables) {
    this.cteTables = cteTables;
  }

  public void fun_deal_cte(ASTNode node_cte) {
    List<Node> childes = node_cte.getChildren();
    for (int i = 0; i < childes.size(); i++) {
      ASTNode child_cte = (ASTNode) childes.get(i);
      this.fun_child_cte(child_cte);
    }
  }

  private void fun_child_cte(ASTNode child_cte) {
    switch (child_cte.getToken().getType()) {
      case HiveParser.TOK_SUBQUERY:
        this.fun_cte_subQuery(child_cte);
        break;
      default:
        break;
    }
  }

  private void fun_cte_subQuery(ASTNode node_sub) {
    if (null == this.cteTables) {
      this.cteTables = new ArrayList<TTable>();
    }
    TTable tTable = new TTable();
    tTable.fun_TOK_SubQUERY(node_sub);
    this.cteTables.add(tTable);
  }
}

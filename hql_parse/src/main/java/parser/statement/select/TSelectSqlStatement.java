package parser.statement.select;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.statement.TCustomSqlStatement;

import java.util.ArrayList;
import java.util.List;

public class TSelectSqlStatement extends TCustomSqlStatement {
  /**
   * hive sql中的cte语法表达式
   */
  private TCTEExpression cteExpression;
  /**
   * sql中的order by语法
   */
  private TOrderBy orderBy;
  /**
   * sql中的group by语法
   */
  private TGroupBy groupBy;
  /**
   * hive sql中的TLateralView语法
   */
  private List<TLateralView> lateralViews;
  // left and right ==> union all

  public TSelectSqlStatement() {
    super();
  }

  public TSelectSqlStatement(int type) {
    super(type);
  }

  @Override
  public void doParse(ASTNode node_q) {
    if (!childIsNil(node_q)) {
      List<Node> childes = node_q.getChildren();
      for (Node child : childes) {
        ASTNode child_q = (ASTNode) child;
        this.fun_child_query(child_q);
      }
    }
  }

  /**
   * 处理TOK_QUERY语法树几点的所有子节点的方法
   * @param child_q TOK_QUERY语法树节点(ASTNode)的子节点
   */
  private void fun_child_query(ASTNode child_q) {
    switch (child_q.getToken().getType()) {
      case HiveParser.TOK_CTE: //with as语法  CTE表达式
        this.fun_TOK_CTE(child_q);
        break;
      case HiveParser.TOK_FROM:
        this.fun_TOK_FROM(child_q);
        break;
      case HiveParser.TOK_INSERT: //分区表分批多次插入时的考虑
        if (null == this.getColumnList()) this.fun_TOK_INSERT(child_q);
        break;
      default:
        break;
    }
  }

  /**
   * 处理TOK_CTE语法树节点的方法
   * @param node_cte cte语法树节点(ASTNode)
   */
  private void fun_TOK_CTE(ASTNode node_cte) {
    if (!childIsNil(node_cte)) {
      this.initCteExpression().fun_deal_cte(node_cte);
    }
  }

  /**
   * 处理TOK_FROM语法树节点的方法
   * @param node_from TOK_FROM语法树节点(ASTNode)
   */
  private void fun_TOK_FROM(ASTNode node_from) {
    if (!childIsNil(node_from)) {
      List<Node> child_from = node_from.getChildren();
      ASTNode node_TTable = (ASTNode) child_from.get(0);
      if (!childIsNil(node_TTable)) {
        fun_TTable(node_TTable);
      }
    }
  }

  private void fun_TOK_INSERT(ASTNode node_insert) {
    if (!childIsNil(node_insert)) {
      List<Node> child_insert = node_insert.getChildren();
      for (Node aChild_insert : child_insert) {
        ASTNode node = (ASTNode) aChild_insert;
        this.fun_TInsert(node);
      }
    }
  }

  private void fun_TTable(ASTNode node_TTable) {
    switch (node_TTable.getToken().getType()) {
      case HiveParser.TOK_LATERAL_VIEW:
      case HiveParser.TOK_LATERAL_VIEW_OUTER:
        this.fun_lateralView(node_TTable);
        break;
      case HiveParser.TOK_TABREF:
        this.initTable().fun_TOK_TABREF(node_TTable);
        break;
      case HiveParser.TOK_SUBQUERY:
        this.initTable().fun_TOK_SubQUERY(node_TTable);
        break;
      case HiveParser.TOK_JOIN:
      case HiveParser.TOK_LEFTOUTERJOIN:
      case HiveParser.TOK_LEFTSEMIJOIN:
      case HiveParser.TOK_RIGHTOUTERJOIN:
      case HiveParser.TOK_FULLOUTERJOIN:
        this.initJoins().fun_TOK_JOIN(node_TTable);
        break;
      default:
        break;
    }
  }
  public void initLateralViews() {
    if (this.lateralViews == null) this.lateralViews = new ArrayList<>();
  }

  private void fun_lateralView(ASTNode node_lv) {
    this.initLateralViews();
    TLateralView lateralView = new TLateralView();
    lateralView.fun_lateralView_child(node_lv, this);
    this.lateralViews.add(lateralView);
  }

  public TOrderBy getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(TOrderBy orderBy) {
    this.orderBy = orderBy;
  }

  public TGroupBy getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(TGroupBy groupBy) {
    this.groupBy = groupBy;
  }

  public List<TLateralView> getLateralViews() {
    return lateralViews;
  }

  public void setLateralViews(List<TLateralView> lateralViews) {
    this.lateralViews = lateralViews;
  }

  private void fun_TInsert(ASTNode node) {
    switch (node.getToken().getType()) {
      case HiveParser.TOK_DESTINATION:
        break;
      case HiveParser.TOK_SELECTDI:
//        //todo
//        break;
      case HiveParser.TOK_SELECT:
        this.initColumnList().fun_TOK_SELECT(node);
        break;
      case HiveParser.TOK_WHERE:
        this.initWhereClause().fun_TOK_WHERE(node);
        break;
      case HiveParser.TOK_GROUPBY: //maybe have others

        break;
      case HiveParser.TOK_HAVING:

        break;
      case HiveParser.TOK_ORDERBY:

        break;
      case HiveParser.TOK_LIMIT:

        break;
      default:
        break;
    }
  }

  public TCTEExpression initCteExpression() {
    this.cteExpression = new TCTEExpression();
    return this.cteExpression;
  }

  public TCTEExpression getCteExpression() {
    return cteExpression;
  }

  public void setCteExpression(TCTEExpression cteExpression) {
    this.cteExpression = cteExpression;
  }
}

package parser.statement.select;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.TypeOne;
import parser.statement.function.TComparator;
import parser.statement.function.TFunctionCall;

public class TWhereClause {
  private TComparator comparator;
  private TFunctionCall functionCall;
  private TORClause orClause;
  private TANDClause andClause;

  public TWhereClause() {
  }

  public TComparator initComparator() {
    this.comparator = new TComparator();
    return this.comparator;
  }

  public TFunctionCall initFunctionCall() {
    this.functionCall = new TFunctionCall();
    return this.functionCall;
  }

  public TORClause initOrClause() {
    this.orClause = new TORClause();
    return this.orClause;
  }

  public TANDClause initAndClause() {
    this.andClause = new TANDClause();
    return this.andClause;
  }

  public TComparator getComparator() {
    return comparator;
  }

  public void setComparator(TComparator comparator) {
    this.comparator = comparator;
  }

  public TFunctionCall getFunctionCall() {
    return functionCall;
  }

  public void setFunctionCall(TFunctionCall functionCall) {
    this.functionCall = functionCall;
  }

  public TORClause getOrClause() {
    return orClause;
  }

  public void setOrClause(TORClause orClause) {
    this.orClause = orClause;
  }

  public TANDClause getAndClause() {
    return andClause;
  }

  public void setAndClause(TANDClause andClause) {
    this.andClause = andClause;
  }

  public void fun_TOK_WHERE(ASTNode node) {
    ASTNode node_switch = (ASTNode) node.getChildren().get(0); // node_switch (=, function, and, or)
    this.fun_andOr_node(node_switch);
  }

  public void fun_andOr_node(ASTNode node_switch) {
    switch (node_switch.getToken().getType()) {
      case HiveParser.LESSTHAN: // "<"
        this.initComparator().fun_comparator(node_switch);
        break;
      case HiveParser.LESSTHANOREQUALTO: //"<="
        this.initComparator().fun_comparator(node_switch);
        break;
      case HiveParser.GREATERTHAN: // ">"
        this.initComparator().fun_comparator(node_switch);
        break;
      case HiveParser.GREATERTHANOREQUALTO: // ">="
        this.initComparator().fun_comparator(node_switch);
        break;
      case HiveParser.EQUAL: // "="
        this.initComparator().fun_comparator(node_switch);
        break;
      case HiveParser.NOTEQUAL: // "!=、<>"
        this.initComparator().fun_comparator(node_switch);
        break;
      case HiveParser.KW_LIKE: //"like"
        this.initComparator().fun_comparator(node_switch);
        break;
      case HiveParser.TOK_FUNCTION: //"between and ..."
        this.initFunctionCall().fun_TOK_FUNCTION(node_switch);
        break;
      case HiveParser.KW_OR: // "or"
        this.initOrClause().fun_LR_andOrClause(node_switch);
        break;
      case HiveParser.KW_AND: // "and"
        this.initAndClause().fun_LR_andOrClause(node_switch);
        break;
      //后面还有直接追加 case
    }
  }

  public TypeOne getNotNull() {
    if (this.comparator != null) return new TypeOne<>(this.comparator);
    if (this.functionCall != null) return new TypeOne<>(this.functionCall);
    if (this.orClause != null) return new TypeOne<>(this.orClause);
    if (this.andClause != null) return new TypeOne<>(this.andClause);
    return null;
  }
}

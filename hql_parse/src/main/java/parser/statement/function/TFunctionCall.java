package parser.statement.function;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.StateToken;

import java.util.ArrayList;
import java.util.List;

public class TFunctionCall extends StateToken {
  private String name;
  private List<TCaseWhenClause> caseWhenClauses;
  private TFunParams funParams;
  private String type;

  public TFunctionCall() {
    super();
  }

  @Override
  public void initToken(CommonToken token) {
    super.initToken(token);
    this.name = token.getText();
  }

  public List<TCaseWhenClause> initCaseWhenClauses() {
    this.caseWhenClauses = new ArrayList<>();
    return this.caseWhenClauses;
  }

  public TFunParams initFunParams() {
    this.funParams = new TFunParams();
    return this.funParams;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TCaseWhenClause> getCaseWhenClauses() {
    return caseWhenClauses;
  }

  public void setCaseWhenClauses(List<TCaseWhenClause> caseWhenClauses) {
    this.caseWhenClauses = caseWhenClauses;
  }

  public TFunParams getFunParams() {
    return funParams;
  }

  public void setFunParams(TFunParams funParams) {
    this.funParams = funParams;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void fun_TOK_FUNCTION(ASTNode node_col) {  //这里既然已经是功能节点，那么首先判断他的第一个子节点是什么,其他子节点则是他的参数
    ArrayList<Node> child_fun = node_col.getChildren();
    ASTNode firstChildNode_fun = (ASTNode) child_fun.get(0);
    switch (firstChildNode_fun.getToken().getType()) {
      //todo cast做类型转换时,以后如有其它类型,直接追加
      case HiveParser.TOK_STRING:
      case HiveParser.TOK_INT:
      case HiveParser.Identifier: //直接为函数名 between and 比较特殊
        fun_identifier_fun(firstChildNode_fun);  // todo 处理函数名，注意具体数据封装的类的继承关系
        if (child_fun.size() > 1) {
          child_fun.remove(0);
          this.initFunParams().fun_funParams(child_fun);
        }
        break;
      case HiveParser.KW_WHEN: // case when 特殊函数
        // todo 这里为了业务暂时先解析then 后面的字段, 但是存在case 后面只有字段没有判断条件, 出现多余的case后的字段(非检查字段),此时会出现误判
        // fun_KW_WHEN((ASTNode) firstChildNode_fun.getParent());
        fun_identifier_fun(firstChildNode_fun);  // todo 处理函数名，注意具体数据封装的类的继承关系
        if (child_fun.size() > 1) {
          child_fun.remove(0);
          this.initFunParams().fun_funParams(child_fun);
        }
        break;
      default:
        break;
    }
  }

  private void fun_identifier_fun(ASTNode firstChildNode_fun) {
    this.initToken((CommonToken) firstChildNode_fun.getToken());
  }

  private void fun_KW_WHEN(ASTNode node_caseFun) {
    //todo case when 后面再处理
  }
}

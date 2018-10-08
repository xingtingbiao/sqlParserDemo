package parser.statement.function;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.BaseUtil;
import parser.base.TLiteral;
import parser.base.TResultColumn;

import java.util.ArrayList;
import java.util.List;

public class TFunParams {
  private List<TFunctionCall> functionCalls;
  private List<TResultColumn> resultColumns;
  private List<TLiteral> literals;

  public TFunParams() {
  }

  public List<TFunctionCall> initFunctionCalls() {
    this.functionCalls = new ArrayList<>();
    return this.functionCalls;
  }

  public List<TResultColumn> initResultColumns() {
    this.resultColumns = new ArrayList<>();
    return this.resultColumns;
  }

  public List<TLiteral> initLiterals() {
    this.literals = new ArrayList<>();
    return this.literals;
  }

  public List<TFunctionCall> getFunctionCalls() {
    return functionCalls;
  }

  public void setFunctionCalls(List<TFunctionCall> functionCalls) {
    this.functionCalls = functionCalls;
  }

  public List<TResultColumn> getResultColumns() {
    return resultColumns;
  }

  public void setResultColumns(List<TResultColumn> resultColumns) {
    this.resultColumns = resultColumns;
  }

  public List<TLiteral> getLiterals() {
    return literals;
  }

  public void setLiterals(List<TLiteral> literals) {
    this.literals = literals;
  }

  public void fun_funParams(ArrayList<Node> child_fun) {
    for (int i = 0; i < child_fun.size(); i++) {
      ASTNode funParamNode = (ASTNode) child_fun.get(i);
      TResultColumn resultColumn;
      TFunctionCall functionCall;
      TLiteral literal;
      switch (funParamNode.getToken().getType()) {
        case HiveParser.TOK_TABLE_OR_COL: //函数内部直接就是字段
          resultColumn = new TResultColumn();
          resultColumn.fun_TOK_TABLE_OR_COL(funParamNode); //todo 注意数据具体封装时的子类关系
          this.addResultColumn(resultColumn);
          break;
        case HiveParser.DOT:
          resultColumn = new TResultColumn();
          resultColumn.fun_col_DOT(funParamNode); // todo 注意具体函数封装
          this.addResultColumn(resultColumn);
          break;
        case HiveParser.TOK_FUNCTION:  //函数内部嵌套函数
          functionCall = new TFunctionCall();
          functionCall.fun_TOK_FUNCTION(funParamNode); //todo 注意数据具体封装时的子类关系
          this.addFunctionCall(functionCall);
          break;
        case HiveParser.Number: //example count(1)
        case HiveParser.NumberLiteral:
          literal = new TLiteral();
          literal.initNumber((CommonToken) funParamNode.getToken());
          this.addLiterals(literal);
          break;
        case HiveParser.StringLiteral: //example concat('a', 'b')
          literal = new TLiteral();
          literal.initString((CommonToken) funParamNode.getToken());
          this.addLiterals(literal);
          break;
        default:
          if (!BaseUtil.childIsNil(funParamNode)) this.fun_funParams(funParamNode.getChildren());
          break;
        // ...后面如果还有其他类型, 直接追加case
      }
    }
  }

  private void addResultColumn(TResultColumn resultColumn) {
    if (null == this.resultColumns) {
      this.initResultColumns().add(resultColumn);
    } else {
      this.resultColumns.add(resultColumn);
    }
  }

  private void addFunctionCall(TFunctionCall functionCall) {
    if (null == this.functionCalls) {
      this.initFunctionCalls().add(functionCall);
    } else {
      this.functionCalls.add(functionCall);
    }
  }

  private void addLiterals(TLiteral literal) {
    if (null == this.literals) {
      this.initLiterals().add(literal);
    } else {
      this.literals.add(literal);
    }
  }
}

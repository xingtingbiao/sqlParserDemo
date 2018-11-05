package parser.base;

import org.antlr.runtime.CommonToken;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.statement.function.TFunctionCall;

import java.util.ArrayList;

public class TResultColumn {

  private ColName colName;
  private TNameOrAlias tNameOrAlias;
  private ColType colType;
  private ColComment colComment;
  private Alias alias;
  private TFunctionCall functionCall; //这里如果函数修饰字段, 理论上必须要有别名, 否则新字段名将不确定
  private boolean allColumn; //(*)example: select * from t
  private int sensitive; //是否敏感
  private TResultColumn sourceColumn; //敏感源字段
  private TArrayExp tArrayExp;

  public TResultColumn() {
  }

  public TResultColumn(boolean allColumn) {
    this.allColumn = allColumn;
  }

  public TResultColumn(ColName colName) {
    this.colName = colName;
  }

  public TResultColumn(ColName colName, TNameOrAlias tNameOrAlias) {
    this.colName = colName;
    this.tNameOrAlias = tNameOrAlias;
  }

  public TResultColumn(ColName colName, TNameOrAlias tNameOrAlias, ColType colType, ColComment colComment, Alias alias, TFunctionCall functionCall, boolean allColumn, int sensitive, TResultColumn sourceColumn, TArrayExp tArrayExp) {
    this.colName = colName;
    this.tNameOrAlias = tNameOrAlias;
    this.colType = colType;
    this.colComment = colComment;
    this.alias = alias;
    this.functionCall = functionCall;
    this.allColumn = allColumn;
    this.sensitive = sensitive;
    this.sourceColumn = sourceColumn;
    this.tArrayExp = tArrayExp;
  }

  public ColName initColName() {
    this.colName = new ColName();
    return this.colName;
  }

  public Alias initAlias() {
    this.alias = new Alias();
    return this.alias;
  }

  public TNameOrAlias initTNameOrAlias() {
    this.tNameOrAlias = new TNameOrAlias();
    return this.tNameOrAlias;
  }

  public TFunctionCall initFunctionCall() {
    this.functionCall = new TFunctionCall();
    return this.functionCall;
  }

  public TArrayExp initTArrayExp() {
    this.tArrayExp = new TArrayExp();
    return this.tArrayExp;
  }

  public ColName getColName() {
    return colName;
  }

  public void setColName(ColName colName) {
    this.colName = colName;
  }

  public TNameOrAlias gettNameOrAlias() {
    return tNameOrAlias;
  }

  public void settNameOrAlias(TNameOrAlias tNameOrAlias) {
    this.tNameOrAlias = tNameOrAlias;
  }

  public ColType getColType() {
    return colType;
  }

  public void setColType(ColType colType) {
    this.colType = colType;
  }

  public ColComment getColComment() {
    return colComment;
  }

  public void setColComment(ColComment colComment) {
    this.colComment = colComment;
  }

  public Alias getAlias() {
    return alias;
  }

  public void setAlias(Alias alias) {
    this.alias = alias;
  }

  public TFunctionCall getFunctionCall() {
    return functionCall;
  }

  public void setFunctionCall(TFunctionCall functionCall) {
    this.functionCall = functionCall;
  }

  public boolean isAllColumn() {
    return allColumn;
  }

  public void setAllColumn(boolean allColumn) {
    this.allColumn = allColumn;
  }

  public int getSensitive() {
    return sensitive;
  }

  public void setSensitive(int sensitive) {
    this.sensitive = sensitive;
  }

  public TResultColumn getSourceColumn() {
    return sourceColumn;
  }

  public void setSourceColumn(TResultColumn sourceColumn) {
    this.sourceColumn = sourceColumn;
  }

  public TArrayExp gettArrayExp() {
    return tArrayExp;
  }

  public void settArrayExp(TArrayExp tArrayExp) {
    this.tArrayExp = tArrayExp;
  }

  public void fun_TOK_SELEXPR(ASTNode node_SELEXPR) {
    ArrayList<Node> child_SELEXPR = node_SELEXPR.getChildren(); // size == 1 or size == 2
    if (child_SELEXPR.size() == 1) {
      ASTNode node_col = (ASTNode) child_SELEXPR.get(0);
      fun_TColumn(node_col);
    } else if (child_SELEXPR.size() == 2){
      ASTNode node_col = (ASTNode) child_SELEXPR.get(0);
      fun_TColumn(node_col);
      ASTNode node_identifier_cn = (ASTNode) child_SELEXPR.get(1);
      if (node_identifier_cn.getToken().getType() == HiveParser.Identifier) {
        CommonToken token = (CommonToken) node_identifier_cn.getToken();
        // todo 具体封装列别名
        this.initAlias().initToken(token);
      }
    }
  }

  private void fun_TColumn(ASTNode node_col) {
    switch (node_col.getToken().getType()) {
      case HiveParser.TOK_ALLCOLREF: // select * from t 这里的*号情况
        //todo 封装boolean allColumn <= true
        this.fun_TOK_ALLCOLREF(node_col);
        break;
      case HiveParser.TOK_FUNCTION: //字段是由函数修饰的情况
        this.initFunctionCall().fun_TOK_FUNCTION(node_col);
        break;
      case HiveParser.TOK_FUNCTIONDI:
        //todo
        break;
      case HiveParser.TOK_FUNCTIONSTAR:
        // todo
        break;
      case HiveParser.TOK_TABLE_OR_COL:
        fun_TOK_TABLE_OR_COL(node_col);
        break;
      case HiveParser.TOK_SETCOLREF:  // union all 是前面默认查询时的情况(实际无字段)
        // 如果直接 select * from t1 union select * from t2... 这里做一层抽象 即转换成：
        // select _u1.* from (select * from t1 union select * from t2...)_u1
        this.allColumn = true;
        break;
      case HiveParser.DOT: //表名或者别名点.字段的情况
        fun_col_DOT(node_col);
        break;
      case HiveParser.LSQUARE: //针对出现split(col,',')[0]数组形式
        fun_col_LSQUARE(node_col);
        break;
      default:
        break;
    }
  }

  private void fun_TOK_ALLCOLREF(ASTNode node_col) {
    this.allColumn = true;
    if (!childIsNil(node_col)) {
      ArrayList<Node> child_allCol = node_col.getChildren();
      ASTNode node_tabName = (ASTNode) child_allCol.get(0);
      if (node_tabName.getToken().getType() == HiveParser.TOK_TABNAME) {
        ASTNode node_tNameOrAlias = (ASTNode) node_tabName.getChildren().get(0);
        if (node_tNameOrAlias.getToken().getType() == HiveParser.Identifier) {
          // todo 封装具体列的表名或表别名
          this.initTNameOrAlias().initToken((CommonToken) node_tNameOrAlias.getToken());
        }
      }
    }
  }

  private boolean childIsNil(ASTNode node) {
    return node.getChildren() == null || node.getChildren().isEmpty();
  }

  public void fun_TOK_TABLE_OR_COL(ASTNode node_col) {
    ASTNode node_colName = (ASTNode) node_col.getChildren().get(0);
    fun_col_name(node_colName);
  }

  public void fun_col_name(ASTNode node_colName) {
    if (node_colName.getToken().getType() == HiveParser.Identifier) {
      CommonToken token = (CommonToken) node_colName.getToken();
      //TODO 封装字段名
      this.initColName().initToken(token);
    }
  }

  public void fun_col_DOT(ASTNode node_col) {
    ArrayList<Node> child_DOT = node_col.getChildren();
    // TODO 这里在解析多个.的字段, 如a.b.c.d.col 现在的逻辑是解a, b; 也许需要解d, col
    ASTNode node_dotOrCol = (ASTNode) child_DOT.get(0);
    if (node_dotOrCol.getToken().getType() == HiveParser.DOT) {
      fun_col_DOT(node_dotOrCol);   //这里是按照原生对于多个DOT的解析语法进行封装
    } else if (node_dotOrCol.getToken().getType() == HiveParser.TOK_TABLE_OR_COL && child_DOT.size() == 2) {
      ASTNode node_colName = (ASTNode) child_DOT.get(1);
      fun_col_name(node_colName);
      ASTNode node_tNameOrAlias = (ASTNode) node_dotOrCol.getChildren().get(0);
      if (node_tNameOrAlias.getToken().getType() == HiveParser.Identifier) {
        CommonToken token = (CommonToken) node_tNameOrAlias.getToken();
        // todo 封装具体列的表名或表别名
        this.initTNameOrAlias().initToken(token);
      }
    }
  }

  private void fun_col_LSQUARE(ASTNode node_col) {
    this.initTArrayExp().fun_lSquare(node_col);
  }
}

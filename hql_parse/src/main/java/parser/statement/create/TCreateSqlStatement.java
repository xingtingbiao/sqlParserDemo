package parser.statement.create;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.base.Operation;
import parser.statement.TCustomSqlStatement;
import parser.statement.select.TSelectSqlStatement;

import java.util.ArrayList;

public class TCreateSqlStatement extends TCustomSqlStatement {
  private boolean external;
  private boolean ifNotExists;
  private TLikeTable likeTable;
  private TPartition partition;
  private TRowFormat rowFormat;
  private TFileFormat fileFormat;
  private TLocation location;
  private TProperties properties;
  private TSelectSqlStatement selectSqlStatement;

  public TCreateSqlStatement() {
    super();
  }

  public TCreateSqlStatement(int type) {
    super(type);
  }

  public TSelectSqlStatement initSelectSqlStatement() {
    this.selectSqlStatement = new TSelectSqlStatement(Operation.SELECT);
    return this.selectSqlStatement;
  }

  public boolean isExternal() {
    return external;
  }

  public void setExternal(boolean external) {
    this.external = external;
  }

  public boolean isIfNotExists() {
    return ifNotExists;
  }

  public void setIfNotExists(boolean ifNotExists) {
    this.ifNotExists = ifNotExists;
  }

  public TLikeTable getLikeTable() {
    return likeTable;
  }

  public void setLikeTable(TLikeTable likeTable) {
    this.likeTable = likeTable;
  }

  public TPartition getPartition() {
    return partition;
  }

  public void setPartition(TPartition partition) {
    this.partition = partition;
  }

  public TRowFormat getRowFormat() {
    return rowFormat;
  }

  public void setRowFormat(TRowFormat rowFormat) {
    this.rowFormat = rowFormat;
  }

  public TFileFormat getFileFormat() {
    return fileFormat;
  }

  public void setFileFormat(TFileFormat fileFormat) {
    this.fileFormat = fileFormat;
  }

  public TLocation getLocation() {
    return location;
  }

  public void setLocation(TLocation location) {
    this.location = location;
  }

  public TProperties getProperties() {
    return properties;
  }

  public void setProperties(TProperties properties) {
    this.properties = properties;
  }

  public TSelectSqlStatement getSelectSqlStatement() {
    return selectSqlStatement;
  }

  public void setSelectSqlStatement(TSelectSqlStatement selectSqlStatement) {
    this.selectSqlStatement = selectSqlStatement;
  }

  @Override
  public void doParse(ASTNode node) {
    if (!childIsNil(node)) {
      ArrayList<Node> child_create = node.getChildren();
      for (int i = 0; i < child_create.size(); i++) {
        ASTNode node_create_child = (ASTNode) child_create.get(i);
        this.fun_TOK_createTable(node_create_child);
      }
    }
  }

  private void fun_TOK_createTable(ASTNode node_create_child) {
    switch (node_create_child.getToken().getType()) {
      case HiveParser.TOK_TABNAME:
        this.initTable().fun_TOK_TABNAME(node_create_child);
        break;
      case HiveParser.KW_EXTERNAL:

        break;
      case HiveParser.TOK_IFNOTEXISTS:
        this.ifNotExists = true;
        break;
      case HiveParser.TOK_LIKETABLE:
        //todo
        break;
      case HiveParser.TOK_TABCOLLIST:
        this.initColumnList().fun_TOK_TABCOLLIST(node_create_child);
        break;
      case HiveParser.TOK_TABLECOMMENT:

        break;
      case HiveParser.TOK_TABLEPARTCOLS:

        break;
      case HiveParser.TOK_TABLEROWFORMAT:

        break;
      case HiveParser.TOK_FILEFORMAT_GENERIC:

        break;
      case HiveParser.TOK_TABLELOCATION:

        break;
      case HiveParser.TOK_TABLEPROPERTY:
        break;
      case HiveParser.TOK_TABLEPROPERTIES:

        break;
      case HiveParser.TOK_QUERY:
        this.initSelectSqlStatement().doParse(node_create_child);
        break;
      default:
        break;
    }
  }

}

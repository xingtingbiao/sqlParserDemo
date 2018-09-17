package parser.statement.alter;

import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import parser.statement.TCustomSqlStatement;

import java.util.ArrayList;

public class TAlterSqlStatement extends TCustomSqlStatement {

  public TAlterSqlStatement() {
    super();
  }

  public TAlterSqlStatement(int type) {
    super(type);
  }

  @Override
  public void doParse(ASTNode node) {
    ArrayList<Node> child_alter = node.getChildren();
    for (Node aChild_alter : child_alter) {
      this.fun_alter((ASTNode) aChild_alter);
    }
  }

  private void fun_alter(ASTNode node) {
    switch (node.getToken().getType()) {
      case HiveParser.TOK_TABNAME:
        this.initTable().fun_TOK_TABNAME(node);
        break;
      case HiveParser.TOK_ALTERTABLE_ADDCOLS:
        
        break;
      case HiveParser.TOK_ALTERTABLE_ADDCONSTRAINT:

        break;
      case HiveParser.TOK_ALTERTABLE_ADDPARTS:

        break;
      case HiveParser.TOK_ALTERTABLE_ARCHIVE:

        break;
      case HiveParser.TOK_ALTERTABLE_BUCKETS:

        break;
      case HiveParser.TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION:

        break;
      case HiveParser.TOK_ALTERTABLE_CLUSTER_SORT:

        break;
      case HiveParser.TOK_ALTERTABLE_COMPACT:

        break;
      case HiveParser.TOK_ALTERTABLE_DROPCONSTRAINT:

        break;
      case HiveParser.TOK_ALTERTABLE_DROPPARTS:

        break;
      case HiveParser.TOK_ALTERTABLE_DROPPROPERTIES:

        break;
      case HiveParser.TOK_ALTERTABLE_EXCHANGEPARTITION:

        break;
      case HiveParser.TOK_ALTERTABLE_FILEFORMAT:

        break;
      case HiveParser.TOK_ALTERTABLE_LOCATION:

        break;
      case HiveParser.TOK_ALTERTABLE_MERGEFILES:

        break;
      case HiveParser.TOK_ALTERTABLE_PARTCOLTYPE:

        break;
      case HiveParser.TOK_ALTERTABLE_PROPERTIES:

        break;
      case HiveParser.TOK_ALTERTABLE_RENAME:

        break;
      case HiveParser.TOK_ALTERTABLE_RENAMECOL:

        break;
      case HiveParser.TOK_ALTERTABLE_RENAMEPART:

        break;
      case HiveParser.TOK_ALTERTABLE_REPLACECOLS:

        break;
      case HiveParser.TOK_ALTERTABLE_SERDEPROPERTIES:

        break;
      case HiveParser.TOK_ALTERTABLE_SERIALIZER:

        break;
      case HiveParser.TOK_ALTERTABLE_SKEWED:

        break;
      case HiveParser.TOK_ALTERTABLE_SKEWED_LOCATION:

        break;
      case HiveParser.TOK_ALTERTABLE_TOUCH:

        break;
      case HiveParser.TOK_ALTERTABLE_UNARCHIVE:

        break;
      case HiveParser.TOK_ALTERTABLE_UPDATECOLSTATS:

        break;
      case HiveParser.TOK_ALTERTABLE_UPDATESTATS:

        break;
      default:
        break;
    }
  }
}

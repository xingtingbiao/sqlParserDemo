package test_sqlparse;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.junit.Test;
import parser.statement.TCustomSqlStatement;

public class BugTest {

    @Test
    public void test001() throws ParseException {
        ParseDriver parse = new ParseDriver();
        ASTNode astNode = parse.parse(TestSqls.s1012);
        System.out.println(astNode.dump());
        TCustomSqlStatement statement = TCustomSqlStatement.parseASTNode(astNode);
        System.out.println(statement);
    }
}

package test_sqlparse;

import business.AutomaticRouting;
import business.BloodRelation;
import business.Business;
import business.entity.AutoRoute;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.junit.Test;
import parser.statement.TCustomSqlStatement;
import parser.statement.select.TSelectSqlStatement;
import parser.statement.select.TTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayTest {

  @Test
  public void test01() throws ParseException {
    ParseDriver parse = new ParseDriver();
    ASTNode astNode = parse.parse(TestSqls.route01);
    System.out.println(astNode.dump());
    TCustomSqlStatement statement = TCustomSqlStatement.parseASTNode(astNode);
//    BloodRelation bloodRelation = new BloodRelation((TSelectSqlStatement) statement);
//    bloodRelation.analysisBlood();
//    System.out.println(statement.toString());
  }

  @Test
  public void test03() throws ParseException {
    String s = new AutomaticRouting().autoRoute(TestSqls.route01, new AutoRoute("base1234", "table1234"));
    System.out.println(s);
  }

  @Test
  public void test02() throws ParseException {
    List<TTable> allTable = new Business().getAllTable(TestSqls.play03);
    System.out.println(allTable);
  }

  @Test
  public void test04() {
    Lock lock = new ReentrantLock();
    boolean b = lock.tryLock();
  }

  @Test
  public void mytest() throws ParseException {
    ParseDriver parseDriver = new ParseDriver();
    ASTNode node = parseDriver.parse("select t.id,t.name as n from (select id,name from base.user_table2 nn) t");
    TCustomSqlStatement statement = TCustomSqlStatement.parseASTNode(node);
    String dump = node.dump();
    System.out.println(dump);
  }

  @Test
  public void testAutoroute() throws ParseException {
    String s = "select t1.id, t1.name from (select b1.t2.id,b1.t2.name from b1.t2) t1";
    AutoRoute route = new AutoRoute("b1", "t2", "b1", "b2", "t3");
    String s1 = new AutomaticRouting().autoRoute(s, route);
    System.out.println(s1);
  }

  @Test
  public void test005() throws ParseException {
//    String s = "select a.name as name, b.class as class from student a join student b on a.id = b.id";
//    ParseDriver parse = new ParseDriver();
//    ASTNode astNode = parse.parse(s);
//    System.out.println(astNode.dump());
//    TCustomSqlStatement statement = TCustomSqlStatement.parseASTNode(astNode);
//    System.out.println(statement);
    List<TCustomSqlStatement> parentWithChild = new Business().getParentWithChild(TestSqls.s1010);
    System.out.println(parentWithChild);
  }
}

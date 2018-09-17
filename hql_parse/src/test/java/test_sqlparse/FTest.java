package test_sqlparse;

import business.Business;
import org.junit.Test;
import parser.base.TypeOne;
import parser.statement.select.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FTest {

  @Test
  public void test01() throws Exception {
    String abc = new TypeOne<>("abc").first;
    System.out.println(abc);
    List<TypeOne> typeOnes = new Business().getAtomics("select id from t");
    Object first = typeOnes.get(0).first;
    if (first instanceof TSelectSqlStatement) {
      System.out.println(true);
    }
  }

  @Test
  public void test02() throws Exception {
    String s = "col@#table";
    String s1 = "col table";
    int index = s.indexOf("@#");
    int index1 = s1.indexOf("@#");
    System.out.println(index);
    System.out.println(index1);
    System.out.println(s.substring(index, s.length()  ));
  }

  @Test
  public void test03() throws Exception {
    List<String> list = new ArrayList<>();
    list.add("zzz1");
    list.add("aaa2");
    list.add("bbb2");
    list.add("fff1");
    list.add("fff2");
    list.add("aaa1");
    list.add("bbb1");
    list.add("zzz2");
    //Map<String, String> zzz1 = list.stream().filter(x -> !x.equals("zzz1")).collect(Collectors.toMap(Function.identity(), Function.identity()));
    List<String> collect = list.stream().map(String::toUpperCase).collect(Collectors.toList());
    List<String> zzz1 = list.stream().filter(x -> x.equals("zzz1")).collect(Collectors.toList());
    System.out.println(collect);
    System.out.println(collect.toString());
  }

  @Test
  public void test04() throws Exception {
//    StringBuilder builder = new StringBuilder();
//    for (int i = 0; i < 100; i++) {
//      builder.append("hello ");
//    }
//    System.out.println(builder.toString());
    Map<Boolean, String> map = new HashMap<>();
    String s = map.get(true);
    System.out.println(s);
  }

  @Test
  public void test05() throws InterruptedException, ExecutionException {
    ExecutorService pool = Executors.newFixedThreadPool(2);
    Future<?> future = pool.submit(() -> {
      try {
        System.out.println("starting01");
        Thread.sleep(2000);
        System.out.println("starting");
      } catch (Exception e) {
        // e.printStackTrace();
      } finally {
        System.out.println("ended");
        // return "a";
      }
    });
    Thread.sleep(100);
    future.cancel(true);
    // System.out.println(s);
    pool.shutdown();
  }
}

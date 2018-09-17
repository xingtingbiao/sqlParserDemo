package util;

import exception.SQLFormatException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessUtil {
  private static final String split = ";";

  public static List<String> splitStatements(String val) throws SQLFormatException {
    List<String> valList = null;
    StringBuilder builder = new StringBuilder();
    String code1 = IdGenerator.createUUID();
    recursiveBuild(getMap(), builder, val, code1);
    String format = builder.toString();
    if (format.length() > 0) {
      valList = new ArrayList<>();
      String[] splits = format.split(split);
      for (String sql : splits) {
        sql = sql.replaceAll(code1, split);
        if (sql.trim().length() > 0) valList.add(sql);
      }
    }
    return valList;
  }

  private static void recursiveBuild(Map<Character, Character> charMap, StringBuilder builder, String valStr, String code1) throws SQLFormatException {
    String val2 = null;
    int index = (-1);
    Character key = null;
    Character value = null;
    Character escape = null; //处理转义符 "\"
    for (int i = 0; i < valStr.length(); i++) {
      key = valStr.charAt(i);
      value = charMap.get(key);
      if (i > 0) escape = valStr.charAt(i - 1);
      if (null != value && (escape == null || escape != '\\')) {
        index = i;
        break;
      }
    }
    if (index < 0) {
      builder.append(valStr);
    } else {
      builder.append(valStr.substring(0, index)); //截取到出现的第一个闭合的括号符的前面的字符串
      String end = valStr.substring(index, valStr.length()); // 截取出现的第一个闭合的括号符(包含此符号)的后面的所有字符的字符串

      Map<Integer, String> formatMap = getFormatMap(end, key, value, code1);
      builder.append(formatMap.get(0));
      val2 = formatMap.get(1);
    }
    if (null != val2 && val2.length() > 0) {
      recursiveBuild(charMap, builder, val2, code1);
    }
  }


  private static Map<Integer, String> getFormatMap(String end, Character key, Character value, String code1) throws SQLFormatException {
    Map<Integer, Character> map = new HashMap<Integer, Character>();
    Map<Integer, String> formatMap = new HashMap<Integer, String>();
    int leftCount = 0; // 统计[(<']的数量 表示可能在字段中出现的几种括号情况
    int endRight = 0;  //记录最后一个右括号的下标
    Character escape = null; //处理转义符 "\"
    for (int i = 0; i < end.length(); i++) {
      if (i > 0) escape = end.charAt(i - 1);
      if (end.charAt(i) == key && (escape == null || escape != '\\')) {  // 如果key为"'" 则左右一样，会出现无法找到对称的情况, 故特殊处理 &&后条件处理转义字符的
        leftCount += 1;
        map.put(leftCount, key);
        if (key == value && leftCount == 2) {
          map.clear();
          leftCount = 0;
        }
      } else if (end.charAt(i) == value && (escape == null || escape != '\\')) { // key 和value一样 那么这个条件将无法进去
        map.remove(leftCount);
        leftCount -= 1;
      }
      if (i != 0 && map.size() == 0) { // 这边通过map的size等于0或者leftCount等于0来判断 起始的(左括号有没有被最后一个右括号)消除掉   所以s的起始字符必须是(
        endRight = i;  // 如果一直走不进 map.size() == 0  则说明少一个或多个右括号)  要么在前面的语法解析过不去，要么这里再处理
        break;
      }
    }
    formatMap = backFormatMap(end, code1, formatMap, endRight);
    return formatMap;
  }

  private static Map<Integer, String> backFormatMap(String end, String code1, Map<Integer, String> formatMap, int endRight) throws SQLFormatException {
    if (endRight != 0) {
      String sub = end.substring(0, endRight + 1);
      if (sub.contains(split)) {
        String format = sub.replaceAll(split, code1); //code随机生成保证唯一性
        // format = format.replaceAll(" ", code2); //code随机生成保证唯一性
        formatMap.put(0, format);
      }else {
        formatMap.put(0, sub);
      }
      formatMap.put(1, end.substring(endRight + 1, end.length()));
    } else {
      throw new SQLFormatException("The syntax of the SQL statement is malformed (for example: {(,<} caused by such characters. Please check carefully!!"); // endRight == 0 代表 这里的map的长度没有清空,也就是左括号没有得到闭合,应该格式抛异常
    }
    return formatMap;
  }

  private static Map<Character, Character> getMap() {
    Map<Character, Character> map = new HashMap<>();
    map.put('(', ')');
    map.put('\'', '\'');
    map.put('\"', '\"');
    // map.put('<', '>');
    return map;
  }
}

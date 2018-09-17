package business;

import business.entity.AutoRoute;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import parser.base.LibraryName;
import parser.base.StateToken;
import parser.base.TableName;
import parser.statement.select.TTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AutomaticRouting {
  private ASTNode node;

  public AutomaticRouting() {
  }

  public ASTNode getNode() {
    return node;
  }

  public void setNode(ASTNode node) {
    this.node = node;
  }

  private ASTNode initAstNode(String val) throws ParseException {
    this.node = new ParseDriver().parse(val);
    return this.node;
  }

  /**
   * 自动路由库名和表名, 注意第二个参数是单个路由对象, 并不是一个集合, 即一次只能完成一个路由(即单表路由, 非多表路由)
   * @param sql   需要路由的sql字符串
   * @param route 设置路由的具体参数的类
   * @return 返回路由后的sql
   * @throws ParseException 解析异常
   */
  public String autoRoute(String sql, AutoRoute route) throws ParseException {
    ASTNode node = this.initAstNode(sql);
    List<TTable> physicalTables = new ArrayList<>();
    List<TTable> colTables = new ArrayList<>();
    this.calculateNamesFromNode(node, physicalTables, colTables);
    List<String> tabNames = physicalTables.stream().filter(x -> null != x.getTableName()).map(t -> t.getTableName().getName()).collect(Collectors.toList());
    colTables = colTables.stream().filter(x -> null != x.getTableName() && tabNames.contains(x.getTableName().getName())).collect(Collectors.toList());
    physicalTables.addAll(colTables);
    List<TTable> routeTables = backRouteTables(route, physicalTables);
    return this.routedSql(routeTables, sql);
  }

  /**
   * 遍历所有语法树节点
   * @param node 根节点即第一个节点(父节点)
   * @param physicalTables 一个所有物理TTable的装载集合
   * @param colTables 字段前缀可能的表名(表别名)和库名的装载TTable集合
   */
  private void calculateNamesFromNode(ASTNode node, List<TTable> physicalTables, List<TTable> colTables) {
    ArrayList<Node> children = null;
    if (null != node) {
      this.switchNames(node, physicalTables, colTables);
      children = node.getChildren();
    }
    if (null != children && children.size() > 0) {
      for (Node n : children) {
        calculateNamesFromNode((ASTNode) n, physicalTables, colTables);
      }
    }
  }

  /**
   * 重点：具体识别哪些语法词片包含库名和表名,暂时只发现TOK_TABNAME, TOK_TABLE_OR_COL这两个, 如果以后发现还有其他的追加case, 并参考两个handle_方法实现新的方法
   * @param node 遍历需要的语法树节点
   * @param physicalTables 一个所有物理TTable的装载集合(物理的意思前面已做解析)
   * @param colTables 字段前缀可能的表名(表别名)和库名的装载TTable集合
   */
  private void switchNames(ASTNode node, List<TTable> physicalTables, List<TTable> colTables) {
    Token token = node.getToken();
    if (null != token) {
      switch (token.getType()) {
        case HiveParser.TOK_TABNAME:
          this.handle_TOK_TABNAME(node, physicalTables);
          break;
        case HiveParser.TOK_TABLE_OR_COL:
          //todo  后续如果需要, 请仔细从语法树上仔细研究TOK_TABLE_OR_COL这个词片
          this.handle_TOK_TABLE_OR_COL(node, colTables);
          break;
        default:
          break;
      }
    }
  }

  private void handle_TOK_TABNAME(ASTNode node, List<TTable> physicalTables) {
    ArrayList<Node> children = node.getChildren();
    TTable table = null;
    if (null != children && children.size() > 0) {
      switch (children.size()) {
        case 1:
          table = new TTable();
          this.backTabName(table, (ASTNode) children.get(0));
          break;
        case 2:
          table = new TTable();
          this.backLibraryName(table, (ASTNode) children.get(0));
          this.backTabName(table, (ASTNode) children.get(1));
          break;
        default:
          break;
      }
    }
    if (null != table) physicalTables.add(table);
  }

  private void handle_TOK_TABLE_OR_COL(ASTNode node, List<TTable> colTables) {
    ArrayList<Node> tabOrCols = node.getChildren();
    TTable table = null;
    ASTNode lastOneDot = (ASTNode) node.getParent();
    ArrayList<Node> lastOneDotChildes = null;
    ASTNode lastTwoDot = null;
    if (null != lastOneDot) {
      lastOneDotChildes = lastOneDot.getChildren();
      lastTwoDot = (ASTNode) lastOneDot.getParent();
    }
    ASTNode tabOrCol = null;
    if (null != tabOrCols && tabOrCols.size() > 0) tabOrCol = (ASTNode) tabOrCols.get(0);
    if ((null != lastOneDot && lastOneDot.getToken().getType() == HiveParser.DOT && null != lastTwoDot && lastTwoDot.getToken().getType() == HiveParser.DOT)
        && null != lastOneDotChildes && lastOneDotChildes.size() > 1) {
      switch (lastOneDotChildes.size()) {
        case 2:
          table = new TTable();
          this.backLibraryName(table, tabOrCol);
          this.backTabName(table, (ASTNode) lastOneDotChildes.get(1));
          break;
        default:
          break;
      }
    } else if ((null != lastOneDot && lastOneDot.getToken().getType() == HiveParser.DOT && null != lastTwoDot && lastTwoDot.getToken().getType() != HiveParser.DOT)
        && null != lastOneDotChildes && lastOneDotChildes.size() > 1) {
      switch (lastOneDotChildes.size()) {
        case 2:
          table = new TTable();
          this.backTabName(table, tabOrCol);
          break;
        default:
          break;
      }
    }
    if (null != table) colTables.add(table);
  }

  /**
   * 根据给定的路由参数过滤所有物理表集合中匹配的物理表, 并返回
   * @param route 路由参数
   * @param physicalTables 所有的物理表集合
   * @return 返回符合路由参数的物理表集合
   */
  private List<TTable> backRouteTables(AutoRoute route, List<TTable> physicalTables) {
    List<TTable> routeTables = new ArrayList<>();
    for (TTable t : physicalTables) {
      LibraryName libraryName = t.getLibraryName();
      TableName tableName = t.getTableName();
      if (null != libraryName && null != tableName) {
        if (libraryName.getName().equalsIgnoreCase(route.getOriginalBaseName())
            && tableName.getName().equalsIgnoreCase(route.getOriginalTabName())) {
          t.setRoute(route);
          routeTables.add(t);
        }
      } else if (null != tableName && null != route.getCurrentBaseName() && !"".equals(route.getCurrentBaseName())
          && tableName.getName().equalsIgnoreCase(route.getOriginalTabName())) {
        t.setRoute(route);
        routeTables.add(t);
      }
    }
    return routeTables;
  }

  /**
   * 这里根据最后筛选完的物理表集合进行路由
   * @param physicalTables 所有的物理表集合
   * @param val 原始sql
   * @return 返回路由后的sql
   */
  private String routedSql(List<TTable> physicalTables, String val) {
    if (physicalTables.size() == 0) return val;
    List<StateToken> tokens = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    for (TTable table : physicalTables) {
      this.packageTokens(table, tokens);
    }
    tokens.sort(Comparator.comparingInt(StateToken::getStart));
    this.packageBuilder(builder, tokens, val);
    return builder.toString();
  }

  /**
   * 封装StateToken这个集合
   * @param table 物理表
   * @param tokens StateToken的list集合
   */
  private void packageTokens(TTable table, List<StateToken> tokens) {
    TableName tableName = table.getTableName();
    LibraryName libraryName = table.getLibraryName();
    if (null != tableName) {
      tableName.setRoute(table.getRoute().getRouteTabName());
      tokens.add(tableName);
    }
    if (null != libraryName) {
      libraryName.setRoute(table.getRoute().getRouteBaseName());
      tokens.add(libraryName);
    }
  }

  /**
   * 根据排序后的StateToken集合, 进行遍历替换, 生成新的sql
   * @param builder StringBuilder
   * @param tokens 排序后的StateToken的list集合(升序)
   * @param val 原始sql
   */
  private void packageBuilder(StringBuilder builder, List<StateToken> tokens, String val) {
    for (int i = 0; i < tokens.size(); i++) {
      if (i == 0) {
        builder.append(val, 0, tokens.get(i).getStart()).append(tokens.get(i).getRoute());
      } else {
        builder.append(val, tokens.get(i - 1).getStop() + 1, tokens.get(i).getStart()).append(tokens.get(i).getRoute());
      }
    }
    builder.append(val, tokens.get(tokens.size() - 1).getStop() + 1, val.length());
  }

  private void backLibraryName(TTable table, ASTNode node) {
    if (null != node && node.getToken().getType() == HiveParser.Identifier) {
      LibraryName libraryName = new LibraryName();
      libraryName.initToken((CommonToken) node.getToken());
      table.setLibraryName(libraryName);
    }
  }

  private void backTabName(TTable table, ASTNode node) {
    if (null != node && node.getToken().getType() == HiveParser.Identifier) {
      TableName tableName = new TableName();
      tableName.initToken((CommonToken) node.getToken());
      table.setTableName(tableName);
    }
  }

}

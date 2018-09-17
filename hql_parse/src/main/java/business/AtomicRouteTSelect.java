package business;

import parser.base.TResultColumn;
import parser.statement.select.TSelectSqlStatement;

import java.util.List;

public class AtomicRouteTSelect {

  /**
   * 物理查询语句的抽象类(物理即没有子查询不可再分)
   */
  private TSelectSqlStatement atomicTSelect;
  /**
   * 物理表对应的所有列字段
   */
  private List<TResultColumn> columns;
  /**
   * 重点：真正的血缘关系图 --> 这里称之为行动路线ActionRoute
   */
  private ActionRoute actionRoute;

  public AtomicRouteTSelect() {
  }

  public AtomicRouteTSelect(TSelectSqlStatement atomicTSelect, ActionRoute route) {
    this.atomicTSelect = atomicTSelect;
    this.actionRoute = route;
  }

  public ActionRoute initActionRoute() {
    this.actionRoute = new ActionRoute();
    return this.actionRoute;
  }

  public TSelectSqlStatement getAtomicTSelect() {
    return atomicTSelect;
  }

  public void setAtomicTSelect(TSelectSqlStatement atomicTSelect) {
    this.atomicTSelect = atomicTSelect;
  }

  public List<TResultColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<TResultColumn> columns) {
    this.columns = columns;
  }

  public ActionRoute getActionRoute() {
    return actionRoute;
  }

  public void setActionRoute(ActionRoute actionRoute) {
    this.actionRoute = actionRoute;
  }
}

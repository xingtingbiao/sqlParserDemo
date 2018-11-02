package lineage;

import business.ActionRoute;
import business.AtomicRouteTSelect;
import business.BloodRelation;
import main.ParseASTNode;
import org.apache.hadoop.hive.ql.parse.ParseException;
import parser.statement.TCustomSqlStatement;
import parser.statement.select.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * 分析sql血缘关系类, 虚表-->物理表(1::N), 虚列-->物理列(1::N)
 */
public class Lineage {

    private TCustomSqlStatement statement;

    private List<ActionRoute> actionRoutes;

    private void initStatement(String sql) throws ParseException {
        this.statement = ParseASTNode.parseNode(sql);
    }

    public Object columnLineage(String sql) throws ParseException {
        this.initActionRoutes(sql);
        if (null != this.actionRoutes && this.actionRoutes.size() > 0) {
            return this.analysisActionRoutes();
        }
        return null;
    }

    private void initActionRoutes(String sql) throws ParseException {
        List<ActionRoute> routes = new ArrayList<>();
        this.initStatement(sql);
        if (this.statement instanceof TSelectSqlStatement) {
            BloodRelation relation = new BloodRelation((TSelectSqlStatement) statement);
            relation.analysisBlood();
            List<AtomicRouteTSelect> atomicRouteTSelects = relation.getAtomicRouteTSelects();
            if (null != atomicRouteTSelects && atomicRouteTSelects.size() > 0) {
                for (AtomicRouteTSelect atomic : atomicRouteTSelects) {
                    ActionRoute actionRoute = relation.dealRouteWithLateralView(atomic.getActionRoute());
                    if (null != actionRoute) routes.add(actionRoute);
                }
            }
        }
        if (routes.size() > 0) this.actionRoutes = routes;
    }

    private Object analysisActionRoutes() {
        // TODO 这里就是最主要的分析函数, 分析完需要返回最外层每个列的所有血缘关系1::N

        return null;
    }

    public TCustomSqlStatement getStatement() {
        return statement;
    }

    public void setStatement(TCustomSqlStatement statement) {
        this.statement = statement;
    }

    public List<ActionRoute> getActionRoutes() {
        return actionRoutes;
    }

    public void setActionRoutes(List<ActionRoute> actionRoutes) {
        this.actionRoutes = actionRoutes;
    }
}
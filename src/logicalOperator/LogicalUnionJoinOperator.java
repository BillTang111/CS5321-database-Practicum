package logicalOperator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Database_Catalog.Catalog;
import UnionFind.UnionFind;
import net.sf.jsqlparser.expression.Expression;
import visitor.PhysicalPlanBuilder;
import visitor.printLogicalQueryPlanVisitor;

/** Union Join Operator for newest logical query plan 
 *  @para childOps: list of children Operator (LogicalSelectOperator / LogicalScanOperator)
 * 	@para residualJoinExpressions: list
 * 	@para UF: UnionFind
 * 	@author Hao Rong, hr335 */
public class LogicalUnionJoinOperator extends LogicalOperator{
	private HashMap<String, LogicalOperator> TableMapChildOps;
	private List<LogicalOperator> childOps;
	private List<Expression> residualJoinExpressions;
	private UnionFind UF;
	
	public LogicalUnionJoinOperator(HashMap<String, LogicalOperator> childs) {
		Catalog data = Catalog.getInstance();
		residualJoinExpressions = data.getJoinResidual();
		UF = data.getUnionFind();
		TableMapChildOps = childs;
		childOps = (List<LogicalOperator>) childs.values();
	}
	
    /** Get all children operators.
     * @return List of all children operators.
     */
    public List<LogicalOperator> getChildrenOperators() {
        return childOps;
    }
    
    /** Get all residual expression.
     * @return List of all residual expression.
     */
    public List<Expression> getJoinResidualExpressions() {
        return residualJoinExpressions;
    }
    
    /** Get union find.
     * @return Union Find.
     */
    public UnionFind getUnionFind() {
        return UF;
    }
    
	@Override
	public void accept(printLogicalQueryPlanVisitor lpv) throws IOException {
		lpv.visit(this);
	}

	@Override
	public void accept(PhysicalPlanBuilder pplanbuilder) throws IOException {
		// TODO Auto-generated method stub
		
	}

}

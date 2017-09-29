package ExpressionTree;

import ExpressionVisitor.ExpressionVisior;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public abstract class A2TreeNode {
	/**
	 * Abstract method for accepting visitor
	 * 
	 * @param visitor
	 *            visitor to be accepted
	 */
	public abstract void accept( ExpressionVisitor visitor);
}

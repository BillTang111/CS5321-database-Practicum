package ExpressionTree;

import ExpressionVisitor.ExpressionVisior;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class ANDexpression extends BinaryTreeNode {

	public ANDexpression(Expression left, Expression right) {
		super(left, right);
	}

	@Override
	public void accept(ExpressionVisitor visitor) {
		//visitor.visit(this);
	}

	
}

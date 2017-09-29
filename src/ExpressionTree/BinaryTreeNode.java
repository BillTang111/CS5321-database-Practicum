package ExpressionTree;

import ExpressionTree.A2TreeNode;
import net.sf.jsqlparser.expression.Expression;

public abstract class BinaryTreeNode extends A2TreeNode{

	private Expression leftChild;
	private Expression rightChild;

	public BinaryTreeNode(Expression left, Expression right) {
		// both set in constructor as won't change
		this.leftChild = left;
		this.rightChild = right;
	}

	/**
	 * Getter for left child
	 * 
	 * @return left child node
	 */
	public Expression getLeftChild() {
		return leftChild;
	}

	/**
	 * Getter for right child
	 * 
	 * @return right child node
	 */
	public Expression getRightChild() {
		return rightChild;
	}
		
}

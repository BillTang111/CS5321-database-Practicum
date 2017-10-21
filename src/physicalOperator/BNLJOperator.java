package physicalOperator;

import java.util.ArrayList;
import java.util.List;

import Tuple.Tuple;
import net.sf.jsqlparser.expression.Expression;
import visitor.visitor;

/**
 * Block Nested Loop Join
 * @author Hao Rong, hr335
 */
public class BNLJOperator extends Operator{
	
	Expression expression;
	Operator outter;
	Operator inner;
	int bufferSize; // Number of pages in the buffer block
	ArrayList<Tuple> block = new ArrayList<Tuple>(); // True buffer to store the data (tuple), need to be filled when one iteration on block is done.
	int maxTupleAmount; // Maximum number of tuple can store in one block.
	int currentTupleAmount; // Current number of tuple in the block.
	boolean finishOuter;
	int nextIndex; // Current index in reading the block
	int finalLoadSize; // The number of tuple in final load of buffer block
	
	Tuple outT; // Tuple in outer relationship, to be loaded in the buffer block
	Tuple outMatch; // Tuple in outer relationship, to be matched with inner relation
	
	public BNLJOperator(Operator left, Operator right, Expression joinExpression, int size) {
		// TODO Auto-generated constructor stub
		expression = joinExpression;
		outter = left;
		inner = right;
		bufferSize = size;
		finishOuter = false;
		nextIndex = 0;
		
		
		outT = outter.getNextTuple(); // help to know the size of outer tuple 
		currentTupleAmount = 0;
		
		maxTupleAmount = (int) Math.floor(bufferSize * 4096 / ((outT.getTuple().size())*4));
		//Page size is 4096 bytes. Each tuple has size 4 the number of attributes.
		
		fillBlock(); // fill the buffer block in initialization
		outMatch = getOuterTuple();
	}

	
	/** Fill the buffer block until it is full & See if we have finished reading outer.
	 * @return true when outer relation is not exhausted.
	 * @return false when we have finish reading all the outer relation*/
	public void fillBlock() {
		clearBlock();
		while ((currentTupleAmount < maxTupleAmount) || (outT!=null)) {
			block.add(outT);
			currentTupleAmount++;
			outT = outter.getNextTuple();
		}
		
		if (outT==null) {
			finishOuter = true;
			finalLoadSize = block.size();
		} else {
			finishOuter = false;
		}
	}
	
	
	/** Clear the buffer block to empty.*/
	public void clearBlock() {
		block = new ArrayList<Tuple>();
		currentTupleAmount = 0;
	}
	
	
	/** Get outer tuple from the block buffer (Our cache with record).*/
	public Tuple getOuterTuple() {
		// Examine if we have empty the block, fill the block when we still have data to process
		if (!finishOuter && (nextIndex > maxTupleAmount-1)){
			fillBlock();
			nextIndex = 0;
		}
		
		// when our block is NOT the LAST load of outer relation
		if (!finishOuter) {
			nextIndex++;
			return block.get(nextIndex-1);
		} 
		
		// when our block is the LAST load of outer relation
		if (nextIndex <= finalLoadSize-1) {
			nextIndex++;
			return block.get(nextIndex-1);
		}
		
		// when we have no data to read in the last load
		return null;
	}
	
	
	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		
		while(outMatch !=null){
			Tuple inMatch = inner.getNextTuple();
			while(inMatch!=null){
				ArrayList outList = outMatch.getTuple();
				ArrayList inList = inMatch.getTuple();
				String s = "";
				for(int i=0; i<outList.size(); i++){
					s=s+outList.get(i)+",";
				}
				for(int j=0; j<inList.size(); j++){
					s=s+inList.get(j)+",";
				}
				s = s.substring(0, s.length()-1);
				ArrayList newTableList = new ArrayList();
				List outTableList = outMatch.getNameList();
				List inTableList = inMatch.getNameList();
				for(int i =0; i<outTableList.size(); i++){
					newTableList.add(outTableList.get(i));
				}
				//System.out.println("1.Table List: " + newTableList);
				//System.out.println("Inner List: " + inTableList);
				for(int i =0; i<inTableList.size(); i++){
					if (newTableList.contains(inTableList.get(i))) continue;
					newTableList.add(inTableList.get(i));
				}
				//System.out.println("2.Table List: " + newTableList);
				Tuple newTuple = new Tuple(s,newTableList);
				visitor v = new visitor(newTuple);
				expression.accept(v);
				if(v.getResult()){
					return newTuple;
				}
				inMatch = inner.getNextTuple();		
			}
			inner.reset();
			outMatch = getOuterTuple();
		}
		return null;
	}
	
	/**Reset the operator to re-call from the beginning */
	@Override
	public void reset() {
		inner.reset();
		outter.reset();	
	}

	/**To print your result. Use for debug 
	 * @param printOrNot: 0: don't print, 1: print*/
	@Override
	public void dump(int printOrNot) {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		if (printOrNot==1){
			while(a != null){
				System.out.println(a.getTuple());
				a=getNextTuple();
				//System.out.println(a.toString());
			}
		} 
		else if (printOrNot==0){
			while(a != null){
				a=getNextTuple();
			}
		}
	}
	
	/** Get all the result tuple in this operator (For debugging) 
	 * @return a list of tuple
	 */
	@Override
	public ArrayList<Tuple> getAllTuple() {
		// TODO Auto-generated method stub
		Tuple a =getNextTuple();
		ArrayList<Tuple> result = new ArrayList<Tuple>();
		while(a != null){
			result.add(a);
			a =getNextTuple();
		}
		return result;
	}
	
	
	
}

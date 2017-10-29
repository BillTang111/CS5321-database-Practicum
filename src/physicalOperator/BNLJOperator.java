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
	
	int numberOfIteration;
	
	Expression expression;
	Operator outter;
	Operator inner;
	int bufferSize; // Number of pages in the buffer block
	ArrayList<Tuple> block = new ArrayList<Tuple>(); // True buffer to store the data (tuple), need to be filled when one iteration on block is done.
	int maxTupleAmount; // Maximum number of tuple can store in one block.
	int currentTupleAmount; // Current number of tuple in the block.
	boolean Lastload;
	int nextIndex; // Current index in reading the block
	int finalLoadSize; // The number of tuple in final load of buffer block
	
	Tuple outT; // Tuple in outer relationship, to be loaded in the buffer block
	Tuple outMatch; // Tuple in outer relationship, to be matched with inner relation
	Tuple inMatch;
	
	boolean finishAllBlock;
	boolean finishOneBlock;
	
	public BNLJOperator(Operator left, Operator right, Expression joinExpression, int size) {
		// TODO Auto-generated constructor stub
		expression = joinExpression;
		outter = left;
		inner = right;
		bufferSize = size;
		Lastload = false;
		nextIndex = 0;
		finishAllBlock = false;
		finishOneBlock = false;
		
		numberOfIteration = 0;
		
		
		outT = outter.getNextTuple(); // help to know the size of outer tuple 
		currentTupleAmount = 0;
		
		maxTupleAmount = (int) Math.floor(bufferSize * 4096 / ((outT.getTuple().size())*4));
		//System.out.println("max amount of tuple in the buffer " + maxTupleAmount);
		//Page size is 4096 bytes. Each tuple has size 4 the number of attributes.
		
		fillBlock(); // fill the buffer block in initialization
		outMatch = nextOuterTuple();
		inMatch = inner.getNextTuple();
	}

	
	/** Fill the buffer block until it is full & See if we have finished reading outer.
	 * @return true when outer relation is not exhausted.
	 * @return false when we have finish reading all the outer relation*/
	public void fillBlock() {
		clearBlock();
		while ((currentTupleAmount < maxTupleAmount) && (outT!=null)) {
			block.add(outT);
			currentTupleAmount++;
			outT = outter.getNextTuple();
			if (outT!=null) {
			//System.out.println("Tuple filling: " + currentTupleAmount + " " + outT.getTuple().toString());
			}
		}
		//System.out.println("Filled one load of data! Max: " + maxTupleAmount);
		
		
		if (outT==null) {
			//System.out.println("Last one load of data!");
			Lastload = true; // finishOuter: whether last load of data.
			finalLoadSize = block.size();
		} else {
			Lastload = false;
		}
	}
	
	
	/** Clear the buffer block to empty.*/
	public void clearBlock() {
		block = new ArrayList<Tuple>();
		currentTupleAmount = 0;
	}
	
	
	/** Get outer tuple from the block buffer (Our cache with record).*/
	public Tuple nextOuterTuple() {
		// when our block is NOT the LAST load of outer relation
		if (!Lastload && (nextIndex < maxTupleAmount-1)) {
			nextIndex++;
			return block.get(nextIndex);
		}
		
		// when our block is the LAST load of outer relation
		if (Lastload && nextIndex < finalLoadSize-1) {
			nextIndex++;
			return block.get(nextIndex);
		}
		
		return null;
	}
	
	
	/** This method return the satisfied tuple and get next tuple from the child operator.
	 * @return the next tuple 
	 * */
	@Override
	public Tuple getNextTuple() {
		while(!finishAllBlock){
			while(inMatch!=null){ //finish one block
				while(outMatch != null){
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
					for(int i =0; i<inTableList.size(); i++){
						if (newTableList.contains(inTableList.get(i))) continue;
						newTableList.add(inTableList.get(i));
					}
					Tuple newTuple = new Tuple(s,newTableList);
					visitor v = new visitor(newTuple);
					//numberOfIteration++;
					expression.accept(v);
					if(v.getResult()){
						//System.out.println("#Iteration: " + newTuple.getTuple().toString());
						outMatch = nextOuterTuple();
						return newTuple;
					}
					outMatch = nextOuterTuple();
				} 
				// finish one round of outer block
				//System.out.println("Go to next tuple");
				nextIndex = -1;
				outMatch = nextOuterTuple();
				inMatch = inner.getNextTuple();//inMatch == null
			} // finally finish one block
			if (Lastload) {
				finishAllBlock = true;
			}
			
			if(!Lastload){
				inner.reset();
				inMatch = inner.getNextTuple();
				fillBlock();
				nextIndex = -1;
				outMatch = nextOuterTuple();
			}
			
			//System.out.println("Another block");
		}// finish all blocks
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


	@Override
	public void reset(int index) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	
	
}

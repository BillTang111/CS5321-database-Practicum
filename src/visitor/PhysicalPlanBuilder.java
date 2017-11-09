package visitor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import BPlusTree.BPlusTree;
import Database_Catalog.BPlusIndexInform;
import Database_Catalog.Catalog;
import logicalOperator.LogicalDuplicateEliminationOperators;
import logicalOperator.LogicalJoinOperator;
import logicalOperator.LogicalOperator;
import logicalOperator.LogicalProjectOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalSortOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import physicalOperator.BNLJOperator;
import physicalOperator.DuplicateEliminationOperators;
import physicalOperator.ExternalSortOperator;
import physicalOperator.IndexScanOperator;
import physicalOperator.JoinOperator;
import physicalOperator.Operator;
import physicalOperator.ProjectOperator;
import physicalOperator.SMJOperator;
import physicalOperator.ScanOperator;
import physicalOperator.ScanOperatorBinary;
import physicalOperator.ScanOperatorHuman;
import physicalOperator.SelectOperator;
import physicalOperator.SortOperator;

public class PhysicalPlanBuilder implements PlanVisitor {
	
	private Stack<Operator> stackOp;
	int jMode; //0 for TNLJ, 1 for BNLJ, or 2 for SMJ
	int jPara; //BNLJ size
	int sMode; //0 for in-memory sort, 1 for external sort
	int sPara; //number of buffer pages for external sort
	private Boolean useIndex;
	private ArrayList<String> indexList;
	private String tableName;
	private String tableColumn;
	
	public PhysicalPlanBuilder() {
		stackOp = new Stack<Operator>();
		interpretConfig();
	}
	
	
	/** @return the root of the entire query plan */
	public Operator getRoot() {
		return stackOp.pop();
	}



	@Override
	public void visit(LogicalDuplicateEliminationOperators logDistinct) throws IOException {
		// TODO Auto-generated method stub
		LogicalOperator logChild = logDistinct.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		DuplicateEliminationOperators distinct = new DuplicateEliminationOperators(child);
		stackOp.push(distinct);
	}



	@Override
	public void visit(LogicalJoinOperator logJoin) throws IOException {
		// TODO Auto-generated method stub
//		PlainSelect selectBody = logJoin.getinnerOperator().;
		Expression joinCondition = logJoin.getJoinCondition();
		LogicalOperator logLeftChild = logJoin.getoutterOperator();
		LogicalOperator logRightChild = logJoin.getinnerOperator();
		logRightChild.accept(this);
		logLeftChild.accept(this);
		
		Operator leftChild = stackOp.pop();
		Operator rightChild = stackOp.pop();
		System.out.println("jMode: " + jMode);
		//System.out.println("join condition: " + joinCondition.toString());
		if (jMode==0){
			System.out.println("Using TNLJ");
			JoinOperator join = new JoinOperator(leftChild, rightChild,joinCondition);
			stackOp.push(join);
		}
		else if (jMode==1){
			System.out.println("Using BNLJ");
			System.out.println("BNLJ buffer size: " + jPara);
			BNLJOperator join = new BNLJOperator(leftChild, rightChild,joinCondition,jPara);
			stackOp.push(join);
		}
		else if (jMode==2){
			System.out.println("Using SMJ");
			System.out.println("SMJ buffer size: " + sPara);
			System.out.println("Sort Mode: " + sMode);
			if (sMode==0) {
				JoinAttributeVisitor visitor = new JoinAttributeVisitor();
	        	joinCondition.accept(visitor);
				SortOperator leftSort = new SortOperator(leftChild, visitor.getLeftAttr());
				SortOperator rightSort = new SortOperator(rightChild, visitor.getRightAttr());
				SMJOperator join = new SMJOperator(leftSort, rightSort, joinCondition);
				stackOp.push(join);
			}
			else if (sMode==1) {
	        	JoinAttributeVisitor visitor = new JoinAttributeVisitor();
	        	joinCondition.accept(visitor);
				ExternalSortOperator leftSorted = new ExternalSortOperator(leftChild, visitor.getLeftAttr(), sPara);
				ExternalSortOperator rightSorted = new ExternalSortOperator(rightChild,visitor.getRightAttr(),sPara);
				SMJOperator join = new SMJOperator(leftSorted, rightSorted, joinCondition);
				stackOp.push(join);
			}
		}
	}



//	private ExternalSortOperator ExternalSortOperator(Operator leftChild,
//			int sPara2, Expression joinCondition) {
//		// TODO Auto-generated method stub
//		return null;
//	}


	@Override
	public void visit(LogicalProjectOperator logProject) throws IOException {
		//System.out.println("haha");
		PlainSelect selectBody = logProject.getPlainSelect();
		LogicalOperator logChild = logProject.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		ProjectOperator project = new ProjectOperator(selectBody, child);
		stackOp.push(project);
	}



	@Override
	public void visit(LogicalScanOperator logDistinct) throws IOException {
		String table = logDistinct.getTableName();
		//ScanOperator scan = new ScanOperator(table);
		//ScanOperatorHuman scan = new ScanOperatorHuman(table);
		ScanOperatorBinary scan = new ScanOperatorBinary(table);
		stackOp.push(scan);
	}



	@Override
	public void visit(LogicalSelectOperator logSelect) throws IOException {
		Expression selectCondition = logSelect.getSelectCondition();
		LogicalOperator logChild = logSelect.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		
		if (!useIndex){
			SelectOperator select = new SelectOperator(selectCondition, child);
			stackOp.push(select);
		}
		else {
			if (!examineEligible(selectCondition, indexList)){
				System.out.println("Using index, but this condition is not eligible");
				SelectOperator select = new SelectOperator(selectCondition, child);
				stackOp.push(select);
			}
			else {
				System.out.println("Using index, this condition attribute is eligible");
				System.out.println(selectCondition);
				System.out.println("Now check if there are multiple conditions on this attribute");
				
				Catalog catalog = Catalog.getInstance();
				HashMap<String, ArrayList> indexInfo = catalog.getIndexInfo();
				
				if (!(child instanceof IndexScanOperator)){
					System.out.println("//no multiple conditions");
					//System.out.println(child.getClass());
					boundVisitor bv = new boundVisitor();
					selectCondition.accept(bv);
					Long lowerBound = ((bv.getLower()==null)? null: ((Long)bv.getLower()-1));
					Long upperBound = ((bv.getUpper()==null)? null: ((Long)bv.getUpper()+1));
					System.out.println("lowKey: " + lowerBound + " | highKey: " + upperBound);
					
					
					ArrayList<String> columnIndexInfo = indexInfo.get(tableColumn);
					boolean isClustered = (columnIndexInfo.get(0)).equals("1")? true: false;
					int order = Integer.parseInt(columnIndexInfo.get(1));
					String indexPath = columnIndexInfo.get(2);
					BPlusIndexInform inform = new BPlusIndexInform(tableColumn, isClustered, order, indexPath);
					IndexScanOperator iSelect = new IndexScanOperator(lowerBound, upperBound, this.tableName, "alias", inform);
					stackOp.push(iSelect);
					
				} else {
					System.out.println("//yes multiple conditions");
					IndexScanOperator indexChild= (IndexScanOperator) child;
					Long lowerBound0 = indexChild.getLowKey();
					Long upperBound0 = indexChild.getHighKey();
					boundVisitor bv = new boundVisitor();
					selectCondition.accept(bv);
					Long lowerBound1 = ((bv.getLower()==null)? null: ((Long)bv.getLower()-1));
					Long upperBound1 = ((bv.getUpper()==null)? null: ((Long)bv.getUpper()+1));
					
					Long lowerBound = null;
					Long upperBound = null;
					
					if (lowerBound0==null || lowerBound1==null) {
						if (lowerBound0==null && lowerBound1==null) {
							lowerBound = null;
						}
						if (lowerBound0==null){
							lowerBound = lowerBound1;
						}
						if (lowerBound1==null){
							lowerBound = lowerBound0;
						}
					} else {
						lowerBound = Math.max(lowerBound0, lowerBound1);
					}
					
					if (upperBound0==null || upperBound1==null) {
						if (upperBound0==null && upperBound1==null) {
							upperBound = null;
						}
						if (upperBound0==null){
							upperBound = upperBound1;
						}
						if (upperBound1==null){
							upperBound = upperBound0;
						}
					} else {
						upperBound = Math.min(upperBound0, upperBound1);
					}
					
					ArrayList<String> columnIndexInfo = indexInfo.get(tableColumn);
					boolean isClustered = (columnIndexInfo.get(0)).equals("1")? true: false;
					int order = Integer.parseInt(columnIndexInfo.get(1));
					String indexPath = columnIndexInfo.get(2);
					BPlusIndexInform inform = new BPlusIndexInform(tableColumn, isClustered, order, indexPath);
					IndexScanOperator iSelect = new IndexScanOperator(lowerBound, upperBound, this.tableName, "alias", inform);
					stackOp.push(iSelect);
				}
			}
		}
	}



	private boolean examineEligible(Expression selectCondition,
			ArrayList<String> iList) {
		String sCondition = selectCondition.toString();
		
		if(!(sCondition.contains("<")||sCondition.contains(">"))){
			// condition must be =
			System.out.println("Reason: this is a = condition");
			return false;
		}
		
		Catalog catalog = Catalog.getInstance();
		HashMap<String, String> aliasPair = catalog.getPairAlias();
		int dot1Index = sCondition.indexOf(".");
		int space1Indect = sCondition.indexOf(" ");
		String firstTable = aliasPair.get(sCondition.substring(0, dot1Index));
		this.tableName = firstTable;
		
		String indexField = firstTable + "." + sCondition.substring(dot1Index+1, space1Indect);
		this.tableColumn = indexField;
		
		if (!iList.contains(indexField)){
			System.out.println("indexList is: " + iList);
			System.out.println("condition field is: " + indexField);
			System.out.println("Reason: condition not in indexList");
			return false;
		}
		
		// iList.contains(indexField) = true
		String remain = sCondition.substring(space1Indect, sCondition.length());
		if (remain.contains(".")){
			// condition after compare sign is another column
			System.out.println("Reason: condition compare two column");
			return false;
		}
		
		return true;
	}


	@Override
	public void visit(LogicalSortOperator logSort) throws IOException {
		PlainSelect selectBody = logSort.getPlainSelect();
		LogicalOperator logChild = logSort.getchildOperator();
		logChild.accept(this);
		List selectItem = selectBody.getOrderByElements();
		
		Operator child = stackOp.pop();
		System.out.println("Sort Mode: " + sMode);
		if (sMode==0) {
			SortOperator sort = new SortOperator(child, selectBody);
			stackOp.push(sort);
		}
		else if (sMode==1){
			ExternalSortOperator sort = new ExternalSortOperator(child, selectItem, sPara);
			stackOp.push(sort);
		}
	}
	
	
	public void interpretConfig(){
		Catalog catalog = Catalog.getInstance();
		String jConfig = catalog.getJoinConfig();
		String sConfig = catalog.getSortConfig();
		jMode = Character.getNumericValue((jConfig.charAt(0)));
		sMode = Character.getNumericValue((sConfig.charAt(0)));
		if (jMode==1){
			String BNLJsize = jConfig.split(" ")[1];
			jPara = Integer.parseInt(BNLJsize);
		}
		if (sMode==1){
			String ESortSize = sConfig.split(" ")[1];
			sPara = Integer.parseInt(ESortSize);
		}
		
		useIndex = false;
		indexList = null;
		Catalog data = Catalog.getInstance();
		if (data.getIndexConfig().equals("1")){
			useIndex = true;
			indexList = data.getIndexList();
			try {
				reloadIndexInfo(data.getInputLocation());
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
	}


	private void reloadIndexInfo(String inputLocation) throws NumberFormatException, IOException {
		BufferedReader indexReader = new BufferedReader(new FileReader(inputLocation + "/db/index_info.txt"));
		String config;
		ArrayList<String> indexConfigList = new ArrayList<String>();
		HashMap<String, ArrayList> indexConfigInfo = new HashMap<String, ArrayList>();
		while((config = indexReader.readLine()) != null){
			//split each line and build corresponding b+ tree
			String[] configs = config.split("\\s+");
			String tableName = configs[0];
			String columnName = configs[1];
			String clusterOrNotString = configs[2];
			boolean clusterOrNot = configs[2].equals("1");
			int order = Integer.parseInt(configs[3]);
			
			ArrayList<String> eachIndexInfo = new ArrayList<String>();
			eachIndexInfo.add(configs[2]);
			eachIndexInfo.add(configs[3]);
			eachIndexInfo.add(inputLocation + "/db/indexes/" + tableName + "." + columnName);
			
			indexConfigList.add(tableName + "." + columnName);
			indexConfigInfo.put(tableName + "." + columnName, eachIndexInfo);
			
		}
		
		indexReader.close();
		
		Catalog catalog = Catalog.getInstance();
		catalog.setIndexList(indexConfigList);
		catalog.setIndexInfo(indexConfigInfo);
	}
	
}

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
import Database_Catalog.IndexInfo;
import Database_Catalog.JoinOrder;
import Database_Catalog.StatsInfo;
import UnionFind.Element;
import UnionFind.UnionFind;
import logicalOperator.LogicalDuplicateEliminationOperators;
import logicalOperator.LogicalJoinOperator;
import logicalOperator.LogicalOperator;
import logicalOperator.LogicalProjectOperator;
import logicalOperator.LogicalScanOperator;
import logicalOperator.LogicalSelectOperator;
import logicalOperator.LogicalSortOperator;
import logicalOperator.LogicalUnionJoinOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
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
	private int defaultJPara;
	private int defaultSPara;
	
	public PhysicalPlanBuilder() {
		stackOp = new Stack<Operator>();
		//interpretConfig();
		defaultJPara = 4;
		defaultSPara = 5;
		sMode = 1;
	}
	
	
	/** @return the root of the entire query plan */
	public Operator getRoot() {
		return stackOp.pop();
	}



	@Override
	public void visit(LogicalDuplicateEliminationOperators logDistinct) throws IOException {
		LogicalOperator logChild = logDistinct.getchildOperator();
		logChild.accept(this);
		
		Operator child = stackOp.pop();
		DuplicateEliminationOperators distinct = new DuplicateEliminationOperators(child);
		stackOp.push(distinct);
	}

	
	/**
	 * visit method for LogicalUnionJoinOperator.
	 * @throws IOException 
	 * @para: A LogicalUnionJoinOperator
	 */
	public void visit(LogicalUnionJoinOperator UnionJoinOp) throws IOException {
		//gets the right ordering of the table by using [JoinOrder];
		System.out.println("Converting LogicalUnionJoinOperator");
		
		JoinOrder tableOrder = new JoinOrder (UnionJoinOp, UnionJoinOp.getUnionFind().getUFlist());
		List<Integer> tableOrderInx = tableOrder.getTablesIndex();
		List<LogicalOperator> UnionJoinOpChildren = UnionJoinOp.getChildrenOperators();
		ArrayList<String> sortedTableList = UnionJoinOp.getSortedTableList();
		
		ArrayList<String> outteredTableList = new ArrayList<String>();
		Operator outter = null;
		for(int i=0; i<tableOrderInx.size(); i++) {
			LogicalOperator logCurrentChild = UnionJoinOpChildren.get(i);
			logCurrentChild.accept(this);
			Operator physicalCurrentChild = stackOp.pop();
			
			if(outter == null){
				outter = physicalCurrentChild;
				outteredTableList.add(sortedTableList.get(i));
			}
			else{
				Operator inner = physicalCurrentChild;
				String toJoinTableName = sortedTableList.get(i);
				analysisResult analysis = findJoinCondition(outteredTableList, toJoinTableName);
				
				if(analysis.getIsEqualEx()){
					ArrayList<Column> outterOder = new ArrayList<Column>(); 
					outterOder.add(analysis.getOutterAttr());
					ArrayList<Column> innerOder = new ArrayList<Column>(); 
					innerOder.add(analysis.getInnerAttr());
					
					ExternalSortOperator outterSorted = new ExternalSortOperator(outter, outterOder, defaultSPara);
					ExternalSortOperator innerSorted = new ExternalSortOperator(inner, innerOder, defaultSPara);
					outter  = new SMJOperator(outterSorted, innerSorted, analysis.getJoinCondition());
				}
				else{
					outter = new BNLJOperator(outter, inner, analysis.getJoinCondition(), defaultJPara);
				}
				outteredTableList.add(toJoinTableName);
			}
		}
		stackOp.push(outter);
		System.out.println("Converting LogicalUnionJoinOperator Finished");
		System.out.println("=== Part of the Tree ===");
		printPhysicalQueryPlanVisitor ppv = new printPhysicalQueryPlanVisitor();
		outter.accept(ppv);
		System.out.println(ppv.getResult());
		System.out.println("========================");
	}
	
	
	/** find join condition for the current join order 
	 *  @return current join condition expression */
	private analysisResult findJoinCondition(ArrayList<String> outteredTableList,
			String toJoinTableName) {
		Catalog data = Catalog.getInstance();
		List<Element> UF = data.getUnionFind().getUFlist();
		List<Expression> resiJoinConditions = data.getJoinResidual();
		
		analysisResult analysis = new analysisResult();
		
		// Examine residual join conditions
		for(Expression e: resiJoinConditions){
			ArrayList<String> twoTableName = findTwoTable(e.toString());
			if(twoTableName.contains(toJoinTableName)){
				if(twoTableName.get(0)==toJoinTableName){
					if(outteredTableList.contains(twoTableName.get(1))){
						analysis.setIsEqualEx(false);
						analysis.setJoinCondition(e);
						return analysis;
					}
				}
				else{
					if(outteredTableList.contains(twoTableName.get(0))){
						analysis.setIsEqualEx(false);
						analysis.setJoinCondition(e);
						return analysis;
					}
				}
			}
		}
		
		// Examine element box
		for(Element eBox: UF) {
			if(eBox.getattri().size()>1){ // If this is a join box
				ArrayList<String> tableList = new ArrayList<String>();
				ArrayList<Column> attrList = new ArrayList<Column>();
				
				for(Column c: eBox.getattri()){
					tableList.add(deAlias(c.toString()));
					attrList.add(c);
				}
//				ArrayList<String> nonDuplicateTableList = new ArrayList<String>();
//				ArrayList<Column> nonDuplicateAttrList = new ArrayList<Column>();
//				for(int i=0; i<tableList.size(); i++){
//					String iName = tableList.get(i);
//					Column iAttr = attrList.get(i);
//					if(!nonDuplicateTableList.contains(iName)){
//						nonDuplicateTableList.add(iName);
//						nonDuplicateAttrList.add(iAttr);
//					}
//				}
				if(tableList.indexOf(toJoinTableName)!=-1){
					Column attrInner = attrList.get(tableList.indexOf(toJoinTableName));
					Column attrOutter = null;
					for(String baseTable: outteredTableList){
						if(tableList.indexOf(baseTable)!=-1){
							attrOutter = attrList.get(tableList.indexOf(baseTable));
						}
					}
					if(attrOutter!=null){
						EqualsTo eqExpr = new EqualsTo(attrOutter,attrInner);
						analysis.setIsEqualEx(true);
						analysis.setJoinCondition(eqExpr);
						analysis.setOutterAttr(attrOutter);
						analysis.setInnerAttr(attrInner);
						return analysis;
					}
				}
			}
		}
			
		return null;
	}
	
	/** Find two table string in the join expression
	 *  And deAlias two table name
	 *  Input: R.A = S.B
	 *  @return [Reserve, Sailors] */
	private ArrayList<String> findTwoTable(String exprString) {
		Catalog data = Catalog.getInstance();
		HashMap<String, String> pairAlias = data.getPairAlias();
		
		int dot1Index = exprString.indexOf(".");
		int space1Index = exprString.indexOf(" ");
		String table1 = pairAlias.get(exprString.substring(0, dot1Index));
		String remainExpr = exprString.substring(space1Index+1, exprString.length());
		int space2Index = remainExpr.indexOf(" ");
		int dot2Index = remainExpr.indexOf(".");		
		String table2 = pairAlias.get(remainExpr.substring(space2Index+1, dot2Index));
				
		ArrayList<String> result = new ArrayList<String>();
		result.add(table1);
		result.add(table2);
		return result;
	}
	
	/** Find the string before the first dot(.) as input String
	 *  Replace the Alias (If exist) to the original table name in the input String 
	 *  Input: R.A or R 
	 *  @return Reserve */
	private String deAlias(String attr) {
		Catalog data = Catalog.getInstance();
		HashMap<String, String> pairAlias = data.getPairAlias();
		int dotIndex = attr.indexOf(".");
		if (dotIndex == -1){
			return pairAlias.get(attr);
		}
				
		String aliasTableName = attr.substring(0, dotIndex);
		return pairAlias.get(aliasTableName);
	}


	@Override
	public void visit(LogicalJoinOperator logJoin) throws IOException {
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
	public void visit(LogicalSelectOperator logSelect) throws IOException{
		String tableName = ((LogicalScanOperator)logSelect.getchildOperator()).getTableName();
		Catalog catalog = Catalog.getInstance();
		StatsInfo stats = catalog.getStatsInfo();
		int tupleNum = stats.getTableAndSizeMap().get(tableName);
		double tupleSize = stats.getTableAndFieldMap().get(tableName).size() * 4;
		double scanCost =  Math.ceil((tupleSize*tupleNum)/4096);
		double minCost = scanCost; // default setting
		double r = 1.0; //default setting
		System.out.println("tableName: "+tableName);
		List<IndexInfo> indexInfos = catalog.getIndexMap().get(tableName);
		int p = (int)scanCost;
		int t = stats.getTableAndSizeMap().get(tableName);
		IndexExprVisitor indexVisitor = null;
		IndexInfo indexinfo = null;
		if(indexInfos != null){
		for(IndexInfo index: indexInfos){
			String columnName = index.getColumn().getColumnName();
			IndexExprVisitor v = new IndexExprVisitor(columnName);
			Expression selectionCondition = logSelect.getSelectCondition();
			selectionCondition.accept(v);
			
			int l = catalog.getLeaveNum(tableName, columnName);
			r = stats.getReductionFactor(tableName, columnName, v.getLowkey(), v.getHighkey(), v.isLowopen(), v.isHighopen());
			double indexCost;
			if(index.isClustered()){
				indexCost = 3 + p*r;
			}else{
				indexCost = 3 + l*r + t*r;
			}
			
			if(indexCost < minCost){
				minCost = indexCost;
				indexVisitor = v;
				indexinfo = index;
			}
		}
		}
		if(indexVisitor != null){
			// use index scan
			LogicalScanOperator logScan = (LogicalScanOperator) logSelect.getchildOperator();
//			IndexScanOperator iSelect = new IndexScanOperator(lowerBound, upperBound, this.tableName, inform);
			Long lowerBound = indexVisitor.getLowkey();
			Long upperBound = indexVisitor.getHighkey();
			if(upperBound!=null){
				if(indexVisitor.isHighopen()==false) upperBound = upperBound+1;
			}
			if(lowerBound!=null){
				if(indexVisitor.isLowopen() ==false) lowerBound = lowerBound-1;
			}
//			BPlusIndexInform inform = new BPlusIndexInform(tableColumn, isClustered, order, indexPath);
			BPlusIndexInform inform = new BPlusIndexInform(indexinfo.getColumn().toString(), indexinfo.isClustered(), indexinfo.getOrder(), indexinfo.getIndexPath());
			IndexScanOperator scan = new IndexScanOperator(lowerBound,upperBound,tableName,inform);
			if(indexVisitor.getNoIndexExpr()!=null){
				SelectOperator op = new SelectOperator(logSelect.getSelectCondition(),scan);
				stackOp.push(op);
			}else{
				stackOp.push(scan);
			}
		}else{
			//use full scan
			logSelect.getchildOperator().accept(this);
			Operator child = stackOp.pop();
			SelectOperator select = new SelectOperator(logSelect.getSelectCondition(), child);
			stackOp.push(select);
			
		}
		
	}

//	@Override
//	public void visit(LogicalSelectOperator logSelect) throws IOException {
//		Expression selectCondition = logSelect.getSelectCondition();
//		LogicalOperator logChild = logSelect.getchildOperator();
//		logChild.accept(this);
//		
//		Operator child = stackOp.pop();
//		
//		if (!useIndex){
//			SelectOperator select = new SelectOperator(selectCondition, child);
//			stackOp.push(select);
//		}
//		else {
//			if (!examineEligible(selectCondition, indexList)){
//				System.out.println("Using index, but this condition is not eligible");
//				SelectOperator select = new SelectOperator(selectCondition, child);
//				stackOp.push(select);
//			}
//			else {
//				System.out.println("Using index, this condition attribute is eligible");
//				System.out.println(selectCondition);
//				System.out.println("Now check if there are multiple conditions on this attribute");
//				
//				Catalog catalog = Catalog.getInstance();
//				HashMap<String, ArrayList> indexInfo = catalog.getIndexInfo();
//				
//				if (!(child instanceof IndexScanOperator)){
//					System.out.println("//no multiple conditions");
//					//System.out.println(child.getClass());
//					boundVisitor bv = new boundVisitor();
//					selectCondition.accept(bv);
//					Long lowerBound = ((bv.getLower()==null)? null: ((Long)bv.getLower()-1));
//					Long upperBound = ((bv.getUpper()==null)? null: ((Long)bv.getUpper()+1));
//					System.out.println("lowKey: " + lowerBound + " | highKey: " + upperBound);
//					
//					
//					ArrayList<String> columnIndexInfo = indexInfo.get(tableColumn);
//					boolean isClustered = (columnIndexInfo.get(0)).equals("1")? true: false;
//					int order = Integer.parseInt(columnIndexInfo.get(1));
//					String indexPath = columnIndexInfo.get(2);
//					
//					System.out.println("isClustered: " + isClustered + " | order: " + order + " | indexPath: " + indexPath);
//					BPlusIndexInform inform = new BPlusIndexInform(tableColumn, isClustered, order, indexPath);
//					System.out.println("BPlusIndexInform built.");
//					IndexScanOperator iSelect = new IndexScanOperator(lowerBound, upperBound, this.tableName, inform);
//					System.out.println("IndexScanOperator built.");
//					stackOp.push(iSelect);
//					
//				} else {
//					System.out.println("//yes multiple conditions");
//					IndexScanOperator indexChild= (IndexScanOperator) child;
//					Long lowerBound0 = indexChild.getLowKey();
//					Long upperBound0 = indexChild.getHighKey();
//					boundVisitor bv = new boundVisitor();
//					selectCondition.accept(bv);
//					Long lowerBound1 = ((bv.getLower()==null)? null: ((Long)bv.getLower()-1));
//					Long upperBound1 = ((bv.getUpper()==null)? null: ((Long)bv.getUpper()+1));
//					
//					Long lowerBound = null;
//					Long upperBound = null;
//					
//					if (lowerBound0==null || lowerBound1==null) {
//						if (lowerBound0==null && lowerBound1==null) {
//							lowerBound = null;
//						}
//						if (lowerBound0==null){
//							lowerBound = lowerBound1;
//						}
//						if (lowerBound1==null){
//							lowerBound = lowerBound0;
//						}
//					} else {
//						lowerBound = Math.max(lowerBound0, lowerBound1);
//					}
//					
//					if (upperBound0==null || upperBound1==null) {
//						if (upperBound0==null && upperBound1==null) {
//							upperBound = null;
//						}
//						if (upperBound0==null){
//							upperBound = upperBound1;
//						}
//						if (upperBound1==null){
//							upperBound = upperBound0;
//						}
//					} else {
//						upperBound = Math.min(upperBound0, upperBound1);
//					}
//					
//					System.out.println("lowKey: " + lowerBound + " | highKey: " + upperBound);
//					
//					ArrayList<String> columnIndexInfo = indexInfo.get(tableColumn);
//					boolean isClustered = (columnIndexInfo.get(0)).equals("1")? true: false;
//					int order = Integer.parseInt(columnIndexInfo.get(1));
//					String indexPath = columnIndexInfo.get(2);
//					BPlusIndexInform inform = new BPlusIndexInform(tableColumn, isClustered, order, indexPath);
//					IndexScanOperator iSelect = new IndexScanOperator(lowerBound, upperBound, this.tableName, inform);
//					stackOp.push(iSelect);
//				}
//			}
//		}
//	}



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
		//System.out.println(iList);
		
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
		System.out.println(child);
		System.out.println("Sort Mode: " + sMode);
		if (sMode==0) {
			SortOperator sort = new SortOperator(child, selectBody);
			System.out.println("hello");
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
		//System.out.println(data.getIndexConfig());
		if (data.getIndexConfig().equals("1")){
			useIndex = true;
			try {
				reloadIndexInfo(data.getInputLocation());
				indexList = data.getIndexList();
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
		System.out.println("##reloadIndexInfo" + indexConfigInfo);
	}
	
	
}

class analysisResult {
	boolean isEqualEx;
	Expression joinCondition;
	Column outterAttr;
	Column innerAttr;
	
	public analysisResult(boolean isE, Expression jExpr, Column outC, Column inC) {
		isEqualEx = isE;
		joinCondition = jExpr;
		outterAttr = outC;
		innerAttr = inC;
	}
	
	public analysisResult() {}
	
	public boolean getIsEqualEx(){
		return isEqualEx;
	}
	
	public Expression getJoinCondition(){
		return joinCondition;
	}
	
	public Column getOutterAttr(){
		return outterAttr;
	}
	
	public Column getInnerAttr(){
		return innerAttr;
	}
	
	public void setIsEqualEx(boolean b){
		isEqualEx = b;
	}
	
	public void setJoinCondition(Expression e){
		joinCondition = e;
	}
	
	public void setOutterAttr(Column c){
		outterAttr = c;
	}
	
	public void setInnerAttr(Column c){
		innerAttr = c;
	}
}

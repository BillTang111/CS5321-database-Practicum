package parser;
import java.io.FileReader;
import java.util.ArrayList;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

/**
 * Example class for getting started with JSQLParser. Reads SQL statements from
 * a file and prints them to screen; then extracts SelectBody from each query
 * and also prints it to screen.
 * 
 * @author Lucja Kot
 */
public class Parser {

//	private static final String queriesFile = "queries.sql";
	private static String queriesFile;
	private ArrayList<PlainSelect> queryList;
	
	
	public Parser(String sql){
		queriesFile = sql;
		PlainSelect selectbody;
		queryList = new ArrayList<PlainSelect>();
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(queriesFile));
			Statement statement;
			while ((statement = parser.Statement()) != null) {
				System.out.println("Read statement: " + statement);
				Select select = (Select) statement;
				selectbody = (PlainSelect) select.getSelectBody();
				
				System.out.println("Select from " + selectbody.getFromItem().toString());
				System.out.println("Select from Alias " + selectbody.getFromItem().getAlias());
				System.out.println("join from " + selectbody.getJoins());
				System.out.println("join from Alias " + selectbody.getJoins().get(0));
				System.out.println("Select body is " + selectbody);			
				System.out.println("condition " + selectbody.getWhere());
				queryList.add(selectbody);
				System.out.println("project: " + selectbody.getSelectItems().get(0));
				System.out.println("join: " + selectbody.getJoins());
				System.out.println("order by: " + selectbody.getOrderByElements());
				System.out.println("distinct : " + selectbody.getDistinct());
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<PlainSelect> getQueryList(){
		return queryList;
	}
	
//	public static void main(String[] args) {
//		try {
//			CCJSqlParser parser = new CCJSqlParser(new FileReader(queriesFile));
//			Statement statement;
//			while ((statement = parser.Statement()) != null) {
//				System.out.println("Read statement: " + statement);
//				Select select = (Select) statement;
//				PlainSelect selectbody = (PlainSelect) select.getSelectBody();
//				System.out.println("Select body is " + selectbody);
//				System.out.println("Select from " + selectbody.getFromItem());
//				System.out.println("the other selected table " + selectbody.getJoins());
//				System.out.println("condition " + selectbody.getWhere());
//				System.out.println("distinct? " + selectbody.getDistinct());
//				System.out.println("order by " + selectbody.getOrderByElements());
//				Expression condition = selectbody.getWhere();
//			}
//		} catch (Exception e) {
//			System.err.println("Exception occurred during parsing");
//			e.printStackTrace();
//		}
//	}
}
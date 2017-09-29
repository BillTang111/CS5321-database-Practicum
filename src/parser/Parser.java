package parser;
import java.io.FileReader;

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
	public PlainSelect selectBody;
	
	public Parser(String sql){
		queriesFile = sql;
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(queriesFile));
			Statement statement;
			while ((statement = parser.Statement()) != null) {
				System.out.println("Read statement: " + statement);
				Select select = (Select) statement;
				PlainSelect selectbody = (PlainSelect) select.getSelectBody();
				selectBody = selectbody;
				System.out.println("Select body is " + selectbody);
				System.out.println("Select from " + selectbody.getFromItem());
				
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
		
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
//package visitor;
//
//import static org.junit.Assert.*;
//
//import java.util.ArrayList;
//
//import org.junit.Test;
//
//import com.sun.javafx.fxml.expression.Expression;
//
//import Tuple.Tuple;
//import net.sf.jsqlparser.statement.select.FromItem;
//
//public class visitorTest {
//
//	@Test
//	public void test() {
//		String input = "1,200,50";
//		Tuple a = new Tuple(input);
//		visitor v = new visitor(a);
//		Integer i = new Integer(1);
//		Integer j = new Integer(2);
//		Expression e = new Expression(i,j,">");
//	}
//
//}
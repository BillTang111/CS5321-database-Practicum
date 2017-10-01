package visitor;

import static org.junit.Assert.*;

import org.junit.Test;

import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class joinVisitorTest {

	@Test
	public void test() {
		//long a, equlas e, colum c d f, c=d
		LongValue a = new LongValue(3);
		EqualsTo e = new EqualsTo();
		Column c = new Column();
		Table h = new Table();
		h.setName("sailor");
		c.setTable(h);
		Column d = new Column();
		d.setTable(h);
		Table f = new Table();
		f.setName("boat");
		Column g = new Column();
		g.setTable(f);
	}

}

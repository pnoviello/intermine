package org.flymine.sql.query;

import junit.framework.*;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.flymine.sql.DatabaseFactory;

public class PostgresExplainResultTest extends TestCase
{
    private PostgresExplainResult er;

    public PostgresExplainResultTest(String arg1) {
        super(arg1);
    }

    public void setUp() {
        er = new PostgresExplainResult();
    }

    public void testParseWarningString() throws Exception {
        er.parseWarningString("Seq Scan on flibble  (cost=0.00..3.15 rows=2 width=4)\n"
                + "  SubPlan\n    ->  Seq Scan on flibble t  (cost=0.00..1.05 rows=1 width=4)\n");
        assertEquals(2, er.getRows());
        assertEquals(0, er.getStart());
        assertEquals(31, er.getComplete());
        assertEquals(4, er.getWidth());
        assertEquals(2, er.getEstimatedRows());
    }

    /*
    public void testConstructNullQuery() throws Exception {
        try {
            er = new PostgresExplainResult(null, DatabaseFactory.getDatabase("db.unittest").getConnection());
            fail("Expected: NullPointerException");
        }
        catch (NullPointerException e) {
        }
    }*/

    public void testConstructNullConnection() throws Exception {
        try {
            er = new PostgresExplainResult(new Query(), null);
            fail("Expected: NullPointerException");
        }
        catch (NullPointerException e) {
        }
    }

    /*
    public void testParseNullString() throws Exception {
        try {
            er.parseWarningString(null);
            fail("Expected: NullPointerException");
        }
        catch (NullPointerException e) {
        }
    }*/

    public void testParseEmptyString() throws Exception {
        try {
            er.parseWarningString("");
            fail("Expected: IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testParseRubbishString() throws Exception {
        try {
            er.parseWarningString("ljkhafjhaiue,mnz fgsdfes) haskfjsdf");
            fail("Expected: IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testParseCraftedString() throws Exception {
        try {
            er.parseWarningString("Flibble Wotsit (cost=76222.25..3241.13 rows2kj"
                    + "hdaslkjasdf) alkjhfasfjasdfkjh");
            fail("Expected: IllegalArgumentException");
        }
        catch (IllegalArgumentException e) {
        }
    }

    public void testConstructNullPreparedStatement() throws Exception {
        try {
            er = new PostgresExplainResult(null);
            fail("Expected: NullPointerException");
        }
        catch (NullPointerException e) {
        }
    }

    public void testNullWarningPreparedStatement() throws Exception {
        // pass in an sql statement without an EXPLAIN, should give no warnings

        try {
            String sql = "select 1";
            PreparedStatement stmt = DatabaseFactory.getDatabase("db.unittest").getConnection().prepareStatement(sql);
            er = new PostgresExplainResult(stmt);
            fail("Expected: SQLException");
        } catch (SQLException e) {
        } catch (NullPointerException e) {
            fail("Expected SQLException but Null PointerException thrown");
        }
    }

    public void testTooManyWarnings() throws Exception {
        // Pass in an sql statement with an explain that returns too many warnings.
        
        try {
            String sql = "explain select flibble from (select 1 as flibble) as lkkjhsdkjfhksjfksfkdsjfhksdjfhskfjhskdjfhsdkjfhskdjfhskdjfhskdfhdksjfhskjfh";
            PreparedStatement stmt = DatabaseFactory.getDatabase("db.unittest").getConnection().prepareStatement(sql);
            er = new PostgresExplainResult(stmt);
            fail("Expected: SQLException");
        } catch (SQLException e) {
            if (!e.getMessage().equals("Database returned more than one warning while EXPLAINing")) {
                fail("Wrong message in exception");
            }
        }
    }



}


package sqlsugg.backends;


import java.util.*;
import java.io.*;

/**
 * Do not forget to run the "finishDump" function.
 * @author Ju Fan
 *
 */
public class DBDump {
	SQLBackend sql;
	String prefix = null;
	List<String> tuples = null;
	int batch = -1;
	BufferedWriter w;
	public DBDump (SQLBackend pSql) {
		sql = pSql;
	}
	
	void doInsert() throws Exception {
		String stmt = prefix;
		int bcount = 0;
		for (String tuple : tuples) {
			stmt += tuple;
			if (bcount < tuples.size() - 1) {
				stmt += ",";
			}
			bcount++;
		}
		try {
			//System.out.println(stmt);
			sql.execute(stmt);
		}catch (Exception e) {
			w.write(stmt);
			w.newLine();
			w.flush();
			e.printStackTrace();
		}
		tuples.clear();

	}
	
	public void initDump (String pPrefix, int pBatch) throws Exception{
		prefix = pPrefix;
		tuples = new LinkedList<String> ();
		batch = pBatch;
		w = new BufferedWriter (new FileWriter("data/errLogs.txt"));
	}
	
	public void addTuple (String tuple) throws Exception {
		tuples.add(tuple);
		if (tuples.size() == batch) {
			doInsert ();
		}
	}
	
	public void finishDump () throws Exception {
		if (tuples.size() > 0) {
			doInsert();
		}
		prefix = null;
		tuples = null;
		w.close();
	}
	
	public static void main (String args[]) throws Exception {
		SQLBackend sql = null;
		DBDump dump = new DBDump (sql);
		dump.initDump("INSERT INTO table1 (a1, a2) VALUES ", 100);
		while (true) {
			dump.addTuple("('abc',123)");
			break;
		}
		dump.finishDump();
	}
}

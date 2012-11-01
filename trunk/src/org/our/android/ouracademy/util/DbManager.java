package org.our.android.ouracademy.util;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.our.android.ouracademy.dao.CategoryDAO;
import org.our.android.ouracademy.dao.ContentDAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbManager {

	/* DB Helper */
	private static final String DATABASE_NAME = "our.db";
	private static final int DATABASE_VERSION = 1;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(CategoryDAO.CATEGORY_DDL);
				db.execSQL(ContentDAO.CONTENT_DDL);
				db.execSQL(ContentDAO.CONTENT_CATEGORY_DDL);
			} catch (Exception e) {
				Log.d("DB Create", e.toString());
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// db.execSQL("DROP IF TABLE EXISTS "+DATABASE_TABLE);
			onCreate(db);
		}
	}

	public DbManager open(Context ctx) throws SQLException {
		mDbHelper = new DatabaseHelper(ctx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/** Closes a database connection */
	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	// ///////////////////////////////////////////////////////////
	DbManager() {

	}

	static public DbManager getInstance() {
		return mDbManager;
	}

	public boolean create(Context ctx, String name) {
		try {
			mDb = ctx.openOrCreateDatabase(name, 0, null);// MODE_WORLD_READABLE
		} catch (Exception err) {
			return (false);
		}

		return (true);
	}

	public SQLiteDatabase getDB() {
		// http://stackoverflow.com/questions/1483629/exception-attempt-to-acquire-a-reference-on-a-close-sqliteclosable
		// 이슈로 인해 수정
		if (mDbHelper != null) {
			return mDbHelper.getWritableDatabase();
		} else {
			return null;
		}
	}

	public boolean beginTransaction(Context ctx) {
		SQLiteDatabase db = getDB();
		if (db == null){
			open(ctx);
			db = getDB();
		}
		
		if (db != null && db.inTransaction() == false) {
			db.beginTransaction();
			return db.inTransaction();
		} else {
			return false;
		}
	}

	public void commitTransaction() {
		getDB().setTransactionSuccessful();
	}

	public boolean endTransaction() {
		SQLiteDatabase db = getDB();
		if (db != null && db.inTransaction() == true) {
			db.endTransaction();
			return !db.inTransaction();
		} else {
			return false;
		}
	}

	/* Table operations */
	public boolean createTable(String tableName, ContentValues values) {
		// int i;
		String statement = "CREATE TABLE IF NOT EXISTS ";
		statement += tableName;
		statement += " ";
		statement += "(";
		// statement += tableEntries;

		Set set = values.valueSet();

		Iterator<Entry<String, String>> itor = set.iterator();
		while (itor.hasNext()) {
			Entry<String, String> entry = itor.next();
			statement += entry.getKey();
			statement += " ";
			statement += entry.getValue();
			if (itor.hasNext()) {
				statement += ",";
			}
		}
		statement += ");";

		try {
			SQLiteDatabase db = getDB();
			if (db != null) {
				db.execSQL(statement);
			} else {
				return false;
			}
		} catch (Exception err) {
			err.printStackTrace();
			return (false);
		}
		return (true);
	}

	public boolean createTable(String tableName, NameValuePair[] values) {
		int i;
		String statement = "CREATE TABLE IF NOT EXISTS ";
		statement += tableName;
		statement += " ";
		statement += "(";

		for (i = 0; i < values.length; i++) {
			statement += values[i].mName;
			statement += " ";
			statement += values[i].mValue;
			if (i != (values.length - 1)) {
				statement += ",";
			}
		}

		statement += ");";

		try {
			SQLiteDatabase db = getDB();
			if (db != null) {
				db.execSQL(statement);
			} else {
				return false;
			}
		} catch (Exception err) {
			return (false);
		}
		return (true);
	}

	public boolean copyTable(String from, String to) {
		String sql = String.format("CREATE TABLE %s AS SELECT * FROM %s", to,
				from);
		SQLiteDatabase db = getDB();

		try {
			if (db != null) {
				db.execSQL(sql);
			} else {
				return false;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean isTableExist(String tableName) {
		String sql = String
				.format("SELECT COUNT(*) FROM sqlite_master WHERE type='table' and name='%s'",
						tableName);
		Cursor cursor = null;
		boolean result = false;
		SQLiteDatabase db = getDB();

		try {
			if (db != null) {
				cursor = db.rawQuery(sql, null);

				if (cursor != null) {
					cursor.moveToFirst();
					if (cursor.getCount() > 0) {
						if (cursor.getInt(0) > 0) {
							result = true;
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}

	public void deleteTable(String tableName) {
		try {
			// mDb.execSQL("DROP IF TABLE EXISTS "+tableName);
			SQLiteDatabase db = getDB();
			if (db != null) {
				db.delete(tableName, null, null);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public void dropTable(String tableName) {
		try {
			SQLiteDatabase db = getDB();

			if (db != null) {
				db.execSQL("DROP TABLE IF EXISTS " + tableName);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	public Cursor select(String tableName, String[] columns, String condition) {
		Cursor cursor = null;
		try {
			SQLiteDatabase db = getDB();
			if (db != null) {
				cursor = db.query(tableName, columns, condition, null, null,
						null, null);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return (cursor);
	}

	public Cursor selectAllColumns(String tableName, String condition) {
		Cursor cursor = null;
		try {
			String sql = String.format("SELECT * FROM %s WHERE %s", tableName,
					condition);
			SQLiteDatabase db = getDB();
			if (db != null) {
				cursor = db.rawQuery(sql, null);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (cursor);
	}

	public Cursor selectByOrder(String tableName, String[] columns,
			String condition, String order) {
		Cursor cursor = null;
		try {
			SQLiteDatabase db = getDB();
			if (db != null) {
				cursor = db.query(tableName, columns, condition, null, null,
						null, order);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return (cursor);
	}

	public long insert(String tableName, DbRow row) {
		/*
		 * long result = 0; try { InsertHelper helper = new
		 * DatabaseUtils.InsertHelper(mDb,tableName);
		 * //helper.prepareForReplace(); //helper.execute(); //helper.close();
		 * helper.replace(row.getConentValues()); //result =
		 * mDb.insert(tableName, null, row.getConentValues()); } catch
		 * (Exception err) { err.printStackTrace(); } return (result);
		 */
		return (insertOrReplace(tableName, row));
	}

	public long insert(String tableName, String command, DbRow row) {
		long result = 0;
		String columns = row.getColumnNames(",", "(", ")");
		String sql = command + " ";
		String col = "";
		sql += " INTO ";
		sql += tableName;
		sql += columns;
		sql += " VALUES ";
		sql += "(";

		int i = 0;
		for (i = 0; i < row.getLength() - 1; i++) {
			sql += "'";
			col = row.getValue(i);
			if (col != null) {
				sql += col.replaceAll("'", "''");
			}
			sql += "',";
		}

		sql += "'";
		sql += row.getValue(i);
		sql += "'";
		sql += ");";

		try {
			/*
			 * SQLiteStatement statement = mDb.compileStatement(sql); for (int i
			 * = 0; i < row.getLength(); i++) statement.bindString(i ,
			 * row.getValue(i)); statement.execute(); //result =
			 * mDb.insert(tableName, null, row.getConentValues());
			 */

			SQLiteDatabase db = getDB();
			if (db != null) {
				db.execSQL(sql);
				result = 1;
			} else {
				return 0;
			}

		} catch (Exception err) {
			err.printStackTrace();
		}
		return (result);
	}

	public long delete(String tableName, String whereClause, String[] whereArgs)
			throws Exception {
		long result = 0;
		try {
			SQLiteDatabase db = getDB();
			result = db.delete(tableName, whereClause, whereArgs);
		} catch (Exception err) {
			err.printStackTrace();
			throw err;
		}

		return result;
	}

	public long insertOrReplace(String tableName, DbRow row) {
		return insert(tableName, "INSERT OR REPLACE", row);
	}

	public long insertOrIgnore(String tableName, DbRow row) {
		return insert(tableName, "INSERT OR IGNORE", row);
	}

	public long insertOrUpdate(String tableName, DbRow row, String condition) {
		long result = 0;
		SQLiteDatabase db = getDB();

		result = insertOrIgnore(tableName, row);
		result = db.update(tableName, row.getConentValues(), condition, null);

		return (result);
	}

	public long update(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) {
		long result = -1;
		SQLiteDatabase db = getDB();

		if(db != null){
			result = db.update(tableName, values, whereClause, whereArgs);
		}

		return (result);

	}

	public int getCount(String tableName) {
		String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
		Cursor cursor;
		SQLiteDatabase db = getDB();
		if (db != null) {
			cursor = db.rawQuery(sql, null);
		} else {
			return -1;
		}
		if (cursor != null && cursor.moveToFirst()) {
			int result = cursor.getInt(0);
			cursor.close();
			return result;
		}
		return (-1);
	}

	public void addColumn(String tableName, String columnName, String type,
			String defaultValue) {
		Cursor dbCursor = this.getDB().rawQuery(
				"SELECT * from " + tableName + " LIMIT 1", null);
		if (dbCursor.getColumnIndex(columnName) == -1) {
			SQLiteDatabase db = this.getDB();
			if (db != null) {
				String query = "ALTER TABLE " + tableName + " ADD "
						+ columnName + " " + type + " " + defaultValue;
				db.execSQL(query);
				db.close(); // 다시 들어왔을 때 Column 을 정상적으로 보여주기 위해 close 한다.
			}
		}
		dbCursor.close();
	}

	private SQLiteDatabase mDb = null;
	private DatabaseHelper mDbHelper;
	private static DbManager mDbManager = new DbManager();
}

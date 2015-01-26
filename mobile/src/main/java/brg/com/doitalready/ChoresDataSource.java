package brg.com.doitalready;

/**
 * Created by jmo on 12/17/2014.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import brg.com.doitalready.model.Chore;

public class ChoresDataSource {

    private static ChoresDataSource instance = null;

    private SQLiteDatabase database;
    private ChoresDatabaseHelper dbHelper;
    private String[] allColumns = ChoresDatabaseHelper.getColumnIds();

    public enum ChoreType {
        ALL(0, ""),
        COMPLETED(1, "TRUE"),
        INCOMPLETE(2, "FALSE");

        private final int value;
        private final String queryValue;

        ChoreType(int value, String queryValue) {
            this.value = value;
            this.queryValue = queryValue;
        }
    };

    private ChoresDataSource(Context context) {
        dbHelper = new ChoresDatabaseHelper(context);
    }

    public static ChoresDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new ChoresDataSource(context);
        }
        return instance;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Chore createChore(String name) {
        ContentValues values = new ContentValues();
        values.put(ChoresDatabaseHelper.COLUMN_NAME, name);
        long insertId = database.insert(ChoresDatabaseHelper.CHORES_TABLE_NAME, null, values);
        Cursor cursor = database.query(ChoresDatabaseHelper.CHORES_TABLE_NAME,
                allColumns, BaseColumns._ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Chore newChore = cursorToChore(cursor);
        cursor.close();
        return newChore;
    }

    public void deleteChore(Chore chore) {
        deleteChore(chore.getId());
    }

    public void deleteChore(long choreId) {
        int result = database.delete(ChoresDatabaseHelper.CHORES_TABLE_NAME, BaseColumns._ID + " = " + choreId, null);
        if (result == 1) {
            System.out.println("Chore deleted with id: " + choreId);
        } else {
            System.out.println("ERROR: Problem deleting Chore with id: " + choreId);
        }
    }

    public void editChore(long choreId, String choreName) {
        ContentValues args = new ContentValues();
        args.put(ChoresDatabaseHelper.COLUMN_NAME, choreName);
        database.update(ChoresDatabaseHelper.CHORES_TABLE_NAME, args, BaseColumns._ID + " = " + choreId, null);
    }

    public void completeChore(long choreId, boolean completed) {
        ContentValues args = new ContentValues();
        args.put(ChoresDatabaseHelper.COLUMN_COMPLETED, completed);
        database.update(ChoresDatabaseHelper.CHORES_TABLE_NAME, args, BaseColumns._ID + " = " + choreId, null);
    }

    public List<Chore> getChores(ChoreType choreType) {
        List<Chore> chores = new ArrayList<Chore>();
        String selectionClause = null;
        String[] selectionArgs = null;
        if (choreType != ChoreType.ALL) {
            selectionClause = ChoresDatabaseHelper.COLUMN_COMPLETED + "=?";
            selectionArgs = new String[]{choreType.queryValue};
        }
        Cursor cursor = database.query(ChoresDatabaseHelper.CHORES_TABLE_NAME,
                allColumns, selectionClause, selectionArgs, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Chore chore = cursorToChore(cursor);
            chores.add(chore);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return chores;
    }

    private Chore cursorToChore(Cursor cursor) {
        Chore chore = new Chore();
        chore.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
        chore.setName(cursor.getString(cursor.getColumnIndex(ChoresDatabaseHelper.COLUMN_NAME)));
        chore.setCompleted(cursor.getInt(cursor.getColumnIndex(ChoresDatabaseHelper.COLUMN_COMPLETED)) == 1 ? true : false);
        chore.setParentID(cursor.getLong(cursor.getColumnIndex(ChoresDatabaseHelper.COLUMN_PARENT_TASK_ID)));
        chore.setCreationDate(ChoresDatabaseHelper.getDate(cursor, ChoresDatabaseHelper.COLUMN_DATE_CREATED));
        chore.setCompletionDate(ChoresDatabaseHelper.getDate(cursor, ChoresDatabaseHelper.COLUMN_DATE_COMPLETED));

        return chore;
    }
}

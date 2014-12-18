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

    // Database fields
    private SQLiteDatabase   database;
    private ChoresDatabaseHelper dbHelper;
    private String[]         allColumns = ChoresDatabaseHelper.getColumnIds();

    public ChoresDataSource(Context context) {
        dbHelper = new ChoresDatabaseHelper(context);
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
        long id = chore.getId();
        int result = database.delete(ChoresDatabaseHelper.CHORES_TABLE_NAME, BaseColumns._ID + " = " + id, null);
        if (result == 1) {
            System.out.println("Chore deleted with id: " + id);
        } else {
            System.out.println("ERROR: Problem deleting Chore with id: " + id);
        }
    }

    public List<Chore> getAllChores() {
        List<Chore> chores = new ArrayList<Chore>();

        Cursor cursor = database.query(ChoresDatabaseHelper.CHORES_TABLE_NAME,
                allColumns, null, null, null, null, null);

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
        chore.setId(cursor.getLong(0));
        chore.setName(cursor.getString(1));
        return chore;
    }
}

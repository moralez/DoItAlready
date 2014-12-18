package brg.com.doitalready;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Created by jmo on 12/17/2014.
 */
public class ChoresDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME                 = "doitalready.db";
    private static final int    DATABASE_VERSION              = 1;
    public  static final String CHORES_TABLE_NAME             = "chores";
    private static final String CHORES_TABLE_CREATE_BEGIN     = "CREATE TABLE " + CHORES_TABLE_NAME + " (";
    private static final String CHORES_TABLE_CREATE_END       =  ");";

    public static final String COLUMN_NAME                    = "NAME";
    public static final String COLUMN_DATE_CREATED            = "DATE_CREATED";
    public static final String COLUMN_DATE_COMPLETED          = "DATE_COMPLETED";
    public static final String COLUMN_COMPLETED               = "COMPLETED";
    public static final String COLUMN_PARENT_TASK_ID          = "NAME";

    ChoresDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int currVersion = oldVersion;
        while (currVersion < newVersion) {
            switch (currVersion) {
                default:
                    break;
            }
        }
    }

    private String getCreateQuery() {
        HashMap<String, String> tableColumns = getChoresTableColumns();
        StringBuilder createQuery = new StringBuilder(CHORES_TABLE_CREATE_BEGIN);
        Iterator it = tableColumns.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            createQuery.append(pairs.getKey());
            createQuery.append(' ');
            createQuery.append(pairs.getValue());
            if (it.hasNext()) {
                createQuery.append(", ");
            }
            it.remove();
        }
        createQuery.append(CHORES_TABLE_CREATE_END);

        Log.i("JMO", "Final Create Query: " + createQuery.toString());

        return createQuery.toString();
    }

    private static HashMap<String, String> getChoresTableColumns() {
        HashMap<String, String> columns = new HashMap<>();

        columns.put(BaseColumns._ID,       "INTEGER PRIMARY KEY AUTOINCREMENT");
        columns.put(COLUMN_NAME,           "TEXT");
        columns.put(COLUMN_DATE_CREATED,   "DATETIME DEFAULT CURRENT_TIMESTAMP");
        columns.put(COLUMN_DATE_COMPLETED, "DATETIME");
        columns.put(COLUMN_COMPLETED,      "BOOLEAN");
        columns.put(COLUMN_PARENT_TASK_ID, "INTEGER");

        return columns;
    }

    public static String[] getColumnIds() {
        Set<String> keys = getChoresTableColumns().keySet();
        return keys.toArray(new String[keys.size()]);
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
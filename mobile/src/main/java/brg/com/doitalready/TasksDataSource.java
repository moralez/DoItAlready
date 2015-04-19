package brg.com.doitalready;

/**
 * Created by jmo on 12/17/2014.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import brg.com.doitalready.model.Task;

public class TasksDataSource {

    private static TasksDataSource instance = null;

    private SQLiteDatabase database;
    private TasksDatabaseHelper dbHelper;
    private String[] allColumns = TasksDatabaseHelper.getColumnIds();

    public enum TaskType {
        ALL(0, ""),
        COMPLETED(1, "TRUE"),
        INCOMPLETE(2, "FALSE");

        private final int value;
        private final String queryValue;

        TaskType(int value, String queryValue) {
            this.value = value;
            this.queryValue = queryValue;
        }
    };

    private TasksDataSource(Context context) {
        dbHelper = new TasksDatabaseHelper(context);
    }

    public static TasksDataSource getInstance(Context context) {
        if (instance == null) {
            instance = new TasksDataSource(context);
        }
        return instance;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Task createTask(String name) {
        ContentValues values = new ContentValues();
        values.put(TasksDatabaseHelper.COLUMN_NAME, name);
        long insertId = database.insert(TasksDatabaseHelper.TASKS_TABLE_NAME, null, values);
        Cursor cursor = database.query(TasksDatabaseHelper.TASKS_TABLE_NAME,
                allColumns, BaseColumns._ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Task newTask = cursorToTask(cursor);
        cursor.close();
        return newTask;
    }

    public void deleteTask(Task task) {
        deleteTask(task.getId());
    }

    public void deleteTask(long taskId) {
        int result = database.delete(TasksDatabaseHelper.TASKS_TABLE_NAME, BaseColumns._ID + " = " + taskId, null);
        if (result == 1) {
            System.out.println("Task deleted with id: " + taskId);
        } else {
            System.out.println("ERROR: Problem deleting Task with id: " + taskId);
        }
    }

    public void editTask(long taskId, String taskName) {
        ContentValues args = new ContentValues();
        args.put(TasksDatabaseHelper.COLUMN_NAME, taskName);
        database.update(TasksDatabaseHelper.TASKS_TABLE_NAME, args, BaseColumns._ID + " = " + taskId, null);
    }

    public void completeTask(long taskId, boolean completed) {
        ContentValues args = new ContentValues();
        args.put(TasksDatabaseHelper.COLUMN_COMPLETED, completed);
        database.update(TasksDatabaseHelper.TASKS_TABLE_NAME, args, BaseColumns._ID + " = " + taskId, null);
    }

    public List<Task> getTasks(TaskType taskType) {
        List<Task> tasks = new ArrayList<>();
        String selectionClause = null;
        String[] selectionArgs = null;
        String groupByClause = null;
        String orderByClause = null;
        if (taskType != TaskType.ALL) {
            selectionClause = TasksDatabaseHelper.COLUMN_COMPLETED + "=?";
            selectionArgs = new String[]{taskType.queryValue};
        } else {
            orderByClause = TasksDatabaseHelper.COLUMN_COMPLETED + " ASC";
        }
        Cursor cursor = database.query(TasksDatabaseHelper.TASKS_TABLE_NAME,
                allColumns, selectionClause, selectionArgs, groupByClause, null, orderByClause);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return tasks;
    }

    public long getNumberOfTasks(TaskType taskType) {
        String selectionClause = null;
        String[] selectionArgs = null;
        if (taskType != TaskType.ALL) {
            selectionClause = TasksDatabaseHelper.COLUMN_COMPLETED + "=?";
            selectionArgs = new String[]{taskType.queryValue};
        }
        return DatabaseUtils.queryNumEntries(database, TasksDatabaseHelper.TASKS_TABLE_NAME, selectionClause, selectionArgs);
    }

    private Task cursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
        task.setName(cursor.getString(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_NAME)));
        task.setCompleted(cursor.getInt(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_COMPLETED)) == 1 ? true : false);
        task.setParentID(cursor.getLong(cursor.getColumnIndex(TasksDatabaseHelper.COLUMN_PARENT_TASK_ID)));
        task.setCreationDate(TasksDatabaseHelper.getDate(cursor, TasksDatabaseHelper.COLUMN_DATE_CREATED));
        task.setCompletionDate(TasksDatabaseHelper.getDate(cursor, TasksDatabaseHelper.COLUMN_DATE_COMPLETED));

        return task;
    }
}

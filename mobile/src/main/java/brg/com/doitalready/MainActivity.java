package brg.com.doitalready;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import brg.com.doitalready.model.Task;

public class MainActivity extends Activity {

    private TasksDataSource mDatasource;
    private ImageButton      mFab;

    private RecyclerView               mRecyclerView;
    private TaskRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFab          = (ImageButton)findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mDrawerList   = (ListView)findViewById(R.id.navigation_drawer);

        mDatasource = TasksDataSource.getInstance(this);
        mDatasource.open();

        List<Task> tasks = mDatasource.getTasks(TasksDataSource.TaskType.ALL);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new TaskRecyclerViewAdapter(tasks, TasksDataSource.TaskType.ALL);
        mRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog taskEntryDialog = new Dialog(MainActivity.this);
                taskEntryDialog.setContentView(R.layout.dialog_new_task);
                taskEntryDialog.setTitle(getString(R.string.add_task_dialog_title));
                taskEntryDialog.setCancelable(true);
                taskEntryDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });
                taskEntryDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        taskEntryDialog.dismiss();
                    }
                });
                taskEntryDialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) taskEntryDialog.findViewById(R.id.task);
                        if (editText != null) {
                            String value = editText.getText().toString();
                            if (value.isEmpty()) {
                                Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.empty_task_description_error),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                                Task task = mDatasource.createTask(value);
                                mAdapter.addItem(task);

                                taskEntryDialog.dismiss();
                            }
                        }
                    }
                });
                taskEntryDialog.show();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                                                        getResources().getStringArray(R.array.main_navigation_options)));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("JMO", "Selected Position: " + position);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        mDatasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mDatasource.close();
        super.onPause();
    }

    private class TaskAdapter extends ArrayAdapter<Task> {

        public TaskAdapter(Context context, int resource, int textViewResourceId, List<Task> objects) {
            super(context, resource, textViewResourceId, objects);
        }
    }
}

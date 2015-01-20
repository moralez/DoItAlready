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

import brg.com.doitalready.model.Chore;

public class MainActivity extends Activity {

    private ChoresDataSource mDatasource;
    private ImageButton      mFab;

    private RecyclerView               mRecyclerView;
    private ChoreRecyclerViewAdapter   mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFab          = (ImageButton)findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        mDrawerList   = (ListView)findViewById(R.id.navigation_drawer);

        mDatasource = new ChoresDataSource(this);
        mDatasource.open();

        List<Chore> chores = mDatasource.getAllChores();

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ChoreRecyclerViewAdapter(chores);
        mRecyclerView.setAdapter(mAdapter);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog choreEntryDialog = new Dialog(MainActivity.this);
                choreEntryDialog.setContentView(R.layout.dialog_new_chore);
                choreEntryDialog.setTitle(getString(R.string.add_chore_dialog_title));
                choreEntryDialog.setCancelable(true);
                choreEntryDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                });
                choreEntryDialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        choreEntryDialog.dismiss();
                    }
                });
                choreEntryDialog.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) choreEntryDialog.findViewById(R.id.chore);
                        if (editText != null) {
                            String value = editText.getText().toString();
                            if (value.isEmpty()) {
                                Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.empty_chore_description_error),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                                Chore chore = mDatasource.createChore(value);
                                mAdapter.addItem(chore);

                                choreEntryDialog.dismiss();
                            }
                        }
                    }
                });
                choreEntryDialog.show();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                                                        getResources().getStringArray(R.array.main_navigation_options)));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("JMO", "Selected Position: " + position);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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

    private class ChoreAdapter extends ArrayAdapter<Chore> {

        public ChoreAdapter(Context context, int resource, int textViewResourceId, List<Chore> objects) {
            super(context, resource, textViewResourceId, objects);
        }
    }
}

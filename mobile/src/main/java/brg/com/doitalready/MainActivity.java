package brg.com.doitalready;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import brg.com.doitalready.model.Chore;

public class MainActivity extends Activity {

    private ChoresDataSource mDatasource;
    private ImageButton      mFab;

    private RecyclerView               mRecyclerView;
    private ChoreRecyclerViewAdapter   mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFab          = (ImageButton)findViewById(R.id.fab);
        mRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

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
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();

                alert.setTitle(getString(R.string.add_chore_dialog_title));

                // Set custom view for dialog
                final View dialogView = inflater.inflate(R.layout.dialog_new_chore, null);
                alert.setView(dialogView);

                alert.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText editText = (EditText)dialogView.findViewById(R.id.chore);
                        if (editText != null) {
                            String value = editText.getText().toString();
                            Chore chore = mDatasource.createChore(value);
//                            ((ArrayAdapter<Chore>)listView.getAdapter()).add(chore);
                            mAdapter.addItem(chore);
                        }
                    }
                });

                alert.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        return;
                    }
                });
                alert.show();
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

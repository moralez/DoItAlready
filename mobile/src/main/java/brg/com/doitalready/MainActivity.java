package brg.com.doitalready;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import brg.com.doitalready.model.Chore;

public class MainActivity extends Activity {

    private ChoresDataSource datasource;
    private ImageButton fab;

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);
        fab = (ImageButton)findViewById(R.id.fab);

        datasource = new ChoresDataSource(this);
        datasource.open();

        List<Chore> chores = datasource.getAllChores();

        ArrayAdapter<Chore> adapter = new ArrayAdapter<Chore>(this, android.R.layout.simple_list_item_1, chores);
        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();

                alert.setTitle(getString(R.string.add_chore_dialog_title));

                // Set custom view for dialog
                alert.setView(inflater.inflate(R.layout.dialog_new_chore, null));

                alert.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = ((EditText)findViewById(R.id.chore)).getText().toString();
                        Log.d("JMO", "Name: " + value);
                        Chore chore = datasource.createChore(value);
                        ((ArrayAdapter<Chore>)listView.getAdapter()).add(chore);
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
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }
}

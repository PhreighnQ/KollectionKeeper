package com.example.phreighnq.kollectionkeeper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;
import com.example.phreighnq.kollectionkeeper.document.Collection;
import com.example.phreighnq.kollectionkeeper.helper.LiveQueryAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private Context _Context;
    private Manager _Manager;
    private Database _Database;
    final static String TAG = "MainActivity";
    final static String DB_NAME = "collection_organizer";

    public LiveQuery collectionLiveQuery;
    public List<String> collectionNames= new ArrayList<String>();

    private Database getDatabase() {
        Application application = (Application) getApplication();
        return application.getDatabase();
    }

    public void loadCollections() {
        ListView collectionList = ((ListView) findViewById(R.id.collectionList));
        Database database = getDatabase();
        View view = database.getView(Application.Views.CollectionsByAlphabet);
        Query query = view.createQuery();
        query.setLimit(100);

        //LiveQueryAdapter liveQueryAdapter = new LiveQueryAdapter(this, query.toLiveQuery());

        //collectionList.setAdapter(liveQueryAdapter);

        List<Document> collections = Collection.getCollectionList(database);
        for(Iterator<Document> i = collections.iterator(); i.hasNext(); ) {
            Document item = i.next();
            Object name = item.getProperty("name");
            if(name!=null) {
                collectionNames.add(name.toString());
            }
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, collectionNames);
        collectionList.setAdapter(arrayAdapter);

        collectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedCollection=collectionNames.get(position);
                Toast.makeText(getApplicationContext(), "Kollection Selected : " + selectedCollection, Toast.LENGTH_LONG).show();
            }
        });

        collectionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedCollection=collectionNames.get(position);
                Toast.makeText(getApplicationContext(), "Kollection Long Clicked : " + selectedCollection, Toast.LENGTH_LONG).show();
                return false;
            }
        });

    }

    public void onCreateButtonClicked(android.view.View view) {
        EditText editText = (EditText)findViewById(R.id.createText);
        String newCollectionName = editText.getText().toString();
        if(newCollectionName.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Unable to Create Collection")
                    .setMessage("No name provided for new collection.")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }
        Database database = getDatabase();
        Document existingCollection = Collection.getCollection(database, newCollectionName);
        if(existingCollection != null) {
            Log.d(Application.TAG, String.format("Collection '%s' already exists.", newCollectionName));
            Toast.makeText(getApplicationContext(), "Collection '" + newCollectionName + "' already exists.",   Toast.LENGTH_LONG).show();
            return;
        }
        Collection.createCollection(database, newCollectionName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(Application.TAG, "MainActivity State: onCreate()");

        setContentView(R.layout.activity_main);

        loadCollections();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Application.TAG, "MainActivity State: onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(Application.TAG, "MainActivity State: onRestart()");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Application.TAG, "MainActivity State: onResume()");

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(Application.TAG, "MainActivity State: onPostResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Application.TAG, "MainActivity State: onPause()");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Application.TAG, "MainActivity State: onStop()");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(Application.TAG, "MainActivity State: onDestroy()");

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

}

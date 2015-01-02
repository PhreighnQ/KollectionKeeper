package com.example.phreighnq.kollectionkeeper;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PhreighnQ on 12/30/2014.
 */
public class DatabaseAccess {
    private Context _Context;
    private Manager _Manager;
    private Database _Database;
    final static String TAG = "DatabaseAccess";

    public DatabaseAccess(Context context) {
        if(context==null) {
            throw new IllegalArgumentException("context can not be null");
        }
        _Context = context;

        getManager();
        if (_Manager == null) {
            throw new RuntimeException("Unable to create Couchbase lite manager");
        }
    }

    public DatabaseAccess(Context context, String databaseName) {
        this(context);

        if (databaseName == null) {
            throw new IllegalArgumentException("databaseName can not be null");
        }

        getDatabase(databaseName);
        if (_Database == null) {
            throw new RuntimeException(String.format("Unable to open Couchbase lite database (%s)", databaseName));
        }

    }

    protected void getManager() {
        // create a _Manager
        try
        {
            _Manager = new Manager(new AndroidContext(_Context), Manager.DEFAULT_OPTIONS);
            Log.d(TAG, "_Manager created");
        } catch (
                IOException e
                )

        {
            Log.e(TAG, "Cannot create _Manager object");
        }
    }

    protected void getDatabase(String databaseName) {
        // create a name for the _Database and make sure the name is legal
        String dbName = "hello";//collection_organizer
        if (!Manager.isValidDatabaseName(dbName))

        {
            Log.e(TAG, "Bad database name");
            return;
        }
        // create a new _Database
        try
        {
            _Database = _Manager.getDatabase(dbName);
            Log.d(TAG, String.format("Database (%s) created", dbName));
        } catch (
                CouchbaseLiteException e
                )

        {
            Log.e(TAG, String.format("Cannot get database (%s)", dbName));
            return;
        }
    }

    protected String addDocument() {
        if (_Manager == null) {
            getManager();
            throw new RuntimeException("Unable to create Couchbase lite manager");
        }

        // get the current date and time
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());
        // create an object that contains data for a document
        Map<String, Object> docContent = new HashMap<String, Object>();
        docContent.put("message", "Hello Couchbase Lite");
        docContent.put("creationDate", currentTimeString);
        // display the data for the new document
        Log.d(TAG, "docContent=" + String.valueOf(docContent));
        // create an empty document
        Document document = _Database.createDocument();
        // add content to document and write the document to the _Database
        try

        {
            document.putProperties(docContent);
            Log.d(TAG, "Document written to _Database named " + _Database.getName() + " with ID = " + document.getId());
        } catch (
                CouchbaseLiteException e
                )

        {
            Log.e(TAG, "Cannot write document to _Database", e);
        }

        // save the ID of the new document
        String docID = document.getId();

        return docID;
    }

    public Document getDocument(String docID) {
        // retrieve the document from the _Database
        Document retrievedDocument = _Database.getDocument(docID);
        // display the retrieved document
        Log.d(TAG, "retrievedDocument=" + String.valueOf(retrievedDocument.getProperties()));

        return retrievedDocument;
    }

    public void updateDocument(Document document) {
        // update the document
        Map<String, Object> updatedProperties = new HashMap<String, Object>();
        updatedProperties.putAll(document.getProperties());
        updatedProperties.put("message", "We're having a heat wave!");
        updatedProperties.put("temperature", "95");
        try

        {
            document.putProperties(updatedProperties);
            Log.d(TAG, "updated retrievedDocument=" + String.valueOf(document.getProperties()));
        } catch (
                CouchbaseLiteException e
                )

        {
            Log.e(TAG, "Cannot update document", e);
        }
    }

    public boolean deleteDocument(String documentID) {
        Document document = getDocument(documentID);
        return deleteDocument(document);
    }

    public boolean deleteDocument(Document document) {
        // delete the document
        try

        {
            document.delete();
            Log.d(TAG, "Deleted document, deletion status = " + document.isDeleted());
            return true;
        } catch (
                CouchbaseLiteException e
                )

        {
            Log.e(TAG, "Cannot delete document", e);
            return false;
        }
    }

}

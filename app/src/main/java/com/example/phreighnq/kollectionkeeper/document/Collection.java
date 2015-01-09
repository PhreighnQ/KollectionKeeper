package com.example.phreighnq.kollectionkeeper.document;

import android.support.annotation.NonNull;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.util.Log;
import com.example.phreighnq.kollectionkeeper.Application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by PhreighnQ on 12/30/2014.
 */
public class Collection {
    private static final String VIEW_NAME = "collections";
    private static final String DOC_TYPE = "collection";

    public String Name;
    public List<Item> Items;

    public static List<Document> getCollectionList(Database database) {
        List<Document> collections =  new ArrayList<Document>();

        Query query = getAllCollections(database);
        if(query==null) return collections;

        QueryEnumerator result;
        try {
            result = query.run();
        } catch(CouchbaseLiteException e) {
            Log.d(Application.TAG, e.getMessage());
            return collections;
        }

        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            Document doc = it.next().getDocument();
            collections.add(doc);
        }

        return collections;
    }

    public static Query getAllCollections(Database database) {
        com.couchbase.lite.View view = database.getView(VIEW_NAME);
        if (view.getMap() == null) {
            Mapper mapper = new Mapper() {
                public void map(Map<String, Object> document, Emitter emitter) {
                    String type = (String)document.get("type");
                    if (DOC_TYPE.equals(type)) {
                        emitter.emit(document.get("name"), document);
                    }
                }
            };
            view.setMap(mapper, "1");
        }

        try {
            Query query = view.createQuery();
            return query;
        } catch(Exception e) {
            Log.d(Application.TAG, e.getMessage());
            return null;
        }
    }

    public static Document createCollection(Database database, String name) {
//        if(getCollection(database, name)!=null) {
//            Log.d(Application.TAG, "Collection already exists");
//            return null;
//        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Calendar calendar = GregorianCalendar.getInstance();
        String currentTimeString = dateFormatter.format(calendar.getTime());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", DOC_TYPE);
        properties.put("name", name);
        properties.put("created_at", currentTimeString);
        properties.put("items", new ArrayList<Item>());

        Document document = database.createDocument();
        try {
            document.putProperties(properties);
        } catch(CouchbaseLiteException e) {
            Log.d(Application.TAG, e.getMessage());
            return null;
        }

        return document;
    }

    public static Document getCollection(Database database, String name) {
        Query query = getAllCollections(database);
        if(query==null) return null;

        QueryEnumerator result;
        try {
            result = query.run();
        } catch(CouchbaseLiteException e) {
            Log.d(Application.TAG, e.getMessage());
            return null;
        }
        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            Document doc = it.next().getDocument();
            Object nameProp = doc.getProperty("name");
            if(nameProp!=null) {
                String collectionName = nameProp.toString();
                if (collectionName.equalsIgnoreCase(name)) {
                    return doc;
                }
            }
        }

        return null;
    }

}

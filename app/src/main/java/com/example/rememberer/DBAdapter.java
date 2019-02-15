package com.example.rememberer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter {

    static final String TAG = "DBAdapter";

    // database name
    static final String DATABASE_NAME = "RemembererDB";
    static final int DATABASE_VERSION = 2;
    // table names
    static final String TABLE_ITEM_HISTORY = "ITEM_HISTORY";
    static final String TABLE_ITEM = "ITEM";
    // attribute names
    static final String KEY_ID_ITEM_HISTORY = "id_item_history";
    static final String KEY_NAME = "name";
    static final String KEY_RATING = "rating";
    static final String KEY_LONGITUDE = "longitude";
    static final String KEY_LATITUDE = "latitude";
    static final String KEY_IMG_NAME = "img_name";
    static final String KEY_ID_ITEM = "id_item";
    // first i last tstamp

    // create queries
    static final String DB_CREATE_ITEM_HISTORY =
            "create table ITEM_HISTORY(id_item_history integer primary key autoincrement, " +
                    "name text not null, " +
                    "rating integer not null, " +
                    "longitude double not null, " +
                    "latitude double not null, " +
                    "img_name text not null, " +
                    "id_item integer not null" +
                    ")";
    static final String DB_CREATE_ITEM =
            "create table ITEM(" +
                    "id_item integer primary key autoincrement" +
                    ")";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx){
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper{
        DatabaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                db.execSQL(DB_CREATE_ITEM_HISTORY);
                db.execSQL(DB_CREATE_ITEM);
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading db from " + oldVersion + " to " +
                    newVersion);
            db.execSQL("DROP TABLE IF EXISTS ITEM_HISTORY");
            db.execSQL("DROP TABLE IF EXISTS ITEM");
            onCreate(db);
        }
    }

    public DBAdapter open() throws SQLException{
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        DBHelper.close();
    }

    // NOVI ITEM
    // insert u item
    // dohvatim njegov id iz item
    // spremim podatke u item_history


    // novi history od itema
    // activity s povjescu tog itema (+)
    // imam id_item i id_history
    // bez insertIntoItem, samo insertIntoItemHistory

    // update itema
    // activity s detaljima itema


    public long insertIntoItem(){

        ContentValues val = new ContentValues();
        val.put(KEY_ID_ITEM, -1);
        return db.insert(TABLE_ITEM, null, val);
    }

    public long insertIntoItem(int id_item){
        ContentValues val = new ContentValues();
        val.put(KEY_ID_ITEM, id_item);
        return db.insert(TABLE_ITEM, null, val);
    }

    public long insertIntoItemHistory(String name, int rating, double longitude,
                                      double latitude, String img_name, int id_item){
        ContentValues initialValues = new ContentValues();

        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_RATING, rating);
        initialValues.put(KEY_LONGITUDE, longitude);
        initialValues.put(KEY_LATITUDE, latitude);
        initialValues.put(KEY_IMG_NAME, img_name);
        initialValues.put(KEY_ID_ITEM, id_item);

        return db.insert(TABLE_ITEM_HISTORY, null, initialValues);
    }

    public boolean updateItemHistory(int id_item_history, String name, int rating,
                                     double longitude, double latitude, String img_name){
        ContentValues val = new ContentValues();
        val.put(KEY_NAME, name);
        val.put(KEY_RATING, rating);
        val.put(KEY_LONGITUDE, longitude);
        val.put(KEY_LATITUDE, latitude);
        val.put(KEY_IMG_NAME, img_name);
        return db.update(TABLE_ITEM_HISTORY, val,
                KEY_ID_ITEM_HISTORY + " = " + id_item_history, null) > 0;
    }

    public boolean deleteItemHistory(int id_item_history){
        return db.delete(TABLE_ITEM_HISTORY, KEY_ID_ITEM_HISTORY + " = " + id_item_history, null) > 0;
    }

    public boolean deleteAllItemHistory(int id_item){
        return db.delete(TABLE_ITEM_HISTORY, KEY_ID_ITEM + " = " + id_item, null) > 0;
    }

    public boolean deleteItem(int id_item) {
        return db.delete(TABLE_ITEM, KEY_ID_ITEM + " = " + id_item, null) > 0;
    }

    // DATA FOR MAIN ACTIVITY
    public ArrayList<Item> getAllCurrentItems(){
//        String joinTables = TABLE_ITEM_HISTORY +
//                " INNER JOIN " +
//                TABLE_ITEM +
//                " ON " +
//                TABLE_ITEM_HISTORY + "." + KEY_ID_ITEM +
//                " = " +
//                TABLE_ITEM + "." + KEY_ID_ITEM;
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        queryBuilder.setTables(joinTables);
//
//
//        Cursor query = queryBuilder.query(db, new String[]{
//                KEY_ID_ITEM_HISTORY,
//                KEY_NAME,
//                KEY_RATING,
//                KEY_LONGITUDE,
//                KEY_LATITUDE,
//                KEY_IMG_NAME,
//                TABLE_ITEM_HISTORY + "." + KEY_ID_ITEM
//        }, null, null, null, null, KEY_NAME);
//
//        return query;

        ArrayList<Item> items = new ArrayList<>();
        Cursor itemIdCursor = getAllItemIds();
        do{
            int idItem = itemIdCursor.getInt(0);
            Cursor tCursor = db.query(
                    false,
                    TABLE_ITEM_HISTORY,
                    new String[]{
                            KEY_ID_ITEM_HISTORY,
                            KEY_NAME,
                            KEY_RATING,
                            KEY_LONGITUDE, KEY_LATITUDE,
                            KEY_IMG_NAME,
                            KEY_ID_ITEM},
                    KEY_ID_ITEM + " = " + idItem,
                    null, null, null,  KEY_ID_ITEM_HISTORY + " DESC", "1");
            if(tCursor != null){
                tCursor.moveToFirst();
                Item item = new Item();
                item.setId_item_history(tCursor.getInt(0));
                item.setName(tCursor.getString(1));
                item.setRating(tCursor.getInt(2));
                item.setLongitude(tCursor.getDouble(3));
                item.setLatitude(tCursor.getDouble(4));
                item.setImg_name(tCursor.getString(5));
                item.setId_item(tCursor.getInt(6));
                items.add(item);
            }
        }while(itemIdCursor.moveToNext());


        return items;
    }


    public int getAllCurrentItemsSize() {
//        String joinTables = TABLE_ITEM_HISTORY +
//                " INNER JOIN " +
//                TABLE_ITEM +
//                " ON " +
//                TABLE_ITEM_HISTORY + "." + KEY_ID_ITEM +
//                " = " +
//                TABLE_ITEM + "." + KEY_ID_ITEM;
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//        queryBuilder.setTables(joinTables);
//
//        Cursor query = queryBuilder.query(db, new String[]{
//                KEY_ID_ITEM_HISTORY,
//                KEY_NAME,
//                KEY_RATING,
//                KEY_LONGITUDE,
//                KEY_LATITUDE,
//                KEY_IMG_NAME,
//                TABLE_ITEM_HISTORY + "." + KEY_ID_ITEM
//        }, null, null, null, null, KEY_NAME);
//
//        return query.getCount();
        return getAllItemIds().getCount();
    }

    // data for ItemHistoryActivity
    public Cursor getItemHistoryByIdItemHistory(int id_item_history){
        Cursor tCursor = db.query(
                true,
                TABLE_ITEM_HISTORY,
                new String[]{
                    KEY_ID_ITEM_HISTORY,
                    KEY_NAME,
                    KEY_RATING,
                    KEY_LONGITUDE, KEY_LATITUDE,
                    KEY_IMG_NAME,
                    KEY_ID_ITEM},
                KEY_ID_ITEM_HISTORY + " = " + id_item_history,
                null, null, null, null, null);
        if(tCursor != null){
            tCursor.moveToFirst();
        }
        return tCursor;
    }

    public Cursor getItemHistoryByIdItem(int id_item){

        Cursor tCursor = db.query(
                false,
                TABLE_ITEM_HISTORY,
                new String[]{
                        KEY_ID_ITEM_HISTORY,
                        KEY_NAME,
                        KEY_RATING,
                        KEY_LONGITUDE, KEY_LATITUDE,
                        KEY_IMG_NAME,
                        KEY_ID_ITEM},
                KEY_ID_ITEM + " = " + id_item,
                null, null, null, KEY_ID_ITEM_HISTORY + " desc", null);
        if(tCursor != null){
            tCursor.moveToFirst();
        }

        return tCursor;
    }

    public Cursor getItemId() {
        Cursor tCursor = db.query(TABLE_ITEM, new String[] {KEY_ID_ITEM},
            null, null, null, null, KEY_ID_ITEM + " DESC");
        if(tCursor != null){
            tCursor.moveToFirst();
        }
        return tCursor;
    }

    public Cursor getAllItemIds(){
        Cursor tCursor = db.query(TABLE_ITEM, new String[]{KEY_ID_ITEM}, null, null, null, null, null);
        if(tCursor != null){
            tCursor.moveToFirst();
        }
        return tCursor;
    }
}

package com.example.rememberer;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemHistoryActivity extends AppCompatActivity{

    private static final String ID_ITEM = "ID_ITEM";
    private static final String NAME_ITEM = "NAME_ITEM";
    private static final String ID_ITEM_HISTORY = "ID_ITEM";
    private static final String RATING_ITEM = "RATING_ITEM";
    private static final String LONGITUDE_ITEM = "LONGITUDE_ITEM";
    private static final String LATITUDE_ITEM = "LATITUDE_ITEM";
    private static final String IMG_NAME_ITEM = "IMG_NAME_ITEM";

    private int id_item = -1;
    private static  String name_item = "";

    private DBAdapter db;

    ArrayList<Item> itemHistoryList = new ArrayList<>();
    ItemListAdapter adapter_historyItems;
    ListView listview_historyItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_history);

        try{
        Bundle bundle = getIntent().getExtras();
        id_item = bundle.getInt(ID_ITEM);
        name_item = bundle.getString(NAME_ITEM);

        Toolbar toolbar = findViewById(R.id.historyToolbar);
        String newTitle = toolbar.getTitle() + " " + name_item;
        toolbar.setTitle(newTitle);
        setSupportActionBar(toolbar);

        fillItemHistoryList();

        if(itemHistoryList.size() > 0){
            // napuni listu predmeta
            listview_historyItems = getListView();

            listview_historyItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // otvori activity s detaljima pritisnutog predmeta
                    Intent intent = new Intent(view.getContext(), ItemDetailsActivity.class);

                    Bundle extras = new Bundle();
                    extras.putInt(ID_ITEM_HISTORY, itemHistoryList.get(position).getId_item_history());
                    extras.putString(NAME_ITEM, itemHistoryList.get(position).getImg_name());
                    extras.putInt(RATING_ITEM, itemHistoryList.get(position).getRating());
                    extras.putDouble(LONGITUDE_ITEM, itemHistoryList.get(position).getLongitude());
                    extras.putDouble(LATITUDE_ITEM, itemHistoryList.get(position).getLatitude());
                    extras.putString(IMG_NAME_ITEM, itemHistoryList.get(position).getImg_name());
                    extras.putInt(ID_ITEM, itemHistoryList.get(position).getId_item());

                    intent.putExtras(extras);

                    startActivity(intent);
                }
            });

            adapter_historyItems = new ItemListAdapter(this, itemHistoryList);
            listview_historyItems.setAdapter(adapter_historyItems);
//            setListViewHeightBasedOnChildren(listview_historyItems);

        }}
        catch (Exception e){}
    }

    private ListView getListView()
    {
        return (ListView)findViewById(R.id.list);
    }

    private void fillItemHistoryList(){
        db = new DBAdapter(this);
        db.open();
        Cursor cursor = db.getItemHistoryByIdItem(id_item);
        if(cursor.moveToFirst()){

            do{
                Item item = new Item();
                item.setId_item_history(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setRating(cursor.getInt(2));
                item.setLongitude(cursor.getDouble(3));
                item.setLatitude(cursor.getDouble(4));
                item.setImg_name(cursor.getString(5));
                item.setId_item(id_item);

                itemHistoryList.add(item);
            }while(cursor.moveToNext());
        }
        db.close();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.newItem){
            addItemHistory(item);
        }
        else if(id == R.id.deleteItem){
            deleteItem(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItemHistory(MenuItem item) {
        Intent intent = new Intent(this, AddNewItemActivity.class);
        intent.putExtra(ID_ITEM, id_item);
        startActivity(intent);
    }

    public void deleteItem(MenuItem item){
        DBAdapter db = new DBAdapter(this);
        db.open();
        boolean deletedHistory = db.deleteAllItemHistory(id_item);
        boolean deletedItem = db.deleteItem(id_item);
        db.close();

        if(deletedHistory && deletedItem){
            Toast.makeText(this,
                    "Item successfully deleted!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,
                    "Item NOT successfully deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        db.open();
        Cursor cursor = db.getItemHistoryByIdItem(id_item);
        ArrayList<Item> newItemHistory = new ArrayList<>();
        if(cursor.moveToFirst()){

            do{
                Item item = new Item();
                item.setId_item_history(cursor.getInt(0));
                item.setName(cursor.getString(1));
                item.setRating(cursor.getInt(2));
                item.setLongitude(cursor.getDouble(3));
                item.setLatitude(cursor.getDouble(4));
                item.setImg_name(cursor.getString(5));
                item.setId_item(id_item);

                newItemHistory.add(item);
            }while(cursor.moveToNext());
        }
        db.close();
        adapter_historyItems.setData(newItemHistory);
        adapter_historyItems.notifyDataSetChanged();
    }

}

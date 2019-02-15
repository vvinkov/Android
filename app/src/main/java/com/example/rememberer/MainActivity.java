package com.example.rememberer;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ExpandableListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity {

    private ArrayList<Item> mainItemList = new ArrayList<>();
    private static final String ID_ITEM = "ID_ITEM";
    private static final String NAME_ITEM = "NAME_ITEM";

    ItemListAdapter adapter_mainItems;

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.mainToolbar);
//        setSupportActionBar(toolbar);

        initializeDB();

        // napuni listu predmeta
        ListView listview_mainItems = getListView();
        adapter_mainItems = new ItemListAdapter(this, mainItemList);
        listview_mainItems.setAdapter(adapter_mainItems);
//        setListViewHeightBasedOnChildren(listview_mainItems);
//        listview_mainItems.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview_mainItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // otvori activity s povjescu pritisnutog predmeta
                Intent intent = new Intent(view.getContext(), ItemHistoryActivity.class);
                intent.putExtra(ID_ITEM, mainItemList.get(position).getId_item());
                intent.putExtra(NAME_ITEM, mainItemList.get(position).getName());
                startActivity(intent);
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
    protected void onResume(){
        super.onResume();
        refreshListView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.searchItem){
            searchItems(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void searchItems(MenuItem item){
//        Intent intent = new Intent(this, ItemDetailsActivity.class);
//        startActivity(intent);
    }


    public void refreshListView()
    {
        ListView lv = getListView();
        adapter_mainItems =  (ItemListAdapter)(getListView()).getAdapter();
//        runOnUiThread(new Runnable() {
//            public void run() {
//
//                adapter.setData(mainItemList);
//
//                adapter.notifyDataSetChanged();
//            }
//        });
        db.open();
        mainItemList = db.getAllCurrentItems();
        adapter_mainItems.setData(mainItemList);
        db.close();
//        lv.invalidate();
//        lv.requestLayout();
    }

    public void addNewItem(View view) {

        Intent intent = new Intent(this, AddNewItemActivity.class);
        intent.putExtra(ID_ITEM, -2);
        startActivity(intent);
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

    private void initializeDB(){
        db = new DBAdapter(this);
        db.open();
        int countOfItems = db.getAllCurrentItemsSize();
        if(countOfItems == 0) {
            long id = db.insertIntoItem();
            Cursor lastIdAdded = db.getItemId();
            long id2 = db.insertIntoItemHistory("kljucevi", 3,
                    1.00, 1.00, "kljucevi.jpg",
                    lastIdAdded.getInt(0));
        }

        mainItemList = db.getAllCurrentItems();
//        cursor.moveToFirst();
//        do{
//            Item prvi = new Item();
//            prvi.setId_item_history(cursor.getInt(0));
//            prvi.setName(cursor.getString(1));
//            prvi.setRating(cursor.getInt(2));
//            prvi.setLongitude(cursor.getDouble(3));
//            prvi.setLatitude(cursor.getDouble(4));
//            prvi.setImg_name(cursor.getString(5));
//            prvi.setId_item(cursor.getInt(6));
//            mainItemList.add(prvi);
//        } while(cursor.moveToNext());


        db.close();
    }
}

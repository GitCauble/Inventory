package com.example.ryan.inventory;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    InventoryDBHelper dbHelper;
    TheCursorAdapter adapter;
    int itemLastVisible = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new InventoryDBHelper(this);

        final FloatingActionButton zap = (FloatingActionButton) findViewById(R.id.fab);
        zap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditDetailsActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readStock();

        adapter = new TheCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int updateScroll) {
                if(updateScroll == 0) return;
                final int currentFirstVisibleItem = view.getFirstVisiblePosition();
                if (currentFirstVisibleItem > itemLastVisible) {
                    zap.show();
                } else if (currentFirstVisibleItem < itemLastVisible) {
                    zap.hide();
                }
                itemLastVisible = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readStock());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, EditDetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellOneItem(id, quantity);
        adapter.swapCursor(dbHelper.readStock());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private int deleteAll() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete(InventoryContract.StockEntry.TABLE_NAME, null, null);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dummy_data:
                // add dummy data for testing
                addDummyData();
                adapter.swapCursor(dbHelper.readStock());
                return true;

            case R.id.action_delete_all_data:
                //delete all data
                deleteAll();
                adapter.swapCursor(dbHelper.readStock());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addDummyData() {
        InventoryProvider coke = new InventoryProvider(
                "Coke",
                "$10",
                10,
                "Coca-Cola",
                "508-805-1111",
                "Coca-Cola@Coca-Cola.com",
                "android.resource://com.example.ryan.inventory/drawable/coke");
        dbHelper.insertItem(coke);

        InventoryProvider sprite = new InventoryProvider(
                "Sprite",
                "$10",
                5,
                "Coca-Cola",
                "508-805-1111",
                "Coca-Cola@Coca-Cola.com",
                "android.resource://com.example.ryan.inventory/drawable/sprite");
        dbHelper.insertItem(sprite);

        InventoryProvider vault = new InventoryProvider(
                "Vault",
                "$11",
                50,
                "Coca-Cola",
                "508-805-1111",
                "Coca-Cola@Coca-Cola.com",
                "android.resource://com.example.ryan.inventory/drawable/vault");
        dbHelper.insertItem(vault);

        InventoryProvider surge = new InventoryProvider(
                "Surge",
                "$13",
                75,
                "Coca-Cola",
                "508-805-1111",
                "Coca-Cola@Coca-Cola.com",
                "android.resource://com.example.ryan.inventory/drawable/surge");
        dbHelper.insertItem(surge);

        InventoryProvider duff = new InventoryProvider(
                "Duff",
                "$99",
                25,
                "Duff Inc.",
                "781-222-1111",
                "duff@eatyourheartout.com",
                "android.resource://com.example.ryan.inventory/drawable/duff");
        dbHelper.insertItem(duff);

    }
}
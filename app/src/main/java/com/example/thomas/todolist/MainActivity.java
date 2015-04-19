package com.example.thomas.todolist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {
    private ArrayList<HashMap<String, Object>> items;
    private SimpleAdapter itemsAdapter;
    private ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<HashMap<String, Object>>();
        readItems();
        itemsAdapter = new SimpleAdapter(this,
                items, R.layout.mylayout,
                new String[] {"IsDone", "title", "comment", "category"},
                new int[]{R.id.isDone, R.id.todoTitle, R.id.category, R.id.comment});
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        // Remove the item within array at position
                        items.remove(pos);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }
    });
    }

    public void onAddItem(View v) {
        //EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        //String itemText = etNewItem.getText().toString();
        //itemsAdapter.add(itemText);
        //etNewItem.setText("");
        //writeItems();
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

        switch (item.getItemId()){
            case R.id.menu_addItem:
                setContentView(R.layout.layout_add_item);
                return true;
            case R.id.menu_goToList:
                setContentView(R.layout.activity_main);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            ArrayList<String> lineList = new ArrayList<String>(FileUtils.readLines(todoFile));
            for (String line : lineList){
                String[] parts = line.split("|");
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("IsDone", Boolean.parseBoolean(parts[0]));
                map.put("title", parts[1]);
                map.put("comment", parts[2]);
                map.put("category", parts[3]);
                items.add(map);
            }
        } catch (IOException e) {
            items = new ArrayList<HashMap<String, Object>>();
        }
    }


    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            for (HashMap<String, Object> item : items){
                FileUtils.writeStringToFile(todoFile, String.format("%s|%s|%s|%s",
                        item.get("IsDone").toString(),
                        item.get("title"),
                        item.get("comment"),
                        item.get("category")));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

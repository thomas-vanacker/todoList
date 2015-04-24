package com.example.thomas.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    TextView mEdit;
    private ArrayList<HashMap<String, Object>> items;
    private SimpleAdapter itemsAdapter;
    private ListView lvItems;
    private int positionOnClick;
    private ListView editLvItems;


    public void populateSetDate(int year, int month, int day) {
        mEdit = (TextView) findViewById(R.id.date);
        String machin = String.format("%02d/%02d/%04d", day, month, year);
        mEdit.setText(machin);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayTodoListView();
    }


    private void createSpinner(final String[] items, final int textBoxId, int spinnerId) {
        AdapterView.OnItemSelectedListener onSpinner =
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        TextView myTextView =
                                (TextView) findViewById(textBoxId);
                        myTextView.setText(items[position]);
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                };
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                items);
        Spinner spinner = (Spinner) findViewById(spinnerId);
        spinner.setAdapter(stringArrayAdapter);
        spinner.setOnItemSelectedListener(onSpinner);
    }

    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    private void displayTodoListView() {
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<>();
        readItems();
        itemsAdapter = new SimpleAdapter(this,
                items, R.layout.mylayout,
                new String[]{"isDone", "title", "comment", "category", "date"},
                new int[]{R.id.isDone, R.id.todoTitle, R.id.category, R.id.comment, R.id.deadline});
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private void displayEditTodoListView() {
        setContentView(R.layout.edittodolayout);
    }

    public void onTodoItemFinished(View view) {
        //Ceci est un commentaire
        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()) {
            int pos = lvItems.getPositionForView(view);
            items.remove(pos);
            // Refresh the adapter
            itemsAdapter.notifyDataSetChanged();
            writeItems();
        }
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
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionOnClick = position;
                Object listItem = lvItems.getItemAtPosition(positionOnClick);

            }
        });
    }

    public void onAddItem(View v) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isDone", false);
        map.put("title", ((EditText) findViewById(R.id.newItemTitle)).getText());
        map.put("comment", ((EditText) findViewById(R.id.newItemComment)).getText());
        map.put("category", ((EditText) findViewById(R.id.newItemCategory)).getText());
        map.put("date", ((TextView) findViewById(R.id.date)).getText());
        map.put("periodicity", ((TextView) findViewById(R.id.periodicity)).getText());
        items.add(map);
        writeItems();
        // Retourne à la liste des items
        displayTodoListView();
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
                String[] spinnerList = {"Canada", "Mexico", "USA"};
                createSpinner(spinnerList, R.id.newItemCategory, R.id.spinnerCategory);
                createSpinner(spinnerList, R.id.periodicity, R.id.spinnerPeriodicity);
                return true;
            case R.id.menu_goToList:
                displayTodoListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            ArrayList<String> lineList = new ArrayList<>(FileUtils.readLines(todoFile));
            for (String line : lineList){
                String[] parts = line.split("\\|");
                HashMap<String, Object> map = new HashMap<>();
                map.put("isDone", Boolean.parseBoolean(parts[0]));
                map.put("title", parts[1]);
                map.put("comment", parts[2]);
                map.put("category", parts[3]);
                if (parts.length == 4) {
                    map.put("date", "");
                    map.put("periodicity", "");
                } else {
                    map.put("date", parts[4]);
                    map.put("periodicity", parts[5]);
                }
                items.add(map);
            }
        } catch (IOException e) {
            items = new ArrayList<>();
        }
    }


    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            List<String> lines = new ArrayList<>();
            for (HashMap<String, Object> item : items){
                lines.add(String.format("%s|%s|%s|%s|%s|%s",
                        item.get("isDone").toString(),
                        item.get("title"),
                        item.get("comment"),
                        item.get("category"),
                        item.get("date"),
                        item.get("periodicity")));
            }
            FileUtils.writeLines(todoFile, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

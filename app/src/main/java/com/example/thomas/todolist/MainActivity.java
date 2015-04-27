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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    TextView mEdit;
    private ArrayList<HashMap<String, Object>> items;
    private SimpleAdapter itemsAdapter;
    private ListView lvItems;
    private String[] spinnerListCategory;
    private String[] spinnerListPeriodicity;
    private int positionOnClick;
    private ListView editLvItems;
    private ArrayList<HashMap<String, Object>> subItems;
    private SimpleAdapter editItemsAdapter;


    public void populateSetDate(int year, int month, int day) {
        mEdit = (TextView) findViewById(R.id.itemDate);
        mEdit.setText(String.format("%02d/%02d/%04d", day, month, year));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spinnerListCategory = new String[]{"Course", "Travail", "Shopping", "Banque"};
        spinnerListPeriodicity = new String[]{"par jour", "par jour ouvrable", "par semaine", "par mois", "par an"};
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
        periodicityUpdate();
        writeItems();
        itemsAdapter = new SimpleAdapter(this,
                items, R.layout.mylayout,
                new String[]{"isDone", "title", "comment", "category", "date"},
                new int[]{R.id.isDone, R.id.todoTitle, R.id.category, R.id.comment, R.id.deadline});
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private void displayEditTodoListView() {
        setContentView(R.layout.edittodolayout);
        editLvItems = (ListView) findViewById(R.id.editItemListView);
        subItems = new ArrayList<>();
        editItemsAdapter = new SimpleAdapter(this,
                subItems,
                R.layout.sub_task_layout,
                new String[]{"isDone", "title"},
                new int[]{R.id.subTaskIsDone, R.id.subTaskTitle});
        editLvItems.setAdapter(editItemsAdapter);
        setupListViewListener();
    }

    public void onTodoItemFinished(View view) {
        //Ceci est un commentaire
        CheckBox checkBox = (CheckBox) view;
        if (checkBox.isChecked()) {
            int pos = lvItems.getPositionForView(view);
            HashMap<String, Object> map = items.get(pos);
            map.put("isDone", checkBox.isChecked());
            items.remove(pos);
            items.add(pos, map);
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
                HashMap<String, Object> listItem = items.get(positionOnClick);
                displayEditTodoListView();
                populateEditView(listItem);
            }
        });
        editLvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        // Remove the item within array at position
                        subItems.remove(pos);
                        // Refresh the adapter
                        editItemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }
                });
    }

    private void populateEditView(HashMap<String, Object> item) {
        EditText mEditTitle = (EditText) findViewById(R.id.editItemTitle);
        mEditTitle.setText((String) item.get("title"));
        EditText mEditCategory = (EditText) findViewById(R.id.editItemCategory);
        mEditCategory.setText((String) item.get("category"));
        EditText mEditComment = (EditText) findViewById(R.id.editItemComment);
        mEditComment.setText((String) item.get("comment"));
        TextView mEditDate = (TextView) findViewById(R.id.editItemDate);
        mEditDate.setText((String) item.get("date"));
        TextView mEditPeriodicity = (TextView) findViewById(R.id.editItemPeriodicity);
        mEditPeriodicity.setText((String) item.get("periodicity"));
        createSpinner(spinnerListCategory, R.id.editItemCategory, R.id.spinnerCategoryEdit);
        createSpinner(spinnerListPeriodicity, R.id.editItemPeriodicity, R.id.spinnerPeriodicityEdit);
        subItems = (ArrayList<HashMap<String, Object>>) item.get("subTaskList");
        editItemsAdapter.notifyDataSetChanged();


    }

    public void onAddSubItem(View v) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isDone", false);
        map.put("title", ((EditText) findViewById(R.id.addSubTaskTitle)).getText());
        subItems.add(map);
    }

    public void onEditItem(View v) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isDone", false);
        map.put("title", ((EditText) findViewById(R.id.editItemTitle)).getText());
        map.put("comment", ((EditText) findViewById(R.id.editItemComment)).getText());
        map.put("category", ((EditText) findViewById(R.id.editItemCategory)).getText());
        map.put("date", ((TextView) findViewById(R.id.editItemDate)).getText());
        map.put("periodicity", ((TextView) findViewById(R.id.editItemPeriodicity)).getText());
        map.put("subTaskList", subItems);
        items.remove(positionOnClick);
        items.add(positionOnClick, map);

    }

    public void onAddItem(View v) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("isDone", false);
        map.put("title", ((EditText) findViewById(R.id.itemTitle)).getText());
        map.put("comment", ((EditText) findViewById(R.id.itemComment)).getText());
        map.put("category", ((EditText) findViewById(R.id.itemCategory)).getText());
        map.put("date", ((TextView) findViewById(R.id.itemDate)).getText());
        map.put("periodicity", ((TextView) findViewById(R.id.itemPeriodicity)).getText());
        map.put("subTaskList", new ArrayList<HashMap<String, Object>>());
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
                createSpinner(spinnerListCategory, R.id.itemCategory, R.id.spinnerCategory);
                createSpinner(spinnerListPeriodicity, R.id.itemPeriodicity, R.id.spinnerPeriodicity);
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
                String[] first_split = line.split("#");
                String[] second_split = line.split("\\|");
                HashMap<String, Object> subMap = new HashMap<>();
                ArrayList<HashMap<String, Object>> list_sub_items = new ArrayList<HashMap<String, Object>>();
                for (String sub_items : second_split) {
                    String[] sub_parts = sub_items.split("$");
                    subMap.put("isDone", Boolean.parseBoolean(sub_parts[0]));
                    subMap.put("title", sub_parts[1]);
                    list_sub_items.add(subMap);
                }
                HashMap<String, Object> map = new HashMap<>();
                String[] parts = first_split[0].split("\\|");
                map.put("isDone", Boolean.parseBoolean(parts[0]));
                map.put("title", parts[1]);
                map.put("comment", parts[2]);
                map.put("category", parts[3]);
                map.put("date", parts[4]);
                map.put("periodicity", parts[5]);
                map.put("subTaskList", list_sub_items);
                items.add(map);
            }
        } catch (IOException e) {
            items = new ArrayList<>();
        }
    }

    private void periodicityUpdate() {
        ArrayList<HashMap<String, Object>> updateItems = new ArrayList<>();
        for (HashMap<String, Object> item : items) {
            Calendar calendar_now = Calendar.getInstance();
            Calendar calendar_set_date = Calendar.getInstance();
            String[] dateList = ((String) item.get("date")).split("\\|");
            int day_of_month = Integer.parseInt(dateList[0]);
            int month = Integer.parseInt(dateList[1]);
            int year = Integer.parseInt(dateList[2]);
            calendar_set_date.set(year, month - 1, day_of_month);
            if (calendar_now.after(calendar_set_date)) {
                if (item.get("periodicity").equals("par jour")) {
                    int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
                    int month_now = calendar_now.get(Calendar.MONTH) + 1;
                    int year_now = calendar_now.get(Calendar.YEAR);
                    item.put("date", String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
                } else if (item.get("periodicity").equals("par jour ouvrable")) {
                    int day_of_week = calendar_now.get(Calendar.DAY_OF_WEEK);
                    if (day_of_week == 0) {
                        calendar_now.roll(Calendar.DAY_OF_MONTH, 1);
                        int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
                        int month_now = calendar_now.get(Calendar.MONTH) + 1;
                        int year_now = calendar_now.get(Calendar.YEAR);
                        item.put("date", String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
                    } else if (day_of_week == 7) {
                        calendar_now.roll(Calendar.DAY_OF_MONTH, 2);
                        int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
                        int month_now = calendar_now.get(Calendar.MONTH) + 1;
                        int year_now = calendar_now.get(Calendar.YEAR);
                        item.put("date", String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
                    } else {
                        int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
                        int month_now = calendar_now.get(Calendar.MONTH) + 1;
                        int year_now = calendar_now.get(Calendar.YEAR);
                        item.put("date", String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
                    }
                } else if (item.get("periodicity").equals("par semaine")) {
                    calendar_now.roll(Calendar.DAY_OF_MONTH, 7);
                    int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
                    int month_now = calendar_now.get(Calendar.MONTH) + 1;
                    int year_now = calendar_now.get(Calendar.YEAR);
                    item.put("date", String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
                } else if (item.get("periodicity").equals("par mois")) {
                    calendar_now.roll(Calendar.MONTH, 1);
                    int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
                    int month_now = calendar_now.get(Calendar.MONTH) + 1;
                    int year_now = calendar_now.get(Calendar.YEAR);
                    item.put("date", String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
                } else if (item.get("periodicity").equals("par an")) {
                    calendar_now.roll(Calendar.YEAR, 1);
                    int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
                    int month_now = calendar_now.get(Calendar.MONTH) + 1;
                    int year_now = calendar_now.get(Calendar.YEAR);
                    item.put("date", String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
                }
            }
            updateItems.add(item);
        }
        items = updateItems;
    }


    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            List<String> lines = new ArrayList<>();
            for (HashMap<String, Object> item : items){
                ArrayList<HashMap<String, Object>> sItems = (ArrayList<HashMap<String, Object>>) item.get("subTaskList");
                String stringSubItemLine = "";
                String stringSubItem = "";
                for (HashMap<String, Object> sItem : sItems) {
                    stringSubItem = String.format("%s$%s",
                            sItem.get("isDone").toString(),
                            sItem.get("title"));
                    if (stringSubItem.equals("")) {
                        stringSubItemLine = stringSubItem;
                    } else {
                        stringSubItemLine = String.format("%s|%s",
                                stringSubItemLine,
                                stringSubItem);
                    }
                }
                lines.add(String.format("%s|%s|%s|%s|%s|%s#%s",
                        item.get("isDone").toString(),
                        item.get("title"),
                        item.get("comment"),
                        item.get("category"),
                        item.get("date"),
                        item.get("periodicity"),
                        stringSubItemLine));
            }
            FileUtils.writeLines(todoFile, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

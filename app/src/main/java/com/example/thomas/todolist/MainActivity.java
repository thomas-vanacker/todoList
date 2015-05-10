package com.example.thomas.todolist;

import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    TextView mEdit;
    private ArrayList<Task> items;
    private TaskAdapter itemsAdapter;
    private ListView lvItems;
    private String[] spinnerListCategory;
    private String[] spinnerListPeriodicity;
    private int positionOnClick;
    private ListView editLvItems;
    private ArrayList<SubTask> subItems;
    private SubTaskAdapter editItemsAdapter;

    /**
     * Permet de recuperer la date du dialogue et de la mettre dans la vue
     *
     * @param year
     * @param month
     * @param day
     */
    public void populateSetDate(int year, int month, int day) {
        mEdit = (TextView) findViewById(R.id.itemDate);
        mEdit.setText(String.format("%02d/%02d/%04d", day, month, year));
    }

    public void updateData() {
        ParseQuery<Task> query = ParseQuery.getQuery(Task.class);
        query.findInBackground(new FindCallback<Task>() {
            @Override
            public void done(List<Task> tasks, ParseException error) {
                if (tasks != null) {
                    itemsAdapter.clear();
                    itemsAdapter.addAll(tasks);
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            Parse.enableLocalDatastore(this);
            Parse.initialize(this, "8cFNytK3bv5km9UHpbEYmGddpQBFGiYFlIc6d9aZ", "m0gZ3jTZANsfDsd29i257k80D4eJ3tbqDoPPwQem");
            ParseObject.registerSubclass(Task.class);
            ParseObject.registerSubclass(SubTask.class);
            items = new ArrayList<Task>();
            editLvItems = new ListView(this);
            subItems = new ArrayList<>();
            spinnerListCategory = new String[]{"Course", "Travail", "Shopping", "Banque"};
            spinnerListPeriodicity = new String[]{"par jour", "par jour ouvrable", "par semaine", "par mois", "par an", "aucune"};
            displayTodoListView();
        } catch (Exception e) {
            String msg = e.getMessage();
            System.out.println(msg);
        }
    }

    /**
     * Permet de revenir à la vue principale
     */
    private void displayTodoListView() {
        try {
            setContentView(R.layout.activity_main);
            lvItems = (ListView) findViewById(R.id.lvItems);
            itemsAdapter = new TaskAdapter(this, items);
            lvItems.setAdapter(itemsAdapter);
            updateData();
            periodicityUpdate();
            updateData();
            setupListViewListener();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Permet d'instancier un spinner d'id spinnerid et de le lier à une textbox
     *
     * @param items
     * @param textBoxId
     * @param spinnerId
     */
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

    /**
     * Permet l'affichage de la vue d'edit
     */
    private void displayEditTodoListView() {
        setContentView(R.layout.edittodolayout);
        editLvItems = (ListView) findViewById(R.id.editItemListView);
        editItemsAdapter = new SubTaskAdapter(this,
                subItems);
        editLvItems.setAdapter(editItemsAdapter);
        setupListViewListener();
    }

    /**
     * Est appelee lors d'un click de checkbox. Permet de changer la valeur de isDone dans la Task
     * @param view
     */
    public void onTodoItemFinished(View view) {
        //Ceci est un commentaire
        CheckBox checkBox = (CheckBox) view;
            int pos = lvItems.getPositionForView(view);
        Task task = itemsAdapter.getItem(pos);
        task.setCompleted(checkBox.isChecked());
        task.saveEventually();
            // Refresh the adapter
            itemsAdapter.notifyDataSetChanged();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        // Remove the item within array at position
                        itemsAdapter.getItem(pos).deleteEventually();
                        itemsAdapter.remove(itemsAdapter.getItem(pos));
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }
                });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionOnClick = position;
                Task listItem = itemsAdapter.getItem(positionOnClick);
                subItems = listItem.getSubTaskList();
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

    /**
     * Gere l'insertion des valeurs dans la view edit
     * @param item
     */
    private void populateEditView(Task item) {
        EditText mEditTitle = (EditText) findViewById(R.id.editItemTitle);
        mEditTitle.setText((String) item.getTitle());
        EditText mEditCategory = (EditText) findViewById(R.id.editItemCategory);
        mEditCategory.setText((String) item.getCategory());
        EditText mEditComment = (EditText) findViewById(R.id.editItemComment);
        mEditComment.setText((String) item.getComment());
        TextView mEditDate = (TextView) findViewById(R.id.editItemDate);
        mEditDate.setText((String) item.getDate());
        TextView mEditPeriodicity = (TextView) findViewById(R.id.editItemPeriodicity);
        mEditPeriodicity.setText((String) item.getPeriodicity());
        createSpinner(spinnerListCategory, R.id.editItemCategory, R.id.spinnerCategoryEdit);
        createSpinner(spinnerListPeriodicity, R.id.editItemPeriodicity, R.id.spinnerPeriodicityEdit);
        editItemsAdapter.notifyDataSetChanged();


    }

    /**
     * Gere l'ajout de sous taches dans la vue edit
     * @param v
     */
    public void onAddSubItem(View v) {
        SubTask map = new SubTask();
        map.setCompleted(false);
        map.setTitle(((EditText) findViewById(R.id.addSubTaskTitle)).getText().toString());
        subItems.add(map);
        displayEditTodoListView();
        populateEditView(itemsAdapter.getItem(positionOnClick));

    }

    /**
     * Gere l'edit d'un item
     * @param v
     */
    public void onEditItem(View v) {
        try {
            Task item = itemsAdapter.getItem(positionOnClick);
            item.setCompleted(false);
            item.setTitle(((EditText) findViewById(R.id.editItemTitle)).getText().toString());
            item.setComment(((EditText) findViewById(R.id.editItemComment)).getText().toString());
            item.setCategory(((EditText) findViewById(R.id.editItemCategory)).getText().toString());
            item.setDate(((TextView) findViewById(R.id.editItemDate)).getText().toString());
            item.setPeriodicity(((TextView) findViewById(R.id.editItemPeriodicity)).getText().toString());
            item.setSubTaskList(subItems);
            item.saveEventually();
            itemsAdapter.notifyDataSetChanged();
            displayTodoListView();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    public void onSeeLocation(View view) {
        startActivity(new Intent(this, Map.class));
    }

    /**
     * Permet l'ajout d'un item
     * @param v
     */
    public void onAddItem(View v) {
        Task task = new Task();
        task.setCompleted(false);
        task.setTitle(((EditText) findViewById(R.id.itemTitle)).getText().toString());
        task.setComment(((EditText) findViewById(R.id.itemComment)).getText().toString());
        task.setCategory(((EditText) findViewById(R.id.itemCategory)).getText().toString());
        task.setDate(((TextView) findViewById(R.id.itemDate)).getText().toString());
        task.setPeriodicity(((TextView) findViewById(R.id.itemPeriodicity)).getText().toString());
        task.setLongitude(Double.parseDouble(((TextView) findViewById(R.id.longitude)).getText().toString()));
        task.setLatitude(Double.parseDouble(((TextView) findViewById(R.id.latitude)).getText().toString()));
        task.setSubTaskList(new ArrayList<SubTask>());
        task.saveEventually();
        itemsAdapter.insert(task, 0);
        // Retourne à la liste des items
        displayTodoListView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Gere le clic sur l'une des options du menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
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

    /**
     * Change les dates des tasks dont la date est passée en utilisant leur periodicite
     */
    private void periodicityUpdate() {
        ArrayList<Task> updateItems = new ArrayList<>();
        for (int taskIndex = 0; taskIndex < itemsAdapter.getCount(); taskIndex++) {
            Task item = itemsAdapter.getItem(taskIndex);
            Calendar calendar_now = Calendar.getInstance();
            Calendar calendar_set_date = Calendar.getInstance();
            String[] dateList = ((String) item.getDate()).split("\\|");
            int day_of_month = Integer.parseInt(dateList[0]);
            int month = Integer.parseInt(dateList[1]);
            int year = Integer.parseInt(dateList[2]);
            calendar_set_date.set(year, month - 1, day_of_month);
            Boolean deadlineExpired = calendar_now.after(calendar_set_date);
            if (deadlineExpired) {
                if (item.getPeriodicity().equals("par jour")) {
                    item.setDate(calendar_now);
                } else if (item.getPeriodicity().equals("par jour ouvrable")) {
                    int day_of_week = calendar_now.get(Calendar.DAY_OF_WEEK);
                    if (day_of_week == 0) {
                        calendar_now.roll(Calendar.DAY_OF_MONTH, 1);
                        item.setDate(calendar_now);
                    } else if (day_of_week == 7) {
                        calendar_now.roll(Calendar.DAY_OF_MONTH, 2);
                        item.setDate(calendar_now);
                    } else {
                        item.setDate(calendar_now);
                    }
                } else if (item.getPeriodicity().equals("par semaine")) {
                    calendar_now.roll(Calendar.DAY_OF_MONTH, 7);
                    item.setDate(calendar_now);
                } else if (item.getPeriodicity().equals("par mois")) {
                    calendar_now.roll(Calendar.MONTH, 1);
                    item.setDate(calendar_now);
                } else if (item.getPeriodicity().equals("par an")) {
                    calendar_now.roll(Calendar.YEAR, 1);
                    item.setDate(calendar_now);
                } else if (item.getPeriodicity().equals("aucune")) {
                    item.deleteEventually();
                }

            }
            if (item.getPeriodicity().equals("aucune")) {
                if (!deadlineExpired) {
                    updateItems.add(item);
                }
            } else {
                if (deadlineExpired) {
                    item.saveEventually();
                }
                updateItems.add(item);
            }


        }
        items = updateItems;
    }

}

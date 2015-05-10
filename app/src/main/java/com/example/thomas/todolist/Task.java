package com.example.thomas.todolist;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Thomas on 27/04/2015.
 */
@ParseClassName("Task")
public class Task extends ParseObject {
    public Task() {

    }

    public boolean isCompleted() {
        return getBoolean("isDone");
    }

    public void setCompleted(boolean complete) {
        put("isDone", complete);
    }

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public String getComment() {
        return getString("comment");
    }

    public void setComment(String comment) {
        put("comment", comment);
    }

    public String getCategory() {
        return getString("category");
    }

    public void setCategory(String category) {
        put("category", category);
    }

    public String getDate() {
        return getString("date");
    }

    public void setDate(String date) {
        put("date", date);
    }

    public void setDate(Calendar calendar_now) {
        int day_of_month_now = calendar_now.get(Calendar.DAY_OF_MONTH);
        int month_now = calendar_now.get(Calendar.MONTH) + 1;
        int year_now = calendar_now.get(Calendar.YEAR);
        setDate(String.format("%02d/%02d/%04d", day_of_month_now, month_now, year_now));
    }

    public String getPeriodicity() {
        return getString("periodicity");
    }

    public void setPeriodicity(String periodicity) {
        put("periodicity", periodicity);
    }

    public ArrayList<SubTask> getSubTaskList() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (Object subTask : getList("subTaskList")) {
            subTasks.add((SubTask) subTask);
        }
        return subTasks;
    }

    public void setSubTaskList(ArrayList<SubTask> subTaskList) {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subTaskList) {
            subTasks.add(subTask);
        }
        put("subTaskList", subTasks);
    }

}

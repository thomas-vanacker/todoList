package com.example.thomas.todolist;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Thomas on 27/04/2015.
 */
@ParseClassName("SubTask")
public class SubTask extends ParseObject {
    public SubTask() {

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
}

package com.example.thomas.todolist;

import java.util.HashMap;

/**
 * Created by Thomas on 18/04/2015.
 */
public class TodoItem {
    private Boolean isDone;
    private String title;
    private String comment;
    private String category;

    public TodoItem(String line){
        String[] parts = line.split("|");
        isDone = Boolean.parseBoolean(parts[0]);
        title = parts[1];
        comment = parts[2];
        category = parts[3];
    }

    public String serialize(){
        return String.format("%s|%s|%s|%s",isDone.toString(),title,comment,category);
    }


    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public HashMap<String, Object> getHashMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("IsDone", this.isDone);
        map.put("title", this.title);
        map.put("comment", this.getComment());
        map.put("category", this.category);
        return map;
    }
}

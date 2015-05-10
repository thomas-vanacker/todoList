package com.example.thomas.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Thomas on 27/04/2015.
 */
public class TaskAdapter extends ArrayAdapter<Task> {
    private Context mContext;
    private ArrayList<Task> mTasks;

    public TaskAdapter(Context context, ArrayList<Task> objects) {
        super(context, R.layout.mylayout, objects);
        this.mContext = context;
        this.mTasks = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.mylayout, null);
        }
        Task task = mTasks.get(position);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.isDone);
        checkBox.setChecked(task.isCompleted());
        TextView titleView = (TextView) convertView.findViewById(R.id.todoTitle);
        titleView.setText(task.getTitle());
        TextView commentView = (TextView) convertView.findViewById(R.id.comment);
        commentView.setText(task.getComment());
        TextView categoryView = (TextView) convertView.findViewById(R.id.category);
        categoryView.setText(task.getCategory());
        TextView dateView = (TextView) convertView.findViewById(R.id.deadline);
        dateView.setText(task.getDate());
        TextView longView = (TextView) convertView.findViewById(R.id.longitude);
        longView.setText(task.getLongitude().toString());
        TextView latView = (TextView) convertView.findViewById(R.id.latitude);
        latView.setText(task.getLatitude().toString());
        return convertView;
    }
}



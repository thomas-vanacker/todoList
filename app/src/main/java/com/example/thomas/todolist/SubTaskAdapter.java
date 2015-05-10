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
public class SubTaskAdapter extends ArrayAdapter<SubTask> {
    private Context mContext;
    private ArrayList<SubTask> mSubTasks;

    public SubTaskAdapter(Context context, ArrayList<SubTask> objects) {
        super(context, R.layout.mylayout, objects);
        this.mContext = context;
        this.mSubTasks = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
            convertView = mLayoutInflater.inflate(R.layout.sub_task_layout, null);
        }
        SubTask subTask = mSubTasks.get(position);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.subTaskIsDone);
        checkBox.setChecked(subTask.isCompleted());
        TextView titleView = (TextView) convertView.findViewById(R.id.subTaskTitle);
        titleView.setText(subTask.getTitle());
        return convertView;
    }
}

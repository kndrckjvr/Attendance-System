package com.example.dev4.attendancesystem;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jasmin Gutierrez on 29/06/2017.
 */

public class AttendanceViewAdapter extends ArrayAdapter<AttendanceClass> {
    public AttendanceViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<AttendanceClass> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.atten_view_adapter, null);
        AttendanceClass atten = AttendanceClass.SHOW_ATTENDANCE.get(position);
        CourseClass course = CourseClass.COURSE_LIST.get(AttendanceList.index);
        if(course.getId().equals(atten.getCourse_id())) {
            TextView name = (TextView) convertView.findViewById(R.id.studNameTx);
            TextView date = (TextView) convertView.findViewById(R.id.studNumTx);
            TextView sect = (TextView) convertView.findViewById(R.id.sectTx);

            name.setText(course.getName());
            date.setText(atten.getDate());
            sect.setText(course.getSection());
        }
        return convertView;
    }
}
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

public class CourseViewAdapter extends ArrayAdapter<CourseClass> {
    public CourseViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<CourseClass> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.course_view_adapter, null);
        CourseClass course = CourseClass.COURSE_LIST.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.studNameTx);
        TextView prof = (TextView) convertView.findViewById(R.id.studNumTx);
        TextView sect = (TextView) convertView.findViewById(R.id.secTx);

        name.setText(course.getName());
        prof.setText(ProfessorClass.CURRENT_LOGON.get(0).getName());
        sect.setText(course.getSection());

        return convertView;
    }
}

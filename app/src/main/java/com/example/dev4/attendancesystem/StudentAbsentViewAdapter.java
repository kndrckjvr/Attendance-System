package com.example.dev4.attendancesystem;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jasmin Gutierrez on 29/06/2017.
 */

public class StudentAbsentViewAdapter extends ArrayAdapter<EnrollClass> {
    public StudentAbsentViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<EnrollClass> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.absent_view_adapter, null);
        StudentClass student;
        int absences = 0;



        for(int i = 0; i < StudentClass.STUDENT_LIST.size(); i++) {
            student = StudentClass.STUDENT_LIST.get(i);
            for(int j = 0; j < PresentClass.SHOW_PRESENT.size(); j++) {
                if(student.getId().equals(PresentClass.SHOW_PRESENT.get(j).getStud_id())) {
                    if(PresentClass.SHOW_PRESENT.get(j).getStatus().equals("0"))
                        absences++;
                }
            }
            if(EnrollClass.SHOW_ENROLL.get(position).getStud_id().equals(student.getId())) {
                TextView name = (TextView) convertView.findViewById(R.id.studNameTx);
                TextView numb = (TextView) convertView.findViewById(R.id.studNumTx);
                TextView abse = (TextView) convertView.findViewById(R.id.absentTx);
                ImageView studImg = (ImageView) convertView.findViewById(R.id.studImg);

                Picasso.with(getContext())
                        .load(LoginActivity._URL+student.getImage())
                        .resize(50, 50)
                        .into(studImg);
                name.setText(student.getName());
                numb.setText(student.getNumber());
                abse.setText(absences+"");
            }
        }

        return convertView;
    }
}

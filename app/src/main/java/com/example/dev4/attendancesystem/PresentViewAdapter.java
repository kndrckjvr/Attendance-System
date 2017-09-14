package com.example.dev4.attendancesystem;

import android.content.Context;
import android.media.Image;
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

public class PresentViewAdapter extends ArrayAdapter<PresentClass> {
    public PresentViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<PresentClass> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.present_view_adapter, null);
        PresentClass present = PresentClass.SHOW_PRESENT.get(position);
        StudentClass student = StudentClass.STUDENT_LIST.get(0);
        for(int i = 0; StudentClass.STUDENT_LIST.size() > i; i++) {
            if(StudentClass.STUDENT_LIST.get(i).getId().equals(present.getStud_id())) {
                student = StudentClass.STUDENT_LIST.get(i);
            }
        }
        TextView name = (TextView) convertView.findViewById(R.id.studNameTx);
        TextView numb = (TextView) convertView.findViewById(R.id.studNumTx);
        ImageView studImg = (ImageView) convertView.findViewById(R.id.studImg);
        ImageView statImg = (ImageView) convertView.findViewById(R.id.statusImg);

        Picasso.with(getContext())
                .load(LoginActivity._URL+student.getImage())
                .resize(50, 50)
                .into(studImg);
        name.setText(student.getName());
        numb.setText(student.getNumber());
        if(present.getStatus().equals("0"))
            statImg.setImageResource(R.drawable.ic_absent);

        return convertView;
    }
}

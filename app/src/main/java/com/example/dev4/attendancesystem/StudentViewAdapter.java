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

public class StudentViewAdapter extends ArrayAdapter<StudentClass> {
    public StudentViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<StudentClass> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.student_view_adapter, null);
        StudentClass stud = StudentClass.STUDENT_LIST.get(position);

        TextView name = (TextView) convertView.findViewById(R.id.studNameTx);
        TextView numb = (TextView) convertView.findViewById(R.id.studNumTx);
        ImageView img = (ImageView) convertView.findViewById(R.id.studImg);

        name.setText(stud.getName());
        numb.setText(stud.getNumber());
        Picasso.with(getContext())
                .load(LoginActivity._URL+stud.getImage())
                .resize(50, 50)
                .into(img);

        return convertView;
    }
}

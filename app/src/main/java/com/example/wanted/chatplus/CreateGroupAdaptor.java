package com.example.wanted.chatplus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by hardik on 9/1/17.
 */
public class CreateGroupAdaptor  extends BaseAdapter {

    private Context context;
    private Remove_Member remove_member;
    private Add_Member add_member;
    private ArrayList<String> name;
    public static ArrayList<Model> modelArrayList;
    private String color;



    public CreateGroupAdaptor(Context context, ArrayList<Model> modelArrayList,ArrayList<String> names,String color) {

        this.context = context;
        this.name = names;
        this.modelArrayList = modelArrayList;
        this.color = color;

    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return modelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return modelArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.create_group_ad, null, true);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.checkBox.setText("  "+name.get(position)+"\n  "+modelArrayList.get(position).getAnimal());
        holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.parseColor(color)));


        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                Integer pos = (Integer)  holder.checkBox.getTag();
                if(modelArrayList.get(pos).getSelected())
                {
                    modelArrayList.get(pos).setSelected(false);
                }
                else
                {
                    modelArrayList.get(pos).setSelected(true);
                }

            }
        });

        return convertView;
    }

    private class ViewHolder {

        protected CheckBox checkBox;

    }

}
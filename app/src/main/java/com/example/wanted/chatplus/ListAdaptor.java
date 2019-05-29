package com.example.wanted.chatplus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class ListAdaptor extends BaseAdapter {

    Context context;
    private final String [] values;
    private final String [] values1;



    public ListAdaptor(Context context, String [] values,String [] values1){//,String [] values2,String [] values3){
        //super(context, R.layout.single_list_item, utilsArrayList);
        this.context = context;
        this.values=values;
        this.values1 = values1;


    }

    @Override
    public int getCount()
    {
        return values.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ViewHolder viewHolder;

        final View result;


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.select_group, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxts);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.aNametxts1);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(values[position]);
        viewHolder.textView.setText(values1[position]);


        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        TextView textView;

    }

}

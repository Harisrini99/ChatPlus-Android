package com.example.wanted.chatplus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class NullAdaptor extends BaseAdapter {

    Context context;
    String main;
    String main2;



    public NullAdaptor(Context context,String main,String main2)
    {//values,String [] values1){//,String  values2,String [] values3){
        this.context = context;
        this.main = main;
        this.main2 = main2;

    }

    @Override
    public int getCount() {
        return 1;
    }



    @Override
    public Object getItem(int i) {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return 1;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        ViewHolder viewHolder = null;

        final View result;



        viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.select_group, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.aNametxts);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.aNametxts1);


            result=convertView;

            convertView.setTag(viewHolder);


        viewHolder.txtName.setText(main);
        viewHolder.textView.setText(main2);
        Log.d(")))","enter3");



        return convertView;
    }

    private static class ViewHolder {

        TextView txtName;
        TextView textView;

    }

}

package com.example.wanted.chatplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

public class GroupAdaptor extends FirebaseListAdapter<Group> {

    private Activity activity;
    private int k;
    private Group_Fragment group_fragment;
    private sendV s = new sendV();
    public GroupAdaptor(Activity activity, Class<Group> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;

        k =getCount();



    }



    @Override
    protected void populateView(View v, Group model, int position) {
        TextView messageText = (TextView) v.findViewById(R.id.aNametxts);
        TextView messageUser = (TextView) v.findViewById(R.id.aNametxts1);
        int k = getCount();

        messageText.setText(model.getGroupname());
        messageUser.setText(model.getGroupdesc());

    }

    protected void putout(View v,Group model,int position)
    {
        TextView messageText = (TextView) v.findViewById(R.id.aNametxts);
        TextView messageTime = (TextView) v.findViewById(R.id.aNametxts1);
        messageText.setText(model.getGroupname());
        messageTime.setText(model.getGroupdesc());
       // if(ac.progressDialog!=null)
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Group chatMessage = getItem(position);
        view = activity.getLayoutInflater().inflate(R.layout.select_group, viewGroup, false);

        putout(view,chatMessage,position);

        return view;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }
}

package com.example.wanted.chatplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class Chat_class_adaptor extends FirebaseListAdapter<Chat_class> {

    private Activity activity;
   // private ProgressDialog progressDialog;
    private Chat_Fragment a;

    public Chat_class_adaptor(Activity activity, Class<Chat_class> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;
       // progressDialog = ProgressDialog.show(activity, "Loading Chats....", "Please wait...", false, false);

        int k =getCount();
        Log.d("enter",Integer.toString(k));
        //if(k ==0)
          //  a.progressDialog.dismiss();
    }
    protected void putout(View v,Chat_class model,int position)
    {
        TextView messageText = (TextView) v.findViewById(R.id.aNametxts);
        TextView messageTime = (TextView) v.findViewById(R.id.aNametxts1);
        String temp = a.contact_list.get(model.getPhoneNumber());
        if(temp == null)
        messageText.setText(model.getPhoneNumber());
        else
            messageText.setText(temp);
        messageTime.setText(model.getPhoneNumber());
      //  a.progressDialog.dismiss();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Chat_class chatMessage = getItem(position);
        view = activity.getLayoutInflater().inflate(R.layout.select_group, viewGroup, false);

        putout(view,chatMessage,position);

        return view;
    }

    @Override
    protected void populateView(View v, Chat_class model, int position) {

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

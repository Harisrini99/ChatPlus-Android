package com.example.wanted.chatplus;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;

public class GroupMessageAdapter extends FirebaseListAdapter<ChatMessage> {

    private Group_chat_screen activity;
    private Chat_Fragment chat_fragment;

    public GroupMessageAdapter(Group_chat_screen activity, Class<ChatMessage> modelClass, int modelLayout, DatabaseReference ref) {
        super(activity, modelClass, modelLayout, ref);
        this.activity = activity;

        int k =getCount();
        Log.d("enter",Integer.toString(k));
    }



    @Override
    protected void populateView(View v, ChatMessage model, int position) {
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageUser = (TextView) v.findViewById(R.id.message_user);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);

        messageText.setText(model.getMessageText());
        String num = model.getMessageUser();
        String user = chat_fragment.contact_list.get(num);
        if(user == null)
        {
            num = num.substring(1,num.length());
            num = "~"+num;
            messageUser.setText(num);
            messageUser.setGravity(Gravity.RIGHT);
        }
        else {
            messageUser.setText(user);
        }

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
        activity.progressDialog.dismiss();
    }

    protected void putout(View v,ChatMessage model,int position)
    {
        TextView messageText = (TextView) v.findViewById(R.id.message_text);
        TextView messageTime = (TextView) v.findViewById(R.id.message_time);

        messageText.setText(model.getMessageText());

        // Format the date before showing it
        messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
        activity.progressDialog.dismiss();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ChatMessage chatMessage = getItem(position);
        Log.d("main1",activity.getLoggedInUserName());
        Log.d("main2",chatMessage.getMessageUser());
        if (chatMessage.getMessageUser().equals(activity.getLoggedInUserName())) {
            view = activity.getLayoutInflater().inflate(R.layout.itemgroup_out_message, viewGroup, false);
                putout(view,chatMessage,position);
        }
        else {
            view = activity.getLayoutInflater().inflate(R.layout.itemgroup_in_message, viewGroup, false);
            populateView(view, chatMessage, position);
        }

        //generating view
        //(view,chatMessage,position);

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

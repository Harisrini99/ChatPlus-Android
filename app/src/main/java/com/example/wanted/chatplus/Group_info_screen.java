package com.example.wanted.chatplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Group_info_screen extends Dialog {


    private ListView lView;
    private ListAdaptor listAdaptor;
    private Group_chat_screen group_chat_screen;
    private String [] names=new String[0];
    private String [] phones= new String[0];
    private Chat_Fragment chat_fragment;
    private  ArrayList<String> g;
    private HashMap<String,String> map;
    private ArrayList<String> name=new ArrayList<>();
    private ArrayList<String> phone=new ArrayList<>();

    private String Groupmem;
    private String Groupname;
    private String Groupdesc;
    private String current_user;


    public Group_info_screen(@NonNull Context context)
    {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_info);
        lView = (ListView)findViewById(R.id.androidList);
        int i=0;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        current_user = firebaseUser.getPhoneNumber();
        Groupname = group_chat_screen.group_name;
        g = group_chat_screen.Groupmembers;
        map = chat_fragment.contact_list;
        for(String p:g)
        {
            String temp = map.get(p);
            Log.d("memmm","--"+temp+"--");
            if(p.equals(current_user))
                name.add("You");
            else if(temp == null ||temp == "")
                name.add(p);
            else
                name.add(temp);
            phone.add(p);
        }
        names = name.toArray(new String[0]);
        phones = phone.toArray(new String[0]);
        listAdaptor = new ListAdaptor(getContext(),names,phones);
        lView.setAdapter(listAdaptor);





    }

    private void putMem()
    {

    }


}

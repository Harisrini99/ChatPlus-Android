package com.example.wanted.chatplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Change_Group_Name extends Dialog implements View.OnClickListener
{

    private CreateGroupAdaptor createGroupAdaptor;
    sendV s;
    private String groupname;
    private String groupdesc;
    private Button create;
    private EditText groupnameed;
    private EditText groupdesced;
    private ArrayList<String> Groupmembers;
    private Group_chat_screen group_chat_screen;
    private ProgressDialog progressDialog;

    public Change_Group_Name(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change__group__name);
        create = (Button)findViewById(R.id.create_group);
        groupnameed = (EditText)findViewById(R.id.groupname);
        groupdesced = (EditText)findViewById(R.id.groupdesc);
        create.setOnClickListener(this);
        s = new sendV();
        groupname = group_chat_screen.group_name;
        groupdesc = group_chat_screen.group_desc;
        Groupmembers = group_chat_screen.Groupmembers;
        groupdesced.setText(groupdesc);
        groupnameed.setText(groupname);





    }
    public Set<String> loadPreferences1()
    {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(s.setACtivity());
        Set<String> name = new HashSet<>();
        name.add("NULL");
        Set<String> names = prefs.getStringSet("Contacts",name);
        return names;
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.create_group)
        {
            progressDialog = ProgressDialog.show(getContext(), "Changing Name and Description....", "Please wait...", false, false);
            deleteExisting();
        }
    }

    private void updateNew() {
        String phone = "";
        int flag = 0;
        for (String p : Groupmembers) {
            flag = 1;
            phone = phone + p + "&";
        }
        if (flag == 1) {
            phone = phone.substring(0, phone.length() - 1);
        }

        for (String p : Groupmembers) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(p).child("GroupDetails")
                    .push()
                    .setValue(new Group(groupnameed.getText().toString(), groupdesced.getText().toString(), phone)
                    );
        }

        copyAndUpdate();
    }


        private void copyAndUpdate()
        {
            final ArrayList<Map> old = new ArrayList<>();
            final ArrayList<String> oldheading = new ArrayList<>();
            for(final String p:Groupmembers)
            {

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(p).child("Groups").child(groupname);
                ref.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        old.clear();
                        oldheading.clear();
                        for(DataSnapshot p :dataSnapshot.getChildren())
                        {

                            HashMap<String,Object> map = new HashMap<>();
                            oldheading.add(p.getKey().toString());
                            map.put("messageText",p.child("messageText").getValue());
                            map.put("messageTime",p.child("messageTime").getValue());
                            map.put("messageUser",p.child("messageUser").getValue());
                            old.add(map);
                        }
                        Log.d("!!!",old.toString());
                        ref.removeValue();
                        HashMap<String,Object> map = new HashMap<>();
                        map.put(groupnameed.getText().toString(),"");
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child(p).child("Groups").child(groupnameed.getText().toString());
                        int i=0;
                        for(String po:oldheading)
                        {
                            ref1.child(po).updateChildren(old.get(i));
                            i++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run()
                {
                    new Group_chat_screen().refreshGroupMembers();
                    progressDialog.dismiss();
                    dismiss();

                }
            };
            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 2000);
            dismiss();
        }


    private void deleteExisting()
    {
        for(String p : Groupmembers)
        {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(p).child("GroupDetails");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String deletedir= "NULL";

                    for(DataSnapshot p :dataSnapshot.getChildren())
                    {
                        String map = new String();
                        if(p.hasChildren())
                            map = p.child("groupname").getValue().toString();
                        if(map.equals(groupname))
                        {
                            deletedir = p.getKey().toString();
                            break;
                        }
                    }
                    ref.child(deletedir).removeValue();
                    Log.d("++++",deletedir);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        updateNew();
    }


}

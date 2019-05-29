package com.example.wanted.chatplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Remove_Member extends Dialog implements View.OnClickListener
{

    private CreateGroupAdaptor createGroupAdaptor;
    sendV s;
    private ListView lView;
    private  ArrayList<Model> modelArrayList;
    private ArrayList<String> selected;
    private String groupname;
    private String groupdesc;
    private String groupparent;
    private ProgressDialog progressDialog;
    private Group_chat_screen group_chat_screen;
    public String [] p1;
    private Button create;
    private  ArrayList<Model> list;
    private Contact_Fragment contact_fragment;
    private Chat_Fragment chat_fragment;
    private ArrayList<String> Groupmembers;
    private String curren;
    private ArrayList<String> pk;
    private String phone;

    public Remove_Member(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        s = new sendV();
        modelArrayList = new ArrayList<>();
        selected = new ArrayList<>();

        groupname = group_chat_screen.group_name;
        groupdesc = group_chat_screen.group_desc;
        groupparent = group_chat_screen.group_parent;
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        curren = firebaseUser.getPhoneNumber();
        Set<String> temp_name_con_set = loadPreferences1();
        Iterator iterator1 = temp_name_con_set.iterator();
        Set<String> groupcon = new HashSet<>();
        while(iterator1.hasNext())
        {
            String element = (String)iterator1.next();
            String ele [] = element.split("&");
            groupcon.add(ele[1]);

        }

        ArrayList<String> temp_name = new ArrayList<>();
        Groupmembers = group_chat_screen.Groupmembers;
        Set<String> groupmem = new HashSet<>(group_chat_screen.Groupmembers);
        groupmem.remove(curren);
        p1 = groupmem.toArray(new String[0]);
        Iterator iterator = groupmem.iterator();
        while (iterator.hasNext())
        {
            String element = (String)iterator.next();
            temp_name.add(chat_fragment.contact_list.get(element));
        }
        Log.d("coing",groupcon.toString());
        if(p1.length == 0)
        {
            dismiss();
            Toast.makeText(getContext(),"No Members to Remove",Toast.LENGTH_SHORT).show();
        }
        else
        {

            setContentView(R.layout.remove__member);
            lView = (ListView)findViewById(R.id.androidList);
            create = (Button)findViewById(R.id.create_group);
            modelArrayList = getModel(false);
            createGroupAdaptor = new CreateGroupAdaptor(getContext(), modelArrayList, temp_name, "#cb2d2d");//, msgya, namesa, msgsa)
            lView.setAdapter(createGroupAdaptor);
            create.setOnClickListener(this);
        }



    }
    private ArrayList<Model> getModel(boolean isSelect){
        list = new ArrayList<>();
        for(int i = 0; i < p1.length; i++){

            Model model = new Model();
            model.setSelected(isSelect);
            model.setAnimal(p1[i]);
            list.add(model);

        }

        return list;
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
            selected.clear();

            for(int i =0 ;i<modelArrayList.size();i++)
            {
                if(modelArrayList.get(i).getSelected())
                    selected.add(modelArrayList.get(i).getAnimal());
            }
            if(selected.size() != 0) {
                Log.d("selected", selected.toString() + " " + groupname + " " + groupdesc);
                progressDialog = ProgressDialog.show(getContext(), "Removing Member..", "Please wait...", true, true);


                phone = "";
                int flag = 0;
                pk = new ArrayList<>(Groupmembers);
                pk.removeAll(selected);
                for (String p : pk) {
                    flag = 1;
                    phone = phone + p + "&";
                }
                if (flag == 1) {
                    phone = phone.substring(0, phone.length() - 1);
                }
                deleteExistingDetails();
            }
            else
            {
                Toast.makeText(getContext(),"Select a Member to Remove",Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void updateNew()
    {


        for(String p:pk)
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(p).child("GroupDetails")
                    .push()
                    .setValue(new Group(groupname, groupdesc,phone)
                    );
            Group_Fragment group_fragment = new Group_Fragment();
            group_fragment.refresh();
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
        pdCanceller.postDelayed(progressRunnable, 3000);

    }

    private void deleteExistingDetails()
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

        deleteExistingGroups();
    }

    private void deleteExistingGroups()
    {
        for(String p : selected)
        {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(p).child("Groups");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    String deletedir= "NULL";

                    for(DataSnapshot p :dataSnapshot.getChildren())
                    {
                        String map = p.getKey().toString();
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

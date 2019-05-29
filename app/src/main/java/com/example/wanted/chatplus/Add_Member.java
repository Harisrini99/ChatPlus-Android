package com.example.wanted.chatplus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.Set;

public class Add_Member extends Dialog implements View.OnClickListener
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
    public  String [] p2;
    private Button create;
    private  ArrayList<Model> list;
    private Contact_Fragment contact_fragment;
    private Chat_Fragment chat_fragment;
    private ArrayList<String> Groupmembers;

    public Add_Member(@NonNull Context context) {
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
        Log.d("coing1",groupcon.toString());
        Log.d("coing2",groupmem.toString());
        groupcon.removeAll(groupmem);
        p2 = groupcon.toArray(new String[0]);
        Iterator iterator = groupcon.iterator();
        while (iterator.hasNext())
        {
            String element = (String)iterator.next();
            temp_name.add(chat_fragment.contact_list.get(element));
        }
        Log.d("coing",groupcon.toString());
        int color = R.color.change;
        if(p2.length == 0)
        {
            Toast.makeText(getContext(),"No Contacts to Add",Toast.LENGTH_SHORT).show();
            dismiss();
        }
        else {
            setContentView(R.layout.add_member);
            lView = (ListView)findViewById(R.id.androidList);
            create = (Button)findViewById(R.id.create_group);
            modelArrayList = getModel(false);
            createGroupAdaptor = new CreateGroupAdaptor(getContext(), modelArrayList, temp_name, "#bb8718");
            lView.setAdapter(createGroupAdaptor);
            create.setOnClickListener(this);
        }



    }
    private ArrayList<Model> getModel(boolean isSelect){
        list = new ArrayList<>();
        for(int i = 0; i < p2.length; i++){

            Model model = new Model();
            model.setSelected(isSelect);
            model.setAnimal(p2[i]);
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
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String curren = firebaseUser.getPhoneNumber();
            if(selected.size() !=0) {
                progressDialog = ProgressDialog.show(getContext(), "Adding Member..", "Please wait...", true, true);
                deleteExisting();
            }
            else
            {
                Toast.makeText(getContext(),"Select a Member to Add",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateNew()
    {
        String phone = "";
        int flag =0;
        ArrayList<String> pk = new ArrayList<>(selected);
        pk.addAll(Groupmembers);
        for(String p :pk)
        {
            flag =1;
            phone =  phone +p + "&";
        }
        if(flag ==1)
        {
            phone = phone.substring(0,phone.length()-1);
        }
        for(String p:pk)
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(p).child("GroupDetails")
                    .push()
                    .setValue(new Group(groupname, groupdesc,phone)
                    );
        }
        for(String p:selected)
        {
            HashMap<String,Object> map = new HashMap<>();
            map.put(groupname,"");
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(p).child("Groups")
                    .updateChildren(map);
        }
        Group_Fragment group_fragment = new Group_Fragment();
        group_fragment.refresh();
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

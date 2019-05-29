package com.example.wanted.chatplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Group_creation_dialog extends Dialog implements View.OnClickListener
{

    private CreateGroupAdaptor createGroupAdaptor;
    sendV s;
    private ListView lView;
    private  ArrayList<Model> modelArrayList;
    private ArrayList<Model> selectednumbers;
    private ArrayList<String> selected;
    private EditText groupnameed;
    private EditText groupdesced;
    private String groupname;
    private String groupdesc;
    private ProgressDialog progressDialog;

    private      String [] p1;
    private Button create;
    private  ArrayList<Model> list;


    public Group_creation_dialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_creation_dialog);
        lView = (ListView)findViewById(R.id.androidList);
        create = (Button)findViewById(R.id.create_group);
        groupnameed = (EditText)findViewById(R.id.groupname);
        groupdesced = (EditText)findViewById(R.id.groupdesc);
        s = new sendV();
        modelArrayList = new ArrayList<>();
        selected = new ArrayList<>();
        Set<String> temp_name_con_set = loadPreferences1();

        Iterator iterator = temp_name_con_set.iterator();
        ArrayList<String> temp_name = new ArrayList<>();
        ArrayList<String> temp_phone = new ArrayList<>();
        while(iterator.hasNext())
        {
            String element = (String) iterator.next();
            String[] ele = element.split("&");
            temp_name.add(ele[0]);
            temp_phone.add(ele[1]);

        }

        p1 = temp_phone.toArray(new String[0]);
        modelArrayList = getModel(false);
        Log.d("coing",modelArrayList.get(0).getAnimal());
        createGroupAdaptor = new CreateGroupAdaptor(getContext(), modelArrayList,temp_name,"#153f0b");//, msgya, namesa, msgsa)
        lView.setAdapter(createGroupAdaptor);

        create.setOnClickListener(this);



    }
    private ArrayList<Model> getModel(boolean isSelect){
        list = new ArrayList<>();
        for(int i = 0; i < p1.length; i++){

            Model model = new Model();
            model.setSelected(isSelect);
            model.setAnimal(p1[i]);
            list.add(model);
            //Log.d("coming",list.get(i).getAnimal());

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
            groupname = groupnameed.getText().toString();
            groupdesc = groupdesced.getText().toString();
            if(selected.size() == 0)
            {
                Toast.makeText(getContext(),"Please select Member to Add",Toast.LENGTH_SHORT).show();
            }
            else if(groupdesc.equals("")||groupname.equals("")||groupname.equals(null)||groupdesc.equals(null))
            {
                Toast.makeText(getContext(),"Enter the Group Name And Description",Toast.LENGTH_SHORT).show();
            }
            else
            {
                selected.add(curren);
                Log.d("selected", selected.toString() + " " + groupname + " " + groupdesc);
                progressDialog = ProgressDialog.show(getContext(), "Creating Group..", "Please wait...", true, true);
                putvalues();
            }

        }
    }
    private void putvalues()
    {
        String phone = "";
        int flag =0;
        for(String p :selected)
        {
            flag =1;
          phone =  phone +p + "&";
        }
       if(flag ==1)
       {
           phone = phone.substring(0,phone.length()-1);
       }

        Log.d("phoneeee",phone.toString());
        Log.d("phoneeee",selected.toString());
        for(String p:selected) {
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
        progressDialog.dismiss();
        dismiss();
        Group_Fragment g = new Group_Fragment();
        g.refresh();
    }


}

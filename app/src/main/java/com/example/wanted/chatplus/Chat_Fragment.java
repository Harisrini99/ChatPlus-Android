package com.example.wanted.chatplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class Chat_Fragment extends Fragment {

    private String content;
    boolean value = true;
    ArrayList<String> namey = new ArrayList<String>();
    ArrayList<String> Name = new ArrayList<String>();
    ArrayList<String> Phone = new ArrayList<String>();
    ArrayList<String> msgs = new ArrayList<String>();
    Set<String> contact_set = new HashSet<>();
    Set<String> database_set = new HashSet<>();
    Set<String> database_set_copy = new HashSet<>();

    public static HashMap<String,String> contact_list = new HashMap<>();

    static String removeph;
     static Boolean datataken = false;


    static ListView lView;
    private static sendV s = new sendV();
    static Chat_class_adaptor lAdapter;
    static Activity context;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        lView = (ListView)view.findViewById(R.id.androidList);
        context = getActivity();
        s.getACtivity(context);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
         removeph = firebaseUser.getPhoneNumber();
         if(contact_list.isEmpty())
            getContactList();
        putGroups();
        return view;
    }



    public void putGroups()
    {
        Log.d("@@@","I am");
        lAdapter = new Chat_class_adaptor(s.setACtivity(),Chat_class.class, R.layout.chat_fragment,
                FirebaseDatabase.getInstance().getReference().child(removeph).child("Chats"));
        lView.setAdapter(lAdapter);
        final ArrayList<String> phone = new ArrayList<>();
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(s.setACtivity(), Contact_chat.class);
                intent.putExtra("Contactname",contact_list.get(lAdapter.getItem(i).getPhoneNumber()));
                intent.putExtra("PhoneNumber",lAdapter.getItem(i).getPhoneNumber());
                s.setACtivity().startActivity(intent);
            }
        });
    }

    public void putGroupsdupli()
    {
        Chat_Fragment c = new Chat_Fragment();
        c.getContactList();
        lAdapter = new Chat_class_adaptor(s.setACtivity(),Chat_class.class, R.layout.chat_fragment,
                FirebaseDatabase.getInstance().getReference().child(removeph).child("Chats"));
        lView.setAdapter(lAdapter);
        final ArrayList<String> phone = new ArrayList<>();
        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(s.setACtivity(), Contact_chat.class);
                intent.putExtra("Contactname",contact_list.get(lAdapter.getItem(i).getPhoneNumber()));
                intent.putExtra("PhoneNumber",lAdapter.getItem(i).getPhoneNumber());
                s.setACtivity().startActivity(intent);
            }
        });
    }

    public void getContactList()
    {
        contact_list.clear();
        contact_set.clear();
        ContentResolver cr=context. getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));

                        if(phoneNo.startsWith("+"))
                            phoneNo = phoneNo;
                        else
                            phoneNo = "+91"+phoneNo;

                        contact_list.put(phoneNo,name);
                        contact_set.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }


    public void refresh()
    {
        putGroupsdupli();
    }
}


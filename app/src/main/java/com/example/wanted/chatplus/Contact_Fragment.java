package com.example.wanted.chatplus;

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


public class Contact_Fragment extends Fragment {

    private String content;
    boolean value = true;
    ArrayList<String> namey = new ArrayList<String>();
    ArrayList<String> Name = new ArrayList<String>();
    public static ArrayList<String> Phone = new ArrayList<String>();
    ArrayList<String> msgs = new ArrayList<String>();
    Set<String> contact_set = new HashSet<>();
    Set<String> database_set = new HashSet<>();
    Set<String> save_name = new HashSet<>();
    Set<String> save_phone = new HashSet<>();

    HashMap<String,String> contact_list = new HashMap<>();
    RelativeLayout.LayoutParams lp;
    private static final String TAG = MainActivity.class.getSimpleName();
    EmojiconEditText emojiconEditText;
    TextView textView;
    ImageView emojiImageView;
    Button submitButton;
    View rootView;
    private String sen;
    private String sen1;
    private String sentemp;
    String[] name ;
    private static sendV s = new sendV();
     String[] phone;
    private  Context context=s.setACtivity();// = getActivity();

    static ListView lView;
    private Chat_Fragment chat_fragment;
    private Uri fileUri;
    Drawable drawable;
    boolean _areLecturesLoaded = false;
    private Main_Page main_page;
     ListAdaptor lAdapter;
    static Boolean datataken = false;
    private Boolean dataget = false;
    private  Boolean firsttime = false;
    private static Intent intent;

    public static Contact_Fragment newInstance(int page, String title) {
        Contact_Fragment fragmentFirst = new Contact_Fragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        lView = (ListView)view.findViewById(R.id.androidList);

        intent = new Intent(s.setACtivity(), Contact_chat.class);

        Set<String> h = loadPreferences1();
        Log.d("null",h.toString());
        Iterator iterator = h.iterator();
        String element="NULL";
        if(iterator.hasNext())
            element = (String) iterator.next();
        Log.d("null",element);

        if(element.equals("NULL") && firsttime==false)
        {
            Log.d("nullenter","yes");
            getNumbersindatabase();
        }
        else
        {
            Log.d("nullenter","no");
            putcontacts();
        }
        firsttime = true;

        return view;
    }
    private void start()
    {
        getNumbersindatabase();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !_areLecturesLoaded )
        {
            Set<String> h = loadPreferences1();
            Log.d("null",h.toString());
            Iterator iterator = h.iterator();
            String element="NULL";
            if(iterator.hasNext())
                element = (String) iterator.next();
            Log.d("null",element);

            if(element.equals("NULL"))
            {
                Log.d("nullenter","yes");
                getNumbersindatabase();
            }
            _areLecturesLoaded = true;

        }
    }
    public void refresh()
    {
        getNumbersindatabase();
    }

    private void getNumbersindatabase()
    {
        main_page = new Main_Page();
        main_page.start();
        Log.d("iienterii","enter");
        DatabaseReference r = FirebaseDatabase.getInstance().getReference();
        Log.d("iienterii","enter1");

        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                {
                    Log.d("iienterii","yes");
                    database_set.add(postSnapshot.getKey());
                }
                Log.d("iienterii","yesout");
                dataget=true;
                getContactList();
                last();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("elemententer","NO");

            }
        });

        Log.d("checkbefore00",dataget.toString());
        //while (dataget == false)
        {
            Log.d("checkbefore",dataget.toString());
        }



    }
    private void last()
    {


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String removeph = firebaseUser.getPhoneNumber();
        database_set.retainAll(contact_set);
        database_set.remove(removeph);
        Phone.clear();
        Iterator iterator = database_set.iterator();
        while(iterator.hasNext())
        {
            String element = (String) iterator.next();
            Log.d("checkdatabase",element);
            save_phone.add(element);
            Phone.add(element);

        }
        Name.clear();
        for(String p:Phone)
        {
            String m = contact_list.get(p);
            if(m!=null) {
                save_name.add(m + "&" + p);
                Name.add(m);
            }
        }

        Log.d("sizee",Integer.toString(save_name.size()));
        savePreferences(save_name,s.setACtivity());

        putcont();


    }
    private void putcont()
    {

            Set<String> temp_name_set = loadPreferences1();

            Iterator iterator = temp_name_set.iterator();
            ArrayList<String> temp_name = new ArrayList<>();
            ArrayList<String> temp_phone = new ArrayList<>();
            while(iterator.hasNext())
            {
                String element = (String)iterator.next();
                String [] ele = element.split("&");
                temp_name.add(ele[0]);
                temp_phone.add(ele[1]);

            }
            s.getPhone(phone);
            s.getContacts(name);
            Log.d("iisize",temp_name.toString());

            name = temp_name.toArray(new String[0]);
            phone = temp_phone.toArray(new String[0]);

            if(name.length == 0)// && temp_phone.size() ==0)
            {
                lView.setAdapter(null);
                Toast.makeText(s.setACtivity(),"No Contacts Found",Toast.LENGTH_LONG).show();
            }
            else
            {
                Log.d("falseput",temp_name.toString());

                datataken = true;lAdapter = new ListAdaptor(s.setACtivity(), name, phone);//, msgya, namesa, msgsa);
                lView.setAdapter(lAdapter);

                Chat_Fragment c = new Chat_Fragment();
                c.refresh();
                Runnable progressRunnable = new Runnable() {

                    @Override
                    public void run()
                    {
                        main_page = new Main_Page();
                        main_page.stop();
                    }
                };
                Handler pdCanceller = new Handler();
                pdCanceller.postDelayed(progressRunnable, 1000);
                Log.d("falseput","done");
                lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //String v = Integer.toString(images[i]);
                        intent.putExtra("Contactname", name[i]);
                        intent.putExtra("PhoneNumber", phone[i]);
                        intent.putExtra("pos", i);
                        s.setACtivity().startActivity(intent);
                    }
                });
            }


    }
    private void putcontacts()
    {

        Set<String> temp_name_set = loadPreferences1();

        Iterator iterator = temp_name_set.iterator();
        ArrayList<String> temp_name = new ArrayList<>();
        ArrayList<String> temp_phone = new ArrayList<>();
        while(iterator.hasNext())
        {
            String element = (String)iterator.next();
            String [] ele = element.split("&");
            temp_name.add(ele[0]);
            temp_phone.add(ele[1]);

        }
        s.getPhone(phone);
        s.getContacts(name);
        Log.d("iisize",temp_name.toString());

        name = temp_name.toArray(new String[0]);
        phone = temp_phone.toArray(new String[0]);

        if(name.length == 0)// && temp_phone.size() ==0)
        {
            lView.setAdapter(null);
            Toast.makeText(s.setACtivity(),"No Contacts Found",Toast.LENGTH_LONG).show();
        }
        else
            {
                Log.d("falseput",temp_name.toString());

            datataken = true;lAdapter = new ListAdaptor(s.setACtivity(), name, phone);//, msgya, namesa, msgsa);
            lView.setAdapter(lAdapter);
            Log.d("falseput","done");
            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //String v = Integer.toString(images[i]);
                    intent.putExtra("Contactname", name[i]);
                    intent.putExtra("PhoneNumber", phone[i]);
                    intent.putExtra("pos", i);
                    s.setACtivity().startActivity(intent);
                }
            });
        }

    }

    public void savePreferences(Set<String> name,Context context)

    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("Contacts",name);
        editor.commit();

    }

    public Set<String> loadPreferences1()
    {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(s.setACtivity());
        Set<String> name = new HashSet<>();
        name.add("NULL");
        Set<String> names = prefs.getStringSet("Contacts",name);
        return names;
    }



    private void getContactList()
    {
        ContentResolver cr =s.setACtivity(). getContentResolver();
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

                        Log.i(TAG, "Name: " + name);
                        Log.i(TAG, "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur!=null){
            cur.close();
        }
    }


}


package com.example.wanted.chatplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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


public class Group_Fragment extends Fragment {

    private static sendV s = new sendV();
    public static ListView lView;

    boolean _areLecturesLoaded = false;
    public ProgressDialog progressDialog = null;
    static GroupAdaptor lAdapter;
    static ListAdaptor lAdapter11;

    static Boolean datataken = false;
    static String removeph;
     Boolean firsttime = false;
    private static final int SIGN_IN_REQUEST_CODE = 111;
    private Set<String> Groups = new HashSet<>();
    private ArrayList<String> groupname = new ArrayList<>();
    private ArrayList<String> groudesc = new ArrayList<>();
    private ArrayList<String> groupmember = new ArrayList<>();

    String[] name;
    String[] desc;
    String phone;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.contact_fragment, container, false);
        lView = (ListView)view.findViewById(R.id.androidList);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
         removeph = firebaseUser.getPhoneNumber();


        putGroups();
        return view;
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !_areLecturesLoaded )
        {
            putGroups();
            _areLecturesLoaded = true;

        }
    }
    public void refresh()
    {
        datataken = false;
        Log.d("iidata", datataken.toString());
        putGroups();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(s.setACtivity(), "Signed in successful!", Toast.LENGTH_LONG).show();
                putGroups();
            } else {
                Toast.makeText(s.setACtivity(), "Sign in failed, please try again later", Toast.LENGTH_LONG).show();
            }
        }    }


    public void putGroups()
    {
        datataken = true;
        Chat a = new Chat();
        final Group g = new Group();
        lAdapter = new GroupAdaptor(s.setACtivity(),Group.class, R.layout.create_group_ad, FirebaseDatabase.getInstance().getReference().child(removeph).child("GroupDetails"));
        Log.d(")))",Integer.toString(lAdapter.getCount()));

        lView.setAdapter(lAdapter);
            final ArrayList<String> phone = new ArrayList<>();
            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    for (int j = 0; j < lAdapter.getViewTypeCount() - 1; j++) {
                        Log.d("savegroup", Integer.toString(j));
                        Groups.add(lAdapter.getItem(j).getGroupname() + "&" + lAdapter.getItem(j).getGroupdesc() + "&" + lAdapter.getItem(j).getGroupMembers());
                        Log.d("savegroup", "in");

                    }
                    Log.d("savegroup", "out");
                    saveGroups(Groups, s.setACtivity());
                    Log.d("savegroup", Groups.toString());

                    Intent intent = new Intent(s.setACtivity(), Group_chat_screen.class);
                    intent.putExtra("GroupName", lAdapter.getItem(i).getGroupname());
                    intent.putExtra("GroupMembers", lAdapter.getItem(i).getGroupMembers());
                    intent.putExtra("GroupDescription", lAdapter.getItem(i).getGroupdesc());
                    intent.putExtra("GroupParent", lAdapter.getRef(i).toString());

                    s.setACtivity().startActivity(intent);
                }
            });


    }

    public void saveGroups(Set<String> name,Context context)

    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet("Groups",name);
        editor.commit();

    }

    public Set<String> loadGroups()
    {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(s.setACtivity());
        Set<String> name = new HashSet<>();
        name.add("NULL");
        Set<String> names = prefs.getStringSet("Groups",name);
        return names;
    }



}


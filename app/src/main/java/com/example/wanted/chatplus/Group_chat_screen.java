package com.example.wanted.chatplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.RelativeLayout.LayoutParams;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class Group_chat_screen extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    EmojiconEditText emojiconEditText;
    TextView textView;
    ImageView emojiImageView;
    Button submitButton;
    View rootView;
    private String sen;
    private String sen1;
    private String sentemp;
    EmojIconActions emojIcon;
    private Uri filePath;
    private static final int CHOOSING_IMAGE_REQUEST = 1234;

    private String content;
    boolean value = true;
    ArrayList<String> namey = new ArrayList<String>();
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> msgy = new ArrayList<String>();
    ArrayList<String> msgs = new ArrayList<String>();
    LayoutParams lp;
    ListView lView;
    static sendV s = new sendV();
    private static final int SIGN_IN_REQUEST_CODE = 111;
    private FirebaseListAdapter<ChatMessage> adapter;
    private String loggedInUserName = "";
    public ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 234;
    private String current_user;
    public static String group_name;
    public static String group_parent;

    public static ArrayList<String> Groupmembers;
    public static String group_desc;
    private Chat_Fragment chat_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_chat_screen);
        rootView = findViewById(R.id.root_view);
        emojiImageView = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (Button) findViewById(R.id.submit_btn);
        lView = (ListView)findViewById(R.id.androidList);
        sendV s = new sendV();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        current_user = firebaseUser.getPhoneNumber();
        String element = getIntent().getExtras().getString("GroupParent");
        String [] ele1 = element.split("/");
        group_parent = ele1[ele1.length-1];
        Log.d("--------",group_parent);
        group_desc = getIntent().getExtras().getString("GroupDescription");
        group_name = getIntent().getExtras().getString("GroupName");
        Groupmembers = new ArrayList<>();
        String Groupmembers_String=  getIntent().getExtras().getString("GroupMembers");
        Log.d("adaptor",Groupmembers_String);
        String ele [] = Groupmembers_String.split("&");
        for(int i =0;i<ele.length;i++)
        {
            Groupmembers.add(ele[i]);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(group_name);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in, show list of messages
            progressDialog = ProgressDialog.show(this, "Loading Messages....", "Please wait...", false, false);
            showAllOldMessages();


        }
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        //  textView = (TextView) findViewById(R.id.textView);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiImageView);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });




        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiconEditText.getText().toString().trim().equals("")) {
                    Toast.makeText(Group_chat_screen.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else {
                        Log.d("groupmemname",Groupmembers.toString());
                    for(String g:Groupmembers)
                    {
                        Log.d("groupmem",g);
                        FirebaseDatabase.getInstance()
                                .getReference()
                                .child(g).child("Groups").child(group_name)
                                .push()
                                .setValue(new ChatMessage(emojiconEditText.getText().toString(),
                                        FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                                );
                    }
                    emojiconEditText.setText("");
                }
            }
        });



    }

    public Set<String> loadGroups()
    {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(s.setACtivity());
        Set<String> name = new HashSet<>();
        name.add("NULL");
        Set<String> names = prefs.getStringSet("Groups",name);
        return names;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:// back
                onBackPressed();
                return true;
            case R.id.logout:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Group_chat_screen.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                                //finish();
                                Intent intent = new Intent(Group_chat_screen.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                return true;
            case R.id.groupinfo:
                Group_info_screen cdd = new Group_info_screen(Group_chat_screen.this);

                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                cdd.create();
                    cdd.show();
                cdd.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                return true;
            case R.id.addmember:
                Add_Member cdd1 = new Add_Member(Group_chat_screen.this);

                cdd1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                cdd1.create();
                if(cdd1.p2.length != 0)
                    cdd1.show();
                cdd1.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                return true;
            case R.id.removemember:
                Remove_Member cdd2 = new Remove_Member(Group_chat_screen.this);
                cdd2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                cdd2.create();
                if(cdd2.p1.length != 0)
                    cdd2.show();
                cdd2.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                return true;
            case R.id.exitgroup:
                progressDialog = ProgressDialog.show(this, "Exiting Group....", "Please wait...", false, false);
                exitGroupDetails();
                return true;
            case R.id.change:
                Change_Group_Name cdd3 = new Change_Group_Name(Group_chat_screen.this);
                cdd3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                cdd3.create();
                cdd3.show();
                cdd3.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

                return true;

        }
        return super.onOptionsItemSelected(item);

    }

    public void refreshGroupMembers()
    {
        Groupmembers.clear();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        current_user = firebaseUser.getPhoneNumber();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(current_user).child("GroupDetails");
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
                    if(map.equals(group_name))
                    {
                        deletedir = p.child("groupMembers").getValue().toString();
                        break;
                    }
                }
                String[] ele = deletedir.split("&");
                for (int i=0;i<ele.length;i++)
                {
                    Groupmembers.add(ele[i]);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void exitGroupDetails()
    {
        for(String p:Groupmembers)
        {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(p).child("GroupDetails");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String deletedir = "NULL";

                    for (DataSnapshot p : dataSnapshot.getChildren()) {
                        String map = new String();
                        if (p.hasChildren())
                            map = p.child("groupname").getValue().toString();
                        if (map.equals(group_name)) {
                            deletedir = p.getKey().toString();
                            break;
                        }
                    }
                    ref.child(deletedir).removeValue();
                    Log.d("++++", deletedir);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        exitGroup();
    }

    private void exitGroup()
    {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(current_user).child("Groups");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String deletedir= "NULL";

                for(DataSnapshot p :dataSnapshot.getChildren())
                {
                    String map = p.getKey().toString();
                    if(map.equals(group_name))
                    {
                        deletedir = p.getKey().toString();
                        break;
                    }
                }
                ref.child(deletedir).removeValue();
                Log.d("++++",deletedir);
                updategroup();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updategroup()
    {
        ArrayList<String> updated = new ArrayList<>(Groupmembers);
        updated.remove(current_user);
        String phone = "";
        int flag =0;
        for(String p :updated)
        {
            flag =1;
            phone =  phone +p + "&";
        }
        if(flag ==1)
        {
            phone = phone.substring(0,phone.length()-1);
        }
        for(String p:updated)
        {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child(p).child("GroupDetails")
                    .push()
                    .setValue(new Group(group_name, group_desc,phone)
                    );
        }
        Runnable progressRunnable = new Runnable() {

            @Override
            public void run()
            {
                Intent intent = new Intent(Group_chat_screen.this,Main_Page.class);
                startActivity(intent);
                progressDialog.dismiss();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 2000);


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in successful!", Toast.LENGTH_LONG).show();
                showAllOldMessages();
            } else {
                Toast.makeText(this, "Sign in failed, please try again later", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void showAllOldMessages() {
        loggedInUserName = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Log.d("Main", "user id: " + loggedInUserName);

        DatabaseReference r = FirebaseDatabase.getInstance().getReference().child(current_user).child("Groups").child(group_name);
        Log.d("group",group_name);

        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean email = dataSnapshot.hasChildren();
                Log.d("database","email---->"+email);
                if(email.equals(false))
                {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new GroupMessageAdapter(this, ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference().child(current_user).child("Groups").child(group_name));
        lView.setAdapter(adapter);

    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groupscreen_top, menu);
        MenuItem bedMenuItem = menu.findItem(R.id.groupinfo);
        bedMenuItem.setTitle("Group Info");
        MenuItem bedMenuItem1 = menu.findItem(R.id.logout);
        bedMenuItem1.setTitle("Logout");
        bedMenuItem1.setIcon(R.drawable.logout);
        bedMenuItem.setIcon(R.drawable.logout);
        MenuItem bedMenuItem2 = menu.findItem(R.id.addmember);
        bedMenuItem2.setTitle("Add Member");

        MenuItem bedMenuItem3 = menu.findItem(R.id.removemember);
        bedMenuItem3.setTitle("Remove Member");

        MenuItem bedMenuItem4 = menu.findItem(R.id.exitgroup);
        bedMenuItem4.setTitle("Exit Group");


        MenuItem bedMenuItem5 = menu.findItem(R.id.change);
        bedMenuItem5.setTitle("Change Name And Description");

        return true;
    }


}



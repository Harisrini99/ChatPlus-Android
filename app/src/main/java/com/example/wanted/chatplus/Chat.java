package com.example.wanted.chatplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
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

import java.io.File;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class Chat extends AppCompatActivity {

    private static final String TAG = Chat.class.getSimpleName();
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
    private Uri fileUri;
    Drawable drawable;
    File file;
    private static final int SIGN_IN_REQUEST_CODE = 111;
    private FirebaseListAdapter<ChatMessage> adapter;
    private String loggedInUserName = "";
    public ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 234;
    private String group;
    private String touser;
    private String contact_name;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        rootView = findViewById(R.id.root_view);
        emojiImageView = (ImageView) findViewById(R.id.emoji_btn);
        submitButton = (Button) findViewById(R.id.submit_btn);
        lView = (ListView)findViewById(R.id.androidList);
        sendV s = new sendV();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        group = firebaseUser.getPhoneNumber();
        contact_name=getIntent().getExtras().getString("Contactname");
        touser = getIntent().getExtras().getString("PhoneNumber");
        int i = getIntent().getExtras().getInt("pos");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(contact_name);




        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else
            {
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
                    Toast.makeText(Chat.this, "Please enter some texts!", Toast.LENGTH_SHORT).show();
                } else
                {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(touser).child(group)
                            .push()
                            .setValue(new ChatMessage(emojiconEditText.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            );
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child(group).child(touser)
                            .push()
                            .setValue(new ChatMessage(emojiconEditText.getText().toString(),
                                    FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                            );
                    emojiconEditText.setText("");
                }
            }
        });



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) {
            case R.id.home: // back
                Toast.makeText(Chat.this, "Yged out!", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
            case R.id.menu_sign_out:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Chat.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                                //finish();
                                Intent intent = new Intent(Chat.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        });
                return true;
        }
        return super.onOptionsItemSelected(item);

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

        DatabaseReference r = FirebaseDatabase.getInstance().getReference().child(group).child(touser);
        Log.d("group",group);

        r.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean email = dataSnapshot.hasChildren();
                Log.d("database","email---->"+email);
                if(email.equals(false))
                {
                    progressDialog.dismiss();
                }
                else
                {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new ChatMessageAdapter(this, ChatMessage.class, R.layout.item_in_message,
                FirebaseDatabase.getInstance().getReference().child(group).child(touser));
        lView.setAdapter(adapter);

    }

    public String getLoggedInUserName() {
        return loggedInUserName;
    }



}



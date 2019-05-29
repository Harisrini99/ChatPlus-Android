package com.example.wanted.chatplus;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.HashSet;
import java.util.Set;


public class Main_Page extends AppCompatActivity

{


    private RecyclerView rcvListImg;
    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private static final String TAG = "Main_Page";
    private SectionsPagerAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    public static ProgressBar progressBar;
    AppBarLayout appBarLayout;
    public static ProgressDialog progressDialog;
    sendV s = new sendV();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        Log.d(TAG, "onCreate:Starting.");
        getSupportActionBar().setElevation(1);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        appBarLayout = (AppBarLayout)findViewById(R.id.appbar);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        new LoadingTask().execute();



    }
    private class LoadingTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... values)
        {
            mSectionsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mViewPager.setOffscreenPageLimit(1);
            setupViewPager(mViewPager);
            tabLayout.setupWithViewPager(mViewPager);
            return null;
        }

        protected void onPostExecute(Void value)
        {

        }
    }
    public void start()
    {
        progressDialog = ProgressDialog.show(s.setACtivity(), "Loading...", "Please wait...", false);
    }

    public void stop()
    {
        progressDialog.dismiss();
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Chat_Fragment(), "Chats");
        adapter.addFragment(new Group_Fragment(), "Groups");
        adapter.addFragment(new Contact_Fragment(), "Contacts");
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groupcreation, menu);
        MenuItem bedMenuItem = menu.findItem(R.id.creategroup);
        bedMenuItem.setTitle("Create a New Group");
        MenuItem bedMenuItem1 = menu.findItem(R.id.logout);
        bedMenuItem1.setTitle("Logout");
        bedMenuItem1.setIcon(R.drawable.logout);
        bedMenuItem.setIcon(R.drawable.logout);
        MenuItem bedMenuItem2 = menu.findItem(R.id.refreshcontact);
        bedMenuItem2.setTitle("Refresh Contact");


        return true;
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.creategroup:
                if(!isOnline())
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setMessage("Please Check Your Internet Connection")
                            // .setIcon(R.drawable.alert)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {

                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {

                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    Group_creation_dialog cdd = new Group_creation_dialog(Main_Page.this);

                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    cdd.show();
                    cdd.getWindow().setLayout(675, 1000);
                }
                return true;
            case R.id.logout:
                if(!isOnline())
                {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                    builder.setMessage("Please Check Your Internet Connection")
                            // .setIcon(R.drawable.alert)
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {

                                }
                            })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {

                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    AuthUI.getInstance().signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    savePreferences(s.setACtivity());
                                    Toast.makeText(Main_Page.this, "You have logged out!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(Main_Page.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                }
                            });
                }
                return true;
            case R.id.refreshcontact:
               Contact_Fragment c =new Contact_Fragment();
                c.refresh();
                return true;

        }
        return super.onOptionsItemSelected(item);

    }
    public void savePreferences(Context context)

    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> name = new HashSet<>();
        name.add("NULL");
        editor.putStringSet("Contacts",name);
        editor.putStringSet("Groups",name);
        editor.putStringSet("Chats",name);
        editor.commit();

    }


}





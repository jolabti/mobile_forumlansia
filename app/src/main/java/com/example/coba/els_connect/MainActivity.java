package com.example.coba.els_connect;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Fragment.PostingFragment;
import Fragment.AboutFragment;
import Fragment.CreateFragment;
import Fragment.ProfileFragment;
import GSON.GsonPosting;
import Utils.SessionManager;
import base.BaseOkHttpClient;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import share.Api;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG_MAIN = "mainactivity";
    SessionManager sessionManager;
    Fragment postingFragment;
    Fragment aboutFragment;
    Fragment createFragment;
    Fragment profileFragment;
    FloatingActionButton fab;
    GsonPosting gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postingFragment = PostingFragment.newInstance();
        aboutFragment = AboutFragment.newInstance();
        createFragment = CreateFragment.newInstance();
        profileFragment = ProfileFragment.newInstance();

        sessionManager = new SessionManager(getApplicationContext());
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
//        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//

                setFragmentByPage(createFragment);
                //fab.setVisibility(View.INVISIBLE);
//
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        startRequestApiGetPosting();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = getFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
        transaction.replace(R.id.layout_main, postingFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_notif) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.id_icon_nav_home) {

            setFragmentByPage(postingFragment);

        } else if (id == R.id.id_icon_nav_profile) {

            setFragmentByPage(profileFragment);
        }
        else if (id == R.id.id_icon_nav_about) {

            setFragmentByPage(aboutFragment);

        } else if (id == R.id.id_icon_nav_logout) {

            sessionManager.logoutUser();

//        } else if (id == R.id.nav_label) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startRequestApiGetPosting() {

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(Api.JSON_SHOW_POSTING)
                .tag(TAG_MAIN)
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build();

        BaseOkHttpClient.cancelRequest(TAG_MAIN);

        BaseOkHttpClient.getInstance(getApplicationContext()).newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("cek_kon_register", e.getLocalizedMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {

                    Log.d("responpost", response.body().string());

//                    try {
//                        JSONObject job = new JSONObject(response.body().string());
//                        Log.d("thejob", job.toString());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }


                }
            }
        });

    }

     public void setFragmentByPage(Fragment frg){
         fab.setVisibility(View.INVISIBLE);
         FragmentTransaction transaction = getFragmentManager().beginTransaction().setTransition( FragmentTransaction.TRANSIT_FRAGMENT_OPEN );
         transaction.replace(R.id.layout_main, frg);
         transaction.addToBackStack(null);
         transaction.commit();
     }


}

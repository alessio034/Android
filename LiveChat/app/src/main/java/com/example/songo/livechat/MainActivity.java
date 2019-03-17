package com.example.songo.livechat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    //creamos la instancia de firebase auth
    private FirebaseAuth mAuth;

    //variable para nuestro toolbar
    private Toolbar mToolbar;

    //creacion de las paginas de los fragmentos
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    //creamos un tablayaut
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("LiveChat");


        //Tabs
        mViewPager = (ViewPager)findViewById(R.id.main_TabPager);
        //obtiene las paginas de los fragmentos
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        //asigna las paginas a pager
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //listo los mostramos
        mTabLayout =(TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //comprueba si el usuario esta logeado o no
        if (currentUser == null){
            sendToStart();
        }
    }

    //mueve a la activity dependiendo si esta logeado o no
    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //muestra las opciones del menu
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
            if (item.getItemId() == R.id.main_logout_btn){
                //nos desconecta
                FirebaseAuth.getInstance().signOut();
                sendToStart();
            }
            if (item.getItemId()==R.id.main_settings_btn){

                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);

            }
        if (item.getItemId()==R.id.main_all_btn){

            Intent allIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(allIntent);

        }
        return true;
    }
}

package com.example.chaton;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private SelectionFragmentPageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mViewPager = findViewById(R.id.main_view_pager);
        mTabLayout = findViewById(R.id.main_tab_layout);
        mAdapter = new SelectionFragmentPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            goToLoginActivity();
        }

        else{
            setupUI();
        }
    }

    private void goToLoginActivity() {
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    private void setupUI() {
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_sign_out: {
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        goToLoginActivity();
                    }
                });
                return true;
            }
            case R.id.action_all_users:{
                Intent allUsersIntent = new Intent(MainActivity.this, UsersActivity.class);
                startActivity(allUsersIntent);
                return true;
            }
            case R.id.action_account_settings: {
                Intent accountIntent = new Intent(MainActivity.this, AccountSettingsActivity.class);
                startActivity(accountIntent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
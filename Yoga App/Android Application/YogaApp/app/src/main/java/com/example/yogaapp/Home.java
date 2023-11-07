package com.example.yogaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Home extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    FirebaseAuth mAuth;
    //    Button btn_logout;
//    TextView tv_welcome;
    FirebaseUser currentUser;
    NavigationBarView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottonnav);
        bottomNavigationView.setOnItemSelectedListener(this);
        loadFragment(new Home_Fragment());


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
//        tv_welcome = findViewById(R.id.tv_welcome);
//        btn_logout = findViewById(R.id.btn_logout);
//        if (currentUser == null) {
//            Intent intent = new Intent(Home.this, Login.class);
//            startActivity(intent);
//            finish();
//        } else {
//            tv_welcome.setText("Welcome " + currentUser.getEmail());
//        }
//        btn_logout.setOnClickListener(v -> {
//            mAuth.signOut();
//            Intent intent = new Intent(Home.this, Login.class);
//            startActivity(intent);
//            finish();
//        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.dashboard) {
            fragment = new DashboardFragment();
        } else if (id == R.id.home) {
            fragment = new Home_Fragment();
        } else if (id == R.id.inventory) {
            fragment = new InventoryFragment();
        }
        if (fragment != null) {
            loadFragment(fragment);
        }
        return true;
    }

    void loadFragment(Fragment fragment) {
        //to attach fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.relativelayout, fragment).commit();
    }
}



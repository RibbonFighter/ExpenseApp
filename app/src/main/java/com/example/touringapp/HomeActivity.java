package com.example.touringapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

    //fragment
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;
    private CameraFragment cameraFragment;
    private TripDashFragment tripFragment;

    //firebase
    private FirebaseAuth auth;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("M-Expense");
        setSupportActionBar(toolbar);

        auth=FirebaseAuth.getInstance();

        bottomNavigationView=findViewById(R.id.botNavbar);
        frameLayout=findViewById(R.id.main_frame);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(
                this,drawerLayout,toolbar,R.string.navigation_drawer_expand,R.string.navigation_drawer_collapse
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView=findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        dashboardFragment= new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();
        tripFragment = new TripDashFragment();

        setFragment(dashboardFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
        switch (item.getItemId()){
        case R.id.dashboard:
            setFragment(dashboardFragment);
            bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color);
            return true;

        case R.id.income:
            setFragment(incomeFragment);
            bottomNavigationView.setItemBackgroundResource(R.color.income_color);
            return true;

        case R.id.expense:
            setFragment(expenseFragment);
            bottomNavigationView.setItemBackgroundResource(R.color.expense_color);
            return true;

        default:
            return false;
}
        });


    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }

    }


    @SuppressLint("NonConstantResourceId")
    public void displaySelectedListener(int itemId){
        Fragment fragment = null;

        switch (itemId) {
            case R.id.dashboard:
                fragment = new DashboardFragment();
                break;
            case R.id.income:
                fragment = new IncomeFragment();
                break;

            case R.id.trip:
                fragment = new TripDashFragment();
                break;

            case R.id.expense:
                fragment = new ExpenseFragment();
                break;

            case R.id.camera:
                fragment = new CameraFragment();
                break;

            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        if (fragment!=null){
            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_frame,fragment);
            fragmentTransaction.commit();
        }

        DrawerLayout drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return false;
    }
}
package com.example.panorama;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.panorama.databinding.MainActivityBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    public static float liabilities = 0;
    public static float netWorth = 0;
    public static float cash = 0;
    public static float assets = 0;

    public static JSONArray accounts = new JSONArray();
    public static JSONArray otherAssets = new JSONArray();
    public static JSONObject currentAccount = new JSONObject();

    public static JSONArray spendingBudgets = new JSONArray();
    public static JSONArray savingBudgets = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.getActionBar().show();
        //this.getActionBar().setIcon(R.drawable.logo24);


        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setLogo(R.drawable.logo24);
        }
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_budgets, R.id.navigation_add)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }


}
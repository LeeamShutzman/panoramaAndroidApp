package com.example.panorama;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.panorama.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LogInActivity extends AppCompatActivity {

    public static final String USER_ID = "userId";
    public static final String ACCOUNT_ID = "accountId";
    public static final String ACCOUNT_NAME = "accountName";
    public static final String ITEM_ID = "itemId";
    public static final String SHARED_PREFERENCES = "sharedPreferences";
    public static final String SERVER_URL = "http://192.168.1.179:3001";
    public static final String WITHIN_BUDGET = "withinBudget";
    public static final String LIABILITIES = "liabilities";
    public static final String NET_WORTH = "netWorth";
    public static final String CASH = "cash";
    public static final String ASSETS = "assets";
    EditText username;
    EditText password;
    Button loginButton;

    public LoadingDialog loadingDialog = new LoadingDialog(LogInActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }


        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                // Start new thread and make API call to login endpoint
                loadingDialog.startLoadingDialog();
                new MyTask().execute();
                //loadingDialog.dismissDialog();

            }
        });
    }


    // Creates new thread, running in main thread throws NetworkOnMainThreadException
    private class MyTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params)
        {
            try {
                return testUsernameAndPassword();
            }catch (SocketTimeoutException e){
                e.printStackTrace();
                loadingDialog.dismissDialog();
                return "Server Connection Failed";
            }catch (ConnectException e){
                e.printStackTrace();
                loadingDialog.dismissDialog();
                return "Server Connection Failed";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }finally {
                loadingDialog.dismissDialog();
            }

            return "Wrong Username or Password\n" +
                    "Please try again";
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            // Parse Id JSON
            String userId = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                userId = jsonObject.getString("id");
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                return;
            }

            // If Id is returned, save it to shared preferences and go to home screen
            if(!userId.isEmpty()){
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USER_ID, userId);
                editor.commit();
                startActivity(intent);
            }
        }

        public String testUsernameAndPassword() throws IOException, NoSuchAlgorithmException, JSONException {
            //Password hashing
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getText().toString().getBytes(StandardCharsets.UTF_8));


            // Prepare strings for login API call
            String userString = username.getText().toString();
            String hashedPassword = String.format("%064x", new BigInteger(1, hash));

            // Connect to Login API end point
            URL url = new URL(SERVER_URL+"/login");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setConnectTimeout(10000);

            urlConnection.connect();



            // Make request
            OutputStream out = urlConnection.getOutputStream();
            out.write(("{\n" +
                    "    \"email\" : \"" + userString + "\",\n" +
                    "    \"password\" : \""+ hashedPassword +"\"\n" +
                    "}").getBytes());
            out.flush();
            out.close();

            // Read response, should be user's Id if successful
            String response = "";
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        // ... do something with line
                        response = line;
                    }
                }
            }

            // If id is returned, username and password were correct, go to home screen
            return response;
        }
    }



}
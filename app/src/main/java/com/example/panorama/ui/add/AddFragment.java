package com.example.panorama.ui.add;

import static com.example.panorama.LogInActivity.ACCOUNT_ID;
import static com.example.panorama.LogInActivity.ITEM_ID;
import static com.example.panorama.LogInActivity.SERVER_URL;
import static com.example.panorama.LogInActivity.SHARED_PREFERENCES;
import static com.example.panorama.LogInActivity.USER_ID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.panorama.AddAssetItem;
import com.example.panorama.AssetListItem;
import com.example.panorama.LogInActivity;
import com.example.panorama.MainActivity;
import com.example.panorama.R;
import com.example.panorama.databinding.FragmentAddBinding;
import com.example.panorama.ui.addAssetFirstPage.AddAssetFirstPageFragment;
import com.example.panorama.ui.transactions.TransactionsFragment;
import com.plaid.link.configuration.LinkTokenConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import com.plaid.link.OpenPlaidLink;
import com.plaid.link.Plaid;
import com.plaid.link.configuration.LinkTokenConfiguration;
import com.plaid.link.result.LinkExit;
import com.plaid.link.result.LinkResult;
import com.plaid.link.result.LinkSuccess;

public class AddFragment extends Fragment {


    ImageButton addAssetButton;
    ImageButton addAccountButton;

    private FragmentAddBinding binding;

    ActivityResultLauncher<LinkTokenConfiguration> linkAccountToPlaid = registerForActivityResult(
            new OpenPlaidLink(),
            success -> {
                if(success instanceof LinkSuccess){
                    new AddFragment.GetAccessToken((LinkSuccess) success).execute();
                }else {

                }
            }
    );

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        AddViewModel addViewModel =
                new ViewModelProvider(this).get(AddViewModel.class);

        binding = FragmentAddBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        addAccountButton = root.findViewById(R.id.addAccountButton);
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AddFragment.OpenLink().execute();

            }
        });

        addAssetButton = root.findViewById(R.id.addAssetButton);
        addAssetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AddAssetFirstPageFragment addAssetFirstPageFragment = new AddAssetFirstPageFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.nav_host_fragment, addAssetFirstPageFragment).addToBackStack(null);
                transaction.commit();
            }
        });

        addViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
                //totalView = root.findViewById(R.id.totalView);
            }
        });
        return root;
    }

    // API call to open Link
    private class OpenLink extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params)
        {
            try {
                return getLinkToken();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        private String getLinkToken() throws IOException {
            URL url = new URL(SERVER_URL+"/create_link_token");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            SharedPreferences sharedPreferences = AddFragment.this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

            // Make request
            OutputStream out = urlConnection.getOutputStream();
            out.write(("{\n" +
                    "    \"uid\" : \"" + sharedPreferences.getString(USER_ID, "No user") +"\"\n" +
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

            // Parse Json
            String linkToken = "";
            try {
                JSONObject jsonObject = new JSONObject(response);
                linkToken = jsonObject.getString("link_token");
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }

            return linkToken;
        }

        // Launch Link
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            LinkTokenConfiguration linkTokenConfiguration = new LinkTokenConfiguration.Builder()
                    .token(result)
                    .build();
            System.out.println(result);

            // Opens Link
            linkAccountToPlaid.launch(linkTokenConfiguration);
        }



    }

    // Exchange public token for access token
    private class GetAccessToken extends AsyncTask<String, Void, String> {
        LinkSuccess success;

        public GetAccessToken(LinkSuccess success) {
            this.success =success;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                return exchangePublicToken(success.getPublicToken());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        // Process response
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                MainActivity.liabilities += (float) jsonObject.getDouble("liabilities");
                MainActivity.netWorth += (float) jsonObject.getDouble("netWorth");
                MainActivity.cash += (float) jsonObject.getDouble("cash");
                MainActivity.assets += (float) jsonObject.getDouble("assets");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Make API call
        private String exchangePublicToken(String publicToken) throws IOException, JSONException {
            URL url = new URL(SERVER_URL+"/get_access_token");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            SharedPreferences sharedPreferences = AddFragment.this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

            // Make request
            OutputStream out = urlConnection.getOutputStream();
            String json ="{\n" +
                    "    \"public_token\" : \"" + publicToken + "\",\n" +
                    "    \"uid\" : \""+ sharedPreferences.getString(USER_ID, "") +"\"\n" +
                    "}";
            System.out.println("wawaweewa \n " + json);
            out.write((json).getBytes());
            out.flush();
            out.close();

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

            urlConnection.disconnect();

            return response;

        }
    }








}

package com.example.panorama.ui.transactions;

import static com.example.panorama.LogInActivity.ACCOUNT_ID;
import static com.example.panorama.LogInActivity.ITEM_ID;
import static com.example.panorama.LogInActivity.SERVER_URL;
import static com.example.panorama.LogInActivity.SHARED_PREFERENCES;
import static com.example.panorama.LogInActivity.USER_ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.panorama.AssetListAdapter;
import com.example.panorama.AssetListItem;
import com.example.panorama.MainActivity;
import com.example.panorama.R;
import com.example.panorama.TransactionsAdapter;
import com.example.panorama.TransactionsItem;
import com.example.panorama.ViewPagerAdapter;
import com.example.panorama.ViewPagerItem;
import com.example.panorama.databinding.FragmentHomeBinding;
import com.example.panorama.databinding.FragmentTransactionsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kotlinx.serialization.json.Json;

public class TransactionsFragment extends Fragment implements TransactionsAdapter.OnItemClickListener {

    private RecyclerView recycler_View;
    ArrayList<TransactionsItem> transactionsList;



    private FragmentTransactionsBinding binding;
    String accountId = "";
    String itemId = "";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        new GetTransactions().execute();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TransactionsViewModel homeViewModel =
                new ViewModelProvider(this).get(TransactionsViewModel.class);

        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle bundle = this.getArguments();
        accountId = bundle.getString(ACCOUNT_ID);
        itemId = bundle.getString(ITEM_ID);

        recycler_View = root.findViewById(R.id.recyclerView);

        try {
            updateRecyclerView(MainActivity.currentAccount);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
                //totalView = root.findViewById(R.id.totalView);
            }
        });



        return root;
    }

    private void updateRecyclerView(JSONObject transactionsObject) throws JSONException {

        JSONObject account;
        JSONArray transactions = null;

        MainActivity.currentAccount = transactionsObject;
        transactionsList = new ArrayList<>();

        if(transactionsObject != null) {

            account = transactionsObject.getJSONArray("accounts").getJSONObject(0);
            transactions = transactionsObject.getJSONArray("transactions");
        }

        if(transactions != null){
            for (int i = 0; i < transactions.length(); i++){
                JSONObject transaction = transactions.getJSONObject(i);
                float amount = (float) transaction.getDouble("amount");
                String date = transaction.getString("date");
                JSONObject location = transaction.getJSONObject("location");
                JSONArray category = transaction.getJSONArray("category");
                String name = transaction.getString("name");
                String merchantName = transaction.getString("merchant_name");

                transactionsList.add(new TransactionsItem(amount,date,location,category,name,merchantName));
            }
        }

        TransactionsAdapter transactionsAdapter = new TransactionsAdapter(transactionsList, TransactionsFragment.this);

        recycler_View.setAdapter(transactionsAdapter);
        recycler_View.setHasFixedSize(true);
        recycler_View.setLayoutManager(new LinearLayoutManager(this.getContext()));

    }

    @Override
    public void onItemClick(int position) {

    }

    public class GetTransactions extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params)
        {
            try {
                System.out.println("doing in background");
                return getTransactions();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        SharedPreferences sharedPreferences = TransactionsFragment.this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        private String getTransactions() throws IOException {
            // Connect to Login API end point
            URL url = new URL(SERVER_URL+"/get_transactions");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();



            // Make request
            OutputStream out = urlConnection.getOutputStream();
            String jsonString = "{\n" +
                    "    \"uid\" : \"" + sharedPreferences.getString(USER_ID, "No user") +"\",\n" +
                    "    \"account_id\" : \"" + accountId +"\",\n" +
                    "    \"item_id\" : \"" + itemId +"\"\n" +
                    "}";
            System.out.println(jsonString);
            out.write((jsonString).getBytes());
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
            System.out.println("response: " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            JSONObject transactionsObject = null;
            try {
                transactionsObject = new JSONObject(result);

                System.out.println("post execute");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                updateRecyclerView(transactionsObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }
}

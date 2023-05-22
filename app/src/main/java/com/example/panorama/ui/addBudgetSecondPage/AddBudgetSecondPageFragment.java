package com.example.panorama.ui.addBudgetSecondPage;

import static com.example.panorama.LogInActivity.SERVER_URL;
import static com.example.panorama.LogInActivity.SHARED_PREFERENCES;
import static com.example.panorama.LogInActivity.USER_ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.util.StringUtil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.panorama.AssetListForBudgetAdapter;
import com.example.panorama.AssetListItem;
import com.example.panorama.MainActivity;
import com.example.panorama.R;
import com.example.panorama.databinding.FragmentAddBudgetSecondPageBinding;
import com.example.panorama.ui.addAssetSecondPage.AddAssetSecondPageFragment;
import com.example.panorama.ui.addBudgetFirstPage.AddBudgetFirstPageFragment;
import com.example.panorama.ui.budgets.BudgetsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class AddBudgetSecondPageFragment extends Fragment {

    private AddBudgetSecondPageViewModel mViewModel;

    public static AddBudgetSecondPageFragment newInstance() {
        return new AddBudgetSecondPageFragment();
    }

    private FragmentAddBudgetSecondPageBinding binding;

    private TextView budgetNameView;
    private TextView budgetTypeView;
    private TextView budgetLimitView;

    private RecyclerView recyclerView;
    private ImageButton addButton;

    AssetListForBudgetAdapter assetListForBudgetAdapter;

    ArrayList<AssetListItem> assets;
    public ArrayList<String> associatedAccounts;

    String budgetName;
    String budgetType;
    float budgetLimit;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAddBudgetSecondPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        budgetNameView = root.findViewById(R.id.budgetName);
        budgetTypeView = root.findViewById(R.id.budgetType);
        budgetLimitView = root.findViewById(R.id.budgetLimit);

        recyclerView = root.findViewById(R.id.recyclerView);
        addButton = root.findViewById(R.id.addButton);

        Bundle bundle = this.getArguments();
        //assetType = bundle.getString("assetType");
        //assetIcon = bundle.getInt("assetIcon");
        budgetName = bundle.getString("budgetName");
        budgetType = bundle.getString("budgetType");
        budgetLimit = bundle.getFloat("budgetLimit");

        budgetNameView.setText(budgetName);
        budgetTypeView.setText(budgetName.substring(0,1).toUpperCase() + budgetName.substring(1));
        budgetLimitView.setText(NumberFormat.getCurrencyInstance().format((budgetLimit/100)));

        associatedAccounts = new ArrayList<>();

        try {
            initializeRecyclerView(MainActivity.accounts, MainActivity.otherAssets);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(AssetListItem assetListItem : assetListForBudgetAdapter.selectedList){
                    associatedAccounts.add("\"" + assetListItem.getAccountId() + "\"");
                }
                new AddBudget().execute();

                BudgetsFragment budgetsFragment = new BudgetsFragment();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.nav_host_fragment, budgetsFragment).addToBackStack(null);
                transaction.commit();

            }
        });



        return root;
    }

    private void initializeRecyclerView(JSONArray accounts, JSONArray otherAssets) throws JSONException {
        assets = new ArrayList<>();

        if(accounts != null) {
            for (int i = 0; i < accounts.length(); i++) {
                JSONObject account = accounts.getJSONObject(i);
                String accountId = account.getString("account_id");
                String itemId = account.getString("item_id");
                float balance = (float) account.getJSONObject("balances").getDouble("current");
                String name = (account.getString("name") + " " + account.getString("mask"));
                assets.add(new AssetListItem(accountId, "Bank of America", name, balance, itemId));
            }
        }else{
            System.out.println("accounts is null");
        }

        /*if(otherAssets != null){
            for(int i = 0; i < otherAssets.length(); i++){
                JSONObject asset = otherAssets.getJSONObject(i);
                String assetType = asset.getString("asset_type");
                double assetIcon = asset.getDouble("asset_icon");
                String assetName = asset.getString("asset_name");
                float assetValue = (float) asset.getDouble("asset_value");
                assets.add(new AssetListItem(String.valueOf(assetIcon), assetType, assetName, assetValue, "asset"));
            }
        }else {
            System.out.println("otherAssets is null");
        }*/

        assetListForBudgetAdapter = new AssetListForBudgetAdapter(assets);

        recyclerView.setAdapter(assetListForBudgetAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    public class AddBudget extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params)
        {
            try {
                System.out.println("doing in background");
                return addBudget();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        SharedPreferences sharedPreferences = AddBudgetSecondPageFragment.this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        private String addBudget() throws IOException {
            // Connect to add asset API end point
            URL url = new URL(SERVER_URL+"/add_budget");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();

            // Make request
            OutputStream out = urlConnection.getOutputStream();
            out.write(("{\n" +
                    "\"uid\" : \"" + sharedPreferences.getString(USER_ID, "") + "\",\n" +
                    "\"budget_name\" : \"" + budgetName + "\",\n" +
                    "\"budget_type\" : \"" + budgetType + "\",\n" +
                    "\"budget_limit\" : " + budgetLimit + ",\n" +
                    "\"associated_accounts\" : " + associatedAccounts.toString() + "\n" +

                    "}").getBytes());
            out.flush();
            out.close();

            // Read response
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
        }


    }


}
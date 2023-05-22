package com.example.panorama.ui.home;

import static com.example.panorama.LogInActivity.ACCOUNT_ID;
import static com.example.panorama.LogInActivity.ACCOUNT_NAME;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.panorama.AssetListAdapter;
import com.example.panorama.AssetListItem;
import com.example.panorama.LoadingDialog;
import com.example.panorama.MainActivity;
import com.example.panorama.R;
import com.example.panorama.ViewPagerAdapter;
import com.example.panorama.ViewPagerItem;
import com.example.panorama.databinding.FragmentHomeBinding;
import com.example.panorama.databinding.FragmentNotificationsBinding;
import com.example.panorama.ui.add.AddFragment;
import com.example.panorama.ui.budgets.BudgetsFragment;
import com.example.panorama.ui.notifications.NotificationsViewModel;
import com.example.panorama.ui.transactions.TransactionsFragment;

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

public class HomeFragment extends Fragment implements AssetListAdapter.OnItemClickListener{
    ViewPager2 viewPager;
    ArrayList<ViewPagerItem> viewPagerItems;

    private RecyclerView recyclerView;
    ArrayList<AssetListItem> assets;

    public LoadingDialog loadingDialog;

    private FragmentHomeBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        loadingDialog = new LoadingDialog(getActivity());
        getBalance();
    }

    private void getBalance(){
        loadingDialog.startLoadingDialog();
        new GetBalance().execute();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = root.findViewById(R.id.text_home);

        viewPager = root.findViewById(R.id.viewpager);
        recyclerView = root.findViewById(R.id.recyclerView);
        updateViewPager();
        try {
            updateRecyclerView(MainActivity.accounts, MainActivity.otherAssets);
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

    @Override
    public void onItemClick(int position) {
        /*Intent intent = new Intent(this, RatingView.class);

        intent.putExtra("rating", myRatings.get(position));
        startActivity(intent);*/

        Bundle bundle = new Bundle();
        AssetListItem asset = assets.get(position);
        bundle.putString(ACCOUNT_ID,asset.getAccountId());
        bundle.putString(ITEM_ID,asset.getItemId());

        MainActivity.currentAccount = new JSONObject();

        TransactionsFragment transactionsFragment = new TransactionsFragment();
        transactionsFragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.nav_host_fragment, transactionsFragment).addToBackStack(null);
        transaction.commit();
    }

    private void updateRecyclerView(JSONArray accounts, JSONArray otherAssets) throws JSONException {
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

        if(otherAssets != null){
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
        }

        AssetListAdapter assetListAdapter = new AssetListAdapter(assets, HomeFragment.this);

        recyclerView.setAdapter(assetListAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
    }

    private void updateViewPager(){
        int[] arrows = {R.drawable.blank,R.drawable.ic_keyboard_arrow_left_black_24dp,R.drawable.ic_keyboard_arrow_right_black_24dp};
        String[] labels = {getString(R.string.liabilities),getString(R.string.net_worth), getString(R.string.cash),getString(R.string.assets)};

        viewPagerItems = new ArrayList<>();

        // Look at constructor, adding appropriate labels and arrows
        viewPagerItems.add(new ViewPagerItem(MainActivity.liabilities,labels[0],arrows[0],arrows[2]));
        viewPagerItems.add(new ViewPagerItem(MainActivity.netWorth,labels[1],arrows[1],arrows[2]));
        viewPagerItems.add(new ViewPagerItem(MainActivity.cash,labels[2],arrows[1],arrows[2]));
        viewPagerItems.add(new ViewPagerItem(MainActivity.assets,labels[3],arrows[1],arrows[0]));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(viewPagerItems);

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setClipToPadding(false);
        viewPager.setClipChildren(false);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(1, false);
        viewPager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public class GetBalance extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params)
        {
            try {
                System.out.println("doing in background");
                return getBalance();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        SharedPreferences sharedPreferences = HomeFragment.this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        private String getBalance() throws IOException {
            // Connect to Login API end point
            URL url = new URL(SERVER_URL+"/get_balance");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();



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
            System.out.println("response: " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);
            JSONObject balanceObject;
            try {
                balanceObject = new JSONObject(result);
                MainActivity.liabilities = (float) balanceObject.getDouble("liabilities");
                MainActivity.netWorth = (float) balanceObject.getDouble("netWorth");
                MainActivity.cash = (float) balanceObject.getDouble("cash");
                MainActivity.assets = (float) balanceObject.getDouble("assets");

                MainActivity.accounts = balanceObject.getJSONArray("accounts");
                MainActivity.otherAssets = balanceObject.getJSONArray("other_assets");

                MainActivity.spendingBudgets = balanceObject.getJSONArray("spending_budgets");
                MainActivity.savingBudgets = balanceObject.getJSONArray("saving_budgets");

                System.out.println("post execute");
                System.out.println(balanceObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            updateViewPager();
            try {
                updateRecyclerView(MainActivity.accounts, MainActivity.otherAssets);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                loadingDialog.dismissDialog();
            }
        }


    }


}

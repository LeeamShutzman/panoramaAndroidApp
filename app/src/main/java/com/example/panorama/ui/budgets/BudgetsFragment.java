package com.example.panorama.ui.budgets;

import static com.example.panorama.LogInActivity.SERVER_URL;
import static com.example.panorama.LogInActivity.SHARED_PREFERENCES;
import static com.example.panorama.LogInActivity.USER_ID;
import static com.example.panorama.LogInActivity.WITHIN_BUDGET;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.panorama.AssetListAdapter;
import com.example.panorama.BudgetListAdapter;
import com.example.panorama.BudgetListItem;
import com.example.panorama.LoadingDialog;
import com.example.panorama.LogInActivity;
import com.example.panorama.MainActivity;
import com.example.panorama.R;
import com.example.panorama.databinding.FragmentBudgetsBinding;
import com.example.panorama.databinding.FragmentNotificationsBinding;
import com.example.panorama.ui.addBudgetFirstPage.AddBudgetFirstPageFragment;
import com.example.panorama.ui.addBudgetSecondPage.AddBudgetSecondPageFragment;
import com.example.panorama.ui.home.HomeFragment;
import com.example.panorama.ui.notifications.NotificationsViewModel;
import com.google.android.material.color.MaterialColors;

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

public class BudgetsFragment extends Fragment implements BudgetListAdapter.OnItemClickListener {

    private FragmentBudgetsBinding binding;

    private RecyclerView recyclerView;
    private TextView withinBudget;
    private ImageButton addButton;



    ArrayList<BudgetListItem> budgetsArray;

    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BudgetsViewModel budgetsViewModel =
                new ViewModelProvider(this).get(BudgetsViewModel.class);
        binding = FragmentBudgetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerView);
        withinBudget = root.findViewById(R.id.withinBudget);
        addButton = root.findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddBudgetFirstPageFragment addBudgetFirstPageFragment = new AddBudgetFirstPageFragment();

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setReorderingAllowed(true);
                transaction.replace(R.id.nav_host_fragment, addBudgetFirstPageFragment).addToBackStack(null);
                transaction.commit();
            }
        });

        try {
            updateRecyclerView(MainActivity.spendingBudgets, MainActivity.savingBudgets);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sharedPreferences.getBoolean(WITHIN_BUDGET, true)){
            withinBudget.setText("Within Budget");
            TypedValue value = new TypedValue ();
            getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorAccent, value, true);
            withinBudget.setTextColor(value.data);
        }else {
            withinBudget.setText("Not Within Budget");
            withinBudget.setTextColor(getResources().getColor(R.color.red));
        }

        if(budgetsArray.isEmpty()){
            withinBudget.setText("No Budgets Set");
            withinBudget.setTextColor(getResources().getColor(R.color.gray));
        }

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            updateRecyclerView(MainActivity.spendingBudgets, MainActivity.savingBudgets);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(sharedPreferences.getBoolean(WITHIN_BUDGET, true)){
            withinBudget.setText("Within Budget");
            TypedValue value = new TypedValue ();
            getContext().getTheme().resolveAttribute(com.google.android.material.R.attr.colorAccent, value, true);
            withinBudget.setTextColor(value.data);
        }else {
            withinBudget.setText("Not Within Budget");
            withinBudget.setTextColor(getResources().getColor(R.color.red));
        }

        if(budgetsArray.isEmpty()){
            withinBudget.setText("No Budgets Set");
            withinBudget.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    public void updateRecyclerView(JSONArray spendingBudgets, JSONArray savingBudgets) throws JSONException {
        budgetsArray = new ArrayList<>();

        sharedPreferences = BudgetsFragment.this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(WITHIN_BUDGET, true);

        if(spendingBudgets != null) {
            for (int i = 0; i < spendingBudgets.length(); i++) {
                JSONObject spendingBudget = spendingBudgets.getJSONObject(i);
                String budgetName = spendingBudget.getString("budget_name");
                String budgetType = spendingBudget.getString("budget_type");
                float budgetLimit = (float) spendingBudget.getDouble("budget_limit");
                float budgetBalance = (float) spendingBudget.getDouble("budget_balance");

                if(budgetBalance > budgetLimit){
                    editor.putBoolean(WITHIN_BUDGET, false);
                }
                budgetsArray.add(new BudgetListItem(budgetName,budgetType,budgetLimit,budgetBalance));
            }
        }

        editor.commit();

        BudgetListAdapter budgetListAdapter = new BudgetListAdapter(budgetsArray, BudgetsFragment.this);

        recyclerView.setAdapter(budgetListAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));


    }

    @Override
    public void onItemClick(int position) {

    }

    public class GetBudgets extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params)
        {
            try {
                System.out.println("doing in background");
                return getBudgets();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }



        private String getBudgets() throws IOException {
            // Connect to Login API end point
            URL url = new URL(SERVER_URL+"/get_budgets");
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
            JSONObject budgetsObject;
            try {
                budgetsObject = new JSONObject(result);

                MainActivity.spendingBudgets = budgetsObject.getJSONArray("spending_budgets");
                MainActivity.savingBudgets = budgetsObject.getJSONArray("saving_budgets");

                System.out.println("post execute");
                System.out.println(budgetsObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                updateRecyclerView(MainActivity.spendingBudgets, MainActivity.savingBudgets);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }
}

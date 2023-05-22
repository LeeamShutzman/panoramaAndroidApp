package com.example.panorama.ui.addAssetSecondPage;

import static com.example.panorama.LogInActivity.SERVER_URL;
import static com.example.panorama.LogInActivity.SHARED_PREFERENCES;
import static com.example.panorama.LogInActivity.USER_ID;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.panorama.MainActivity;
import com.example.panorama.R;
import com.example.panorama.databinding.FragmentAddAssetFirstPageBinding;
import com.example.panorama.databinding.FragmentAddAssetSecondPageBinding;
import com.example.panorama.ui.home.HomeFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

public class AddAssetSecondPageFragment extends Fragment {

    private FragmentAddAssetSecondPageBinding binding;

    EditText assetName;
    EditText assetValue;
    ImageButton addButton;
    String assetType;
    int assetIcon;

    public static AddAssetSecondPageFragment newInstance() {
        return new AddAssetSecondPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddAssetSecondPageBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        assetName = root.findViewById(R.id.assetName);
        assetValue = root.findViewById(R.id.assetValue);
        addButton = root.findViewById(R.id.addButton);

        Bundle bundle = this.getArguments();
        assetType = bundle.getString("assetType");
        assetIcon = bundle.getInt("assetIcon");

        assetValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            private String current = "";

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = assetValue.getText().toString();
                if(!s.equals(current)){
                    assetValue.removeTextChangedListener(this);

                    String cleanString = s.replaceAll("[$,.]","");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;

                    System.out.println(s);
                    System.out.println(cleanString);
                    System.out.println(parsed);
                    System.out.println(formatted);
                    System.out.println(current);

                    assetValue.setText(formatted);
                    assetValue.setSelection(formatted.length());

                    assetValue.addTextChangedListener(this);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddAsset().execute();
            }
        });


        return root;
    }

    public class AddAsset extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params)
        {
            String assetValueString = assetValue.getText().toString().replaceAll("[$,]","");
            try {
                System.out.println("doing in background");
                return addAsset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "fail";
        }

        SharedPreferences sharedPreferences = AddAssetSecondPageFragment.this.getActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);

        private String addAsset() throws IOException {
            // Connect to add asset API end point
            URL url = new URL(SERVER_URL+"/add_asset");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();


            String cleanString = assetValue.getText().toString().replaceAll("[$,]","");
            double parsed = Double.parseDouble(cleanString);

            // Make request
            OutputStream out = urlConnection.getOutputStream();
            out.write(("{\n" +
                    "\"uid\" : \"" + sharedPreferences.getString(USER_ID, "") + "\",\n" +
                    "\"asset_type\" : \"" + assetType + "\",\n" +
                    "\"asset_icon\" : " + assetIcon + ",\n" +
                    "\"asset_name\" : \"" + assetName.getText().toString() + "\",\n" +
                    "\"asset_value\" : " + parsed + "\n" +
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
package com.example.panorama.ui.addBudgetFirstPage;

import androidx.fragment.app.FragmentTransaction;

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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.panorama.R;
import com.example.panorama.databinding.FragmentAddBudgetFirstPageBinding;
import com.example.panorama.ui.addBudgetSecondPage.AddBudgetSecondPageFragment;

import java.text.NumberFormat;

public class AddBudgetFirstPageFragment extends Fragment {

    private AddBudgetFirstPageViewModel mViewModel;

    private FragmentAddBudgetFirstPageBinding binding;

    private EditText budgetName;
    private RadioGroup budgetType;
    private EditText budgetLimit;
    private ImageButton next;
    private String budgetTypeString;
    private TextView budgetLimitLabel;

    public static AddBudgetFirstPageFragment newInstance() {
        return new AddBudgetFirstPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAddBudgetFirstPageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        budgetName = root.findViewById(R.id.budgetName);
        budgetType = root.findViewById(R.id.budgetType);
        budgetLimit = root.findViewById(R.id.budgetLimit);
        next = root.findViewById(R.id.nextButton);
        budgetLimitLabel = root.findViewById(R.id.budgetLimitLabel);
        budgetTypeString = "";
        budgetLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            private String current = "";

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = budgetLimit.getText().toString();
                if(!s.equals(current)){
                    budgetLimit.removeTextChangedListener(this);

                    String cleanString = s.replaceAll("[$,.]","");
                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;

                    System.out.println(s);
                    System.out.println(cleanString);
                    System.out.println(parsed);
                    System.out.println(formatted);
                    System.out.println(current);

                    budgetLimit.setText(formatted);
                    budgetLimit.setSelection(formatted.length());

                    budgetLimit.addTextChangedListener(this);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        budgetType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.spendingOption:
                        budgetTypeString = "spending";
                        budgetLimitLabel.setText("Limit:");
                        break;
                    case R.id.savingOption:
                        budgetTypeString = "saving";
                        budgetLimitLabel.setText("Monthly Goal:");
                        break;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cleanString = budgetLimit.getText().toString().replaceAll("[$,]","");
                double parsed = Double.parseDouble(cleanString);

                // Input validation
                if(budgetName.getText().length() > 0 && budgetTypeString.length() > 0 && parsed > 0) {
                    Bundle bundle = new Bundle();

                    bundle.putString("budgetName", budgetName.getText().toString());
                    bundle.putString("budgetType",budgetTypeString);
                    bundle.putFloat("budgetLimit", (float) parsed);

                    AddBudgetSecondPageFragment addBudgetSecondPageFragment = new AddBudgetSecondPageFragment();
                    addBudgetSecondPageFragment.setArguments(bundle);

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.setReorderingAllowed(true);
                    transaction.replace(R.id.nav_host_fragment, addBudgetSecondPageFragment).addToBackStack(null);
                    transaction.commit();
                }else {
                    Toast.makeText(getContext(), "Fill out all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }


}
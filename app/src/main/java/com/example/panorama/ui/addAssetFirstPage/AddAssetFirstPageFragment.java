package com.example.panorama.ui.addAssetFirstPage;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.panorama.AddAssetAdapter;
import com.example.panorama.AddAssetItem;
import com.example.panorama.R;
import com.example.panorama.databinding.FragmentAddAssetFirstPageBinding;
import com.example.panorama.ui.addAssetSecondPage.AddAssetSecondPageFragment;
import com.example.panorama.ui.home.HomeViewModel;

import java.util.ArrayList;

public class AddAssetFirstPageFragment extends Fragment implements AddAssetAdapter.OnItemClickListener {


    private FragmentAddAssetFirstPageBinding binding;

    private RecyclerView recyclerView;
    ArrayList<AddAssetItem> assetTypes;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentAddAssetFirstPageBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        assetTypes = new ArrayList<>();

        assetTypes.add(new AddAssetItem("Home", R.drawable.ic_home_black_24dp));
        assetTypes.add(new AddAssetItem("Automobile",R.drawable.ic_directions_car_black_24dp));
        assetTypes.add(new AddAssetItem("Rental Property", R.drawable.ic_rental_property_24));
        assetTypes.add(new AddAssetItem("Personal Belonging", R.drawable.ic_personal_belonging_24));

        AddAssetAdapter addAssetAdapter = new AddAssetAdapter(assetTypes, AddAssetFirstPageFragment.this);

        recyclerView = root.findViewById(R.id.recyclerView);

        recyclerView.setAdapter(addAssetAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));


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
        Bundle bundle = new Bundle();

        AddAssetItem addAssetItem = assetTypes.get(position);
        bundle.putString("assetType", addAssetItem.getAssetType());
        bundle.putInt("assetIcon", addAssetItem.getAssetIcon());

        AddAssetSecondPageFragment addAssetSecondPageFragment = new AddAssetSecondPageFragment();
        addAssetSecondPageFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.nav_host_fragment, addAssetSecondPageFragment).addToBackStack(null);
        transaction.commit();

    }
}
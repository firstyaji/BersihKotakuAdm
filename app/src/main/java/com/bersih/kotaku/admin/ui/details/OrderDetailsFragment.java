package com.bersih.kotaku.admin.ui.details;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bersih.kotaku.admin.R;
import com.bersih.kotaku.admin.databinding.FragmentOrderDetailsBinding;
import com.bersih.kotaku.admin.epoxy.PickerController;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderDetailsFragment extends Fragment {

    private FragmentOrderDetailsBinding viewBinding;
    private OrderDetailsViewModel mViewModel;
    private PickerController pickerController = new PickerController();

    public static OrderDetailsFragment newInstance() {
        return new OrderDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentOrderDetailsBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrderDetailsViewModel.class);

        pickerController.setHeaderListener(this::popUp);
        pickerController.setSubListener(subscription -> {
            String map = "http://maps.google.co.in/maps?q=" + subscription.addressName + " " + subscription.addressDetails;
            if (subscription.addressDetails.contains("https")) {
                map = subscription.addressDetails;
            }
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
            startActivity(i);
        });

        viewBinding.orderDetailsEpoxy.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        viewBinding.orderDetailsEpoxy.setControllerAndBuildModels(pickerController);

        OrderDetailsFragmentArgs args = OrderDetailsFragmentArgs.fromBundle(getArguments());
        mViewModel.listenPicker(args.getSubsID()).observe(getViewLifecycleOwner(), v -> pickerController.setPickerList(v));
        mViewModel.listenSubs(args.getSubsID()).observe(getViewLifecycleOwner(), v -> v.ifPresent(it -> pickerController.setSubscription(it)));

        viewBinding.orderDetailsPicker.setOnClickListener(v -> {
            v.setVisibility(View.INVISIBLE);
            mViewModel.pickTrash();
        });
    }

    public void popUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }
}
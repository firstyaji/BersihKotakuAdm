package com.bersih.kotaku.admin.ui.order;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bersih.kotaku.admin.R;
import com.bersih.kotaku.admin.databinding.FragmentOrderBinding;
import com.bersih.kotaku.admin.epoxy.OrderPendingController;
import com.bersih.kotaku.admin.epoxy.model.SubPendingModel;
import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrderFragment extends Fragment {

    private FragmentOrderBinding viewBinding;
    private OrderViewModel mViewModel;
    private OrderPendingController orderPendingController = new OrderPendingController();

    public static OrderFragment newInstance() {
        return new OrderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentOrderBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        viewBinding.orderPendingEpoxy.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        viewBinding.orderPendingEpoxy.setControllerAndBuildModels(orderPendingController);

        orderPendingController.setOnClickListener(new SubPendingModel.OnClickListener() {
            @Override
            public void onClick(Subscription subscription) {
                mViewModel.accept(subscription);
            }

            @Override
            public void onReject(Subscription subscription) {
                mViewModel.reject(subscription);
            }
        });

        mViewModel.listenPendingSubs().observe(getViewLifecycleOwner(), v -> { orderPendingController.setSubscriptionList(v); });
    }
}
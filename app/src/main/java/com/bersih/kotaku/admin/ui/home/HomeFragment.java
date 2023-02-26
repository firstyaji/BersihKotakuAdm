package com.bersih.kotaku.admin.ui.home;

import androidx.lifecycle.ViewModelProvider;

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
import com.bersih.kotaku.admin.databinding.FragmentHomeBinding;
import com.bersih.kotaku.admin.epoxy.HomeController;
import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;
import dagger.hilt.android.lifecycle.HiltViewModel;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding viewBinding;
    private HomeViewModel mViewModel;
    private HomeController homeController = new HomeController();

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        viewBinding.homeEpoxy.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        viewBinding.homeEpoxy.setControllerAndBuildModels(homeController);

        homeController.setSubOnClickListener(this::gotoDetails);

        mViewModel.listenSubs().observe(getViewLifecycleOwner(), v -> homeController.setSubscriptionList(v));
    }

    void gotoDetails(Subscription subscription) {
        HomeFragmentDirections.ActionHomeFragmentToOrderDetailsFragment action = HomeFragmentDirections
                .actionHomeFragmentToOrderDetailsFragment(subscription.id, subscription.userID);
        NavHostFragment.findNavController(this).navigate(action);
    }
}
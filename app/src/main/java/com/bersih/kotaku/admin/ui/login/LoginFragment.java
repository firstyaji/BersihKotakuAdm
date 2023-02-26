package com.bersih.kotaku.admin.ui.login;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bersih.kotaku.admin.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(new FirebaseAuthUIActivityResultContract(), this::onSignInResult);
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        mViewModel.getLoginEvent().observe(getViewLifecycleOwner(), loginEvent -> {
            if (loginEvent instanceof LoginEvent.LoginSuccess) {
                moveToMain();
            } else if (loginEvent instanceof LoginEvent.LoginFailed) {
                showErrorMessage(((LoginEvent.LoginFailed) loginEvent).message);
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Change to requestLogin for login page
            // change to login for direct login
            // requestLogin();
            login();
        }
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK && FirebaseAuth.getInstance().getCurrentUser() != null) {
            mViewModel.loginRegister(FirebaseAuth.getInstance().getCurrentUser());
        } else {
            if (response != null && response.getError() != null) {
                showErrorMessage(String.format(Locale.ENGLISH, "can't login with error code %d", response.getError().getErrorCode()));
            }
        }
    }

    private void showErrorMessage(String message) {
        Toast.makeText(requireContext().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void moveToMain() {
        NavHostFragment.findNavController(this)
                .navigate(R.id.homeFragment, null,
                        new NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(R.id.homeFragment, true)
                                .build());
    }

    private void requestLogin() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(false).build()
        );
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void login() {
        String email = "root@email.com";
        String password = "rahasia";
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
           if (task.isSuccessful() && task.getResult().getUser() != null) {
               mViewModel.loginRegister(task.getResult().getUser());
           }
        });
    }
}
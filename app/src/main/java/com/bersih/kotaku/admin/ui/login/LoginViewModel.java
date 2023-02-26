package com.bersih.kotaku.admin.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.bersih.kotaku.admin.firebase.model.Admin;
import com.bersih.kotaku.admin.firebase.service.AdminService;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.rxrelay3.BehaviorRelay;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;

class LoginEvent {
    static class Idle extends LoginEvent{}
    static class LoginSuccess extends LoginEvent{
        Admin admin;
        LoginSuccess(Admin user) { this.admin = user; }
    }
    static class LoginFailed extends LoginEvent{
        String message;
        LoginFailed(String message){ this.message = message; }
    }
}

@HiltViewModel
public class LoginViewModel extends ViewModel {
    private final AdminService adminService;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final BehaviorRelay<LoginEvent> loginEvent = BehaviorRelay.createDefault(new LoginEvent.Idle());

    public LiveData<LoginEvent> getLoginEvent() {
        return LiveDataReactiveStreams.fromPublisher(loginEvent.toFlowable(BackpressureStrategy.LATEST));
    }

    @Inject
    public LoginViewModel(AdminService adminService) {
        this.adminService = adminService;
    }

    public void loginRegister(FirebaseUser firebaseUser) {
        loginEvent.accept(new LoginEvent.Idle());
        disposable.add(
                adminService.getAdmin(firebaseUser.getUid()).doOnNext(v -> {
                    v.ifPresent(it -> {
                        loginEvent.accept(new LoginEvent.LoginSuccess(it));
                    });
                }).doOnError(v -> {
                    loginEvent.accept(new LoginEvent.LoginFailed(v.getMessage()));
                }).observeOn(Schedulers.io()).subscribe()
        );
    }
}
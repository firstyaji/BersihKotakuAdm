package com.bersih.kotaku.admin.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.bersih.kotaku.admin.firebase.service.SubscriptionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class HomeViewModel extends ViewModel {
    private final SubscriptionService subscriptionService;
    @Inject public HomeViewModel(SubscriptionService subscriptionService){
        this.subscriptionService = subscriptionService;
    }

    public LiveData<List<Subscription>> listenSubs() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        Observable<List<Subscription>> observable = this.subscriptionService.listenCurrentActiveSubs()
                .subscribeOn(Schedulers.io());
        return LiveDataReactiveStreams.fromPublisher(observable.toFlowable(BackpressureStrategy.LATEST));
    }
}
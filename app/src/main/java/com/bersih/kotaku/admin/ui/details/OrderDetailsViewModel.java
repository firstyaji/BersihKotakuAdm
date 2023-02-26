package com.bersih.kotaku.admin.ui.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.bersih.kotaku.admin.firebase.model.Notification;
import com.bersih.kotaku.admin.firebase.model.Picker;
import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.bersih.kotaku.admin.firebase.model.User;
import com.bersih.kotaku.admin.firebase.service.SubscriptionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class OrderDetailsViewModel extends ViewModel {
    private final SubscriptionService subscriptionService;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private Optional<Subscription> subscription = Optional.empty();

    @Inject public OrderDetailsViewModel(SubscriptionService subscriptionService){
        this.subscriptionService = subscriptionService;
    }

    public LiveData<Optional<User>> listenUser(String userID) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        Observable<Optional<User>> observable = this.subscriptionService.listenUser(userID)
                .subscribeOn(Schedulers.io());
        return LiveDataReactiveStreams.fromPublisher(observable.toFlowable(BackpressureStrategy.LATEST));
    }

    public LiveData<Optional<Subscription>> listenSubs(String subsID) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        Observable<Optional<Subscription>> observable = this.subscriptionService.listenSubs(subsID)
                .doOnNext(v-> subscription = v)
                .subscribeOn(Schedulers.io());
        return LiveDataReactiveStreams.fromPublisher(observable.toFlowable(BackpressureStrategy.LATEST));
    }

    public LiveData<List<Picker>> listenPicker(String subsID) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        Observable<List<Picker>> observable = this.subscriptionService.listenPicker(subsID)
                .subscribeOn(Schedulers.io());
        return LiveDataReactiveStreams.fromPublisher(observable.toFlowable(BackpressureStrategy.LATEST));
    }

    public void pickTrash() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        if (!subscription.isPresent()) {
            return;
        }

        Subscription current = subscription.get();
        long now = Instant.now().toEpochMilli();
        current.lastPicker = now;
        Picker picker = new Picker();
        picker.adminID = firebaseUser.getUid();
        picker.createdAt = now;
        picker.userID = current.userID;
        picker.packID = current.packID;
        picker.subscriptionID = current.id;
        picker.title = String.format(Locale.ENGLISH, "Sampah telah diambil", current.id);
        disposable.add(subscriptionService.insertPicker(picker).concatMap(v -> {
            if (v.isPresent()) {
                Notification notification = new Notification();
                notification.createdAt = Instant.now().toEpochMilli();
                notification.isRead = false;
                notification.userID = current.userID;
                notification.payload = v.get();
                notification.type = "Picker";
                notification.title = "Sampah telah diambil";
                return subscriptionService.insertNotification(notification);
            } else {
                return Observable.error(new Throwable("failed to put notification pick"));
            }
        }).concatMap(v -> subscriptionService.updateLastPicker(current)).observeOn(Schedulers.io()).onErrorComplete().subscribe());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
package com.bersih.kotaku.admin.ui.order;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.bersih.kotaku.admin.firebase.model.Notification;
import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.bersih.kotaku.admin.firebase.model.User;
import com.bersih.kotaku.admin.firebase.service.SubscriptionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class OrderViewModel extends ViewModel {
    private final SubscriptionService subscriptionService;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject public OrderViewModel(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    public LiveData<List<Subscription>> listenPendingSubs() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        Observable<List<Subscription>> observable = this.subscriptionService.listenCurrentPendingSubs()
                .subscribeOn(Schedulers.io());
        return LiveDataReactiveStreams.fromPublisher(observable.toFlowable(BackpressureStrategy.LATEST));
    }

    public void accept(Subscription subscription) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        disposable.add(
                subscriptionService.acceptSubs(subscription).observeOn(Schedulers.io())
                        .concatMap(v -> {
                            if (v) {
                                Notification notification = new Notification();
                                notification.createdAt = Instant.now().toEpochMilli();
                                notification.isRead = false;
                                notification.userID = subscription.userID;
                                notification.payload = subscription.id;
                                notification.type = "SubsAccept";
                                notification.title = "Paket sampah berhasil dipesan";
                                return subscriptionService.insertNotification(notification);
                            } else {
                                return Observable.error(new Throwable("failed to accept subscription"));
                            }
                        }).onErrorComplete().subscribe()
        );
    }

    public void reject(Subscription subscription) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Objects.requireNonNull(firebaseUser);

        disposable.add(
                subscriptionService.rejectSubs(subscription).observeOn(Schedulers.io())
                        .concatMap(v -> {
                            if (v) {
                                Notification notification = new Notification();
                                notification.createdAt = Instant.now().toEpochMilli();
                                notification.isRead = false;
                                notification.userID = subscription.userID;
                                notification.payload = subscription.id;
                                notification.type = "SubsReject";
                                notification.title = "Paket sampah gagal dipesan";
                                return subscriptionService.insertNotification(notification);
                            } else {
                                return Observable.error(new Throwable("failed to reject subscription"));
                            }
                        }).onErrorComplete().subscribe()
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
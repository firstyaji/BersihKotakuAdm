package com.bersih.kotaku.admin.firebase.service;

import com.bersih.kotaku.admin.firebase.model.Notification;
import com.bersih.kotaku.admin.firebase.model.Picker;
import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.bersih.kotaku.admin.firebase.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import timber.log.Timber;

public class SubscriptionService {
    private final FirebaseFirestore firestore;

    @Inject public SubscriptionService(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public Observable<List<Subscription>> listenCurrentActiveSubs() {
        // 1664341477704
        // 1664341319429
        // 1663709983022
        long currentTime = Instant.now().toEpochMilli();
        return Observable.create(emitter -> {
            ListenerRegistration listener = this.firestore.collection("subscriptions")
                    .whereGreaterThanOrEqualTo("validUntil", currentTime)
                    // .whereEqualTo("status", "Accepted")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            try {
                                List<Subscription> subscriptionList = new ArrayList<>();
                                for (DocumentSnapshot snapshot : value.getDocuments()) {
                                    if (snapshot.exists()) {
                                        Subscription subscription = Objects.requireNonNull(snapshot.toObject(Subscription.class))
                                                .withId(snapshot.getId());
                                        if (subscription.status.equals("Accepted")) {
                                            subscriptionList.add(subscription);
                                        }
                                    }
                                }
                                emitter.onNext(subscriptionList);
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        }
                    });
            emitter.setCancellable(listener::remove);
        });
    }

    public Observable<List<Subscription>> listenCurrentPendingSubs() {
        return Observable.create(emitter -> {
            ListenerRegistration listener = this.firestore.collection("subscriptions")
                    // .whereEqualTo("status", "Pending") not trigger update if not Pending anymore
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            try {
                                List<Subscription> subscriptionList = new ArrayList<>();
                                for (DocumentSnapshot snapshot : value.getDocuments()) {
                                    if (snapshot.exists()) {
                                        Subscription subscription = Objects.requireNonNull(snapshot.toObject(Subscription.class))
                                                .withId(snapshot.getId());
                                        // we check here cause the query not working
                                        if (subscription.status.equals("Pending")) {
                                            subscriptionList.add(subscription);
                                        }
                                    }
                                }
                                emitter.onNext(subscriptionList);
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        }
                    });
            emitter.setCancellable(listener::remove);
        });
    }

    public Observable<Boolean> acceptSubs(Subscription subscription) {
        subscription.status = "Accepted";
        return Observable.create(emitter -> {
            this.firestore.collection("subscriptions")
                    .document(subscription.id)
                    .set(subscription).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onNext(true);
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });
    }

    public Observable<Boolean> rejectSubs(Subscription subscription) {
        subscription.status = "Rejected";
        return Observable.create(emitter -> {
            this.firestore.collection("subscriptions")
                    .document(subscription.id)
                    .set(subscription).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onNext(true);
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });
    }

    public Observable<Optional<Subscription>> listenSubs(String subs) {
        return Observable.create(emitter -> {
            ListenerRegistration listener = this.firestore.collection("subscriptions")
                    .document(subs).addSnapshotListener((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (value != null && value.exists()) {
                            Subscription subscription = value.toObject(Subscription.class);
                            if (subscription != null) {
                                subscription.withId(value.getId());
                                emitter.onNext(Optional.of(subscription));
                            }
                        } else {
                            emitter.onNext(Optional.empty());
                        }
                    });
            emitter.setCancellable(listener::remove);
        });
    }

    public Observable<Optional<String>> insertPicker(Picker picker) {
        return Observable.create(emitter -> {
            this.firestore.collection("picker").add(picker).addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    emitter.onNext(Optional.of(task.getResult().getId()));
                } else {
                    emitter.onError(task.getException());
                }
            });
        });
    }

    public Observable<Boolean> updateLastPicker(Subscription subscription) {
        return Observable.create(emitter -> {
            this.firestore.collection("subscriptions")
                    .document(subscription.id)
                    .update("lastPicker", subscription.lastPicker).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onNext(true);
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });
    }

    public Observable<List<Picker>> listenPicker(String subscriptionID) {
        return Observable.create(emitter -> {
            ListenerRegistration listener = this.firestore.collection("picker")
                    .whereEqualTo("subscriptionID", subscriptionID)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .addSnapshotListener(((value, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            try {
                                List<Picker> pickerList = new ArrayList<>();
                                for (DocumentSnapshot snapshot : value.getDocuments()) {
                                    if (snapshot.exists()) {
                                        Picker pick = Objects.requireNonNull(snapshot.toObject(Picker.class))
                                                .withId(snapshot.getId());
                                        pickerList.add(pick);
                                    }
                                }
                                emitter.onNext(pickerList);
                            } catch (Exception e) {
                                emitter.onError(e);
                            }
                        }
                    }));
            emitter.setCancellable(listener::remove);
        });
    }

    public Observable<Optional<User>> listenUser(String id) {
        return Observable.create(emitter -> {
            this.firestore.collection("users").document(id)
                    .addSnapshotListener( (task, error) -> {
                        if (error != null) {
                            emitter.onError(error);
                            return;
                        }

                        if (task != null && task.exists()) {
                            User model = task.toObject(User.class);
                            if (model != null) {
                                model.withId(task.getId());
                                emitter.onNext(Optional.of(model));
                            }
                        } else {
                            emitter.onNext(Optional.empty());
                        }
                    });
        });
    }

    public Observable<Boolean> insertNotification(Notification notification) {
        return Observable.create(emitter -> {
            this.firestore.collection("users").document(notification.userID)
                    .collection("notification").add(notification).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onNext(true);
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });
    }
}

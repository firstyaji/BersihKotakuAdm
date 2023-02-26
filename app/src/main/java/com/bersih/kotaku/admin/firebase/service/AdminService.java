package com.bersih.kotaku.admin.firebase.service;

import com.bersih.kotaku.admin.firebase.model.Admin;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Optional;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

public class AdminService {
    private final FirebaseFirestore firestore;

    @Inject public AdminService(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public Observable<Optional<Admin>> getAdmin(String firebaseID) {
        return Observable.create(emitter -> {
            this.firestore.collection("admin").document(firebaseID)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (!task.getResult().exists()) {
                                emitter.onNext(Optional.empty());
                                return;
                            }
                            Admin model = task.getResult().toObject(Admin.class);
                            if (model != null) {
                                model.withId(task.getResult().getId());
                                emitter.onNext(Optional.of(model));
                            }
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }
}

package com.bersih.kotaku.admin.hilt;

import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public final class HiltModule {

    @Provides
    public FirebaseFirestore provideFirestore() {
        return FirebaseFirestore.getInstance();
    }
}

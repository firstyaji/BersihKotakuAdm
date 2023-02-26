package com.bersih.kotaku.admin.epoxy.model;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bersih.kotaku.admin.R;

public class Konfir_no_data extends EpoxyModelWithHolder<Konfir_no_data.Holder> {

    @Override
    protected Konfir_no_data.Holder createNewHolder(@NonNull ViewParent parent) {
        return new Holder();
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.view_konfir_nodata;
    }


    public static class Holder extends EpoxyHolder {

        @Override
        protected void bindView(@NonNull View itemView) {

        }
    }
}

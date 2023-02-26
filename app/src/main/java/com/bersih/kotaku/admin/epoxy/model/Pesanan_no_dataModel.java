package com.bersih.kotaku.admin.epoxy.model;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bersih.kotaku.admin.R;

public class Pesanan_no_dataModel extends EpoxyModelWithHolder<Pesanan_no_dataModel.Holder> {

    @Override
    protected Pesanan_no_dataModel.Holder createNewHolder(@NonNull ViewParent parent) {
        return new Pesanan_no_dataModel.Holder();
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.view_listpes_nodata;
    }


    public static class Holder extends EpoxyHolder {

        @Override
        protected void bindView(@NonNull View itemView) {

        }
    }
}

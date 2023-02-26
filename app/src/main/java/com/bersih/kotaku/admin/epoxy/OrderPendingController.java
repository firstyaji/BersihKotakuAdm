package com.bersih.kotaku.admin.epoxy;

import com.airbnb.epoxy.AsyncEpoxyController;
import com.bersih.kotaku.admin.epoxy.model.Header2Model;
import com.bersih.kotaku.admin.epoxy.model.HeaderModel;
import com.bersih.kotaku.admin.epoxy.model.Konfir_no_data;
import com.bersih.kotaku.admin.epoxy.model.SubPendingModel;
import com.bersih.kotaku.admin.firebase.model.Subscription;

import java.util.ArrayList;
import java.util.List;

public class OrderPendingController extends AsyncEpoxyController {
    private SubPendingModel.OnClickListener onClickListener;
    private List<Subscription> subscriptionList = new ArrayList<>();

    public void setOnClickListener(SubPendingModel.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        requestModelBuild();
    }

    public void setSubscriptionList(List<Subscription> subscriptionList) {
        this.subscriptionList = subscriptionList;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        new Header2Model("Konfirmasi Pesanan", null).id("Pending Order".hashCode()).addTo(this);

        if (subscriptionList.size() == 0) {
            new Konfir_no_data().id("Control_konfir_no_data".hashCode()).addTo(this);
        }

        for (Subscription subscription: subscriptionList) {
            new SubPendingModel(subscription, onClickListener).id(subscription.hashCode()).addTo(this);
        }
    }
}

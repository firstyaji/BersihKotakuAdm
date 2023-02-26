package com.bersih.kotaku.admin.epoxy;

import com.airbnb.epoxy.AsyncEpoxyController;
import com.bersih.kotaku.admin.epoxy.model.Header1Model;
import com.bersih.kotaku.admin.epoxy.model.HeaderModel;
import com.bersih.kotaku.admin.epoxy.model.Pesanan_no_dataModel;
import com.bersih.kotaku.admin.epoxy.model.SubModel;
import com.bersih.kotaku.admin.firebase.model.Subscription;

import java.util.ArrayList;
import java.util.List;

public class HomeController extends AsyncEpoxyController {
    private SubModel.OnClickListener subOnClickListener;
    private List<Subscription> subscriptionList = new ArrayList<>();

    public void setSubOnClickListener(SubModel.OnClickListener subOnClickListener) {
        this.subOnClickListener = subOnClickListener;
        requestModelBuild();
    }

    public void setSubscriptionList(List<Subscription> subscriptionList) {
        this.subscriptionList = subscriptionList;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        new Header1Model("Daftar Pesanan", null).id("Order_header".hashCode()).addTo(this);

        if (subscriptionList.size() == 0) {
            new Pesanan_no_dataModel().id("Controller_pesanan_noData".hashCode()).addTo(this);
        }

        for (Subscription subscription: subscriptionList) {
            new SubModel(subscription, subOnClickListener, false).id(subscription.hashCode()).addTo(this);
        }
    }
}

package com.bersih.kotaku.admin.epoxy;

import com.airbnb.epoxy.AsyncEpoxyController;
import com.bersih.kotaku.admin.epoxy.model.HeaderModel;
import com.bersih.kotaku.admin.epoxy.model.PickerModel;
import com.bersih.kotaku.admin.epoxy.model.SubModel;
import com.bersih.kotaku.admin.firebase.model.Picker;
import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.bersih.kotaku.admin.firebase.model.User;

import java.util.ArrayList;
import java.util.List;

public class PickerController extends AsyncEpoxyController {
    private User user;
    private Subscription subscription;
    private List<Picker> pickerList = new ArrayList<>();
    private HeaderModel.OnClickListener headerListener;
    private SubModel.OnClickListener subListener = null;

    public void setUser(User user) {
        this.user = user;
        requestModelBuild();
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
        requestModelBuild();
    }

    public void setPickerList(List<Picker> pickerList) {
        this.pickerList = pickerList;
        requestModelBuild();
    }

    public void setHeaderListener(HeaderModel.OnClickListener headerListener) {
        this.headerListener = headerListener;
        requestModelBuild();
    }

    public void setSubListener(SubModel.OnClickListener subListener) {
        this.subListener = subListener;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {
        new HeaderModel("Status Pesanan", headerListener).id("Order details".hashCode()).addTo(this);

        if (subscription != null) {
            new SubModel(subscription, subListener, true).id(subscription.id.hashCode()).addTo(this);
        }
        for (Picker picker: pickerList) {
            new PickerModel(picker).id(picker.id.hashCode()).addTo(this);
        }
    }
}

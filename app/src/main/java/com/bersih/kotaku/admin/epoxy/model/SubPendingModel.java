package com.bersih.kotaku.admin.epoxy.model;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bersih.kotaku.admin.R;
import com.bersih.kotaku.admin.databinding.ViewSubsItemPendingBinding;
import com.bersih.kotaku.admin.firebase.model.Subscription;
import com.bersih.kotaku.admin.utils.GlideFirebaseStorage;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SubPendingModel extends EpoxyModelWithHolder<SubPendingModel.Holder> {
    private final Subscription subscription;
    private final OnClickListener onClickListener;

    public SubPendingModel(Subscription subscription, OnClickListener onClickListener) {
        this.subscription = subscription;
        this.onClickListener = onClickListener;
    }

    @Override
    protected Holder createNewHolder(@NonNull ViewParent parent) {
        return new Holder().bind(subscription, onClickListener);
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);
        holder.bind(subscription, onClickListener);
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.view_subs_item_pending;
    }

    public static class Holder extends EpoxyHolder {
        private ViewSubsItemPendingBinding viewBinding;
        private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMM yyyy")
                .withZone(ZoneId.systemDefault());

        @Override
        protected void bindView(@NonNull View itemView) {
            viewBinding = ViewSubsItemPendingBinding.bind(itemView);
        }

        public Holder bind(Subscription subscription, OnClickListener onClickListener) {
            if (viewBinding == null) return this;
            viewBinding.subsPendingHeader.subsName.setText(subscription.fullName);
            viewBinding.subsPendingHeader.subsPackName.setText(subscription.packName);
            viewBinding.subsPendingHeader.subsLocation.setText(subscription.addressName);

            Instant instant = Instant.ofEpochMilli(subscription.registerAt);
            viewBinding.subsPendingHeader.subsTime.setText(formatterDate.format(instant));
            GlideFirebaseStorage.smartLoad(viewBinding.getRoot(), subscription.paymentReceipt, viewBinding.subsPendingImage);
            viewBinding.subsPendingButton.setOnClickListener(v -> onClickListener.onClick(subscription));
            viewBinding.subsPendingButtonReject.setOnClickListener(v -> onClickListener.onReject(subscription));
            return this;
        }
    }

    public interface OnClickListener {
        void onClick(Subscription subscription);
        void onReject(Subscription subscription);
    }
}

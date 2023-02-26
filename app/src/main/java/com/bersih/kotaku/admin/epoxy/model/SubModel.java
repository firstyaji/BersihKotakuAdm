package com.bersih.kotaku.admin.epoxy.model;

import android.graphics.Color;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bersih.kotaku.admin.R;
import com.bersih.kotaku.admin.databinding.ViewSubsItemBinding;
import com.bersih.kotaku.admin.firebase.model.Subscription;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class SubModel extends EpoxyModelWithHolder<SubModel.Holder> {
    private final Subscription subscription;
    private final OnClickListener onClickListener;
    private final boolean isMaps;

    public SubModel(Subscription subscription, OnClickListener onClickListener, boolean isMaps) {
        this.subscription = subscription;
        this.onClickListener = onClickListener;
        this.isMaps = isMaps;
    }

    @Override
    protected Holder createNewHolder(@NonNull ViewParent parent) {
        return new Holder().bind(subscription, onClickListener, isMaps);
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);
        holder.bind(subscription, onClickListener, isMaps);
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.view_subs_item;
    }


    public static class Holder extends EpoxyHolder {
        private ViewSubsItemBinding viewBinding;
        private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMM yyyy")
                .withZone(ZoneId.systemDefault());
        private final Instant now = Instant.now();
        private final ZonedDateTime zdt = ZonedDateTime.ofInstant( now , ZoneId.systemDefault() );
        long beforeDays = zdt.minusDays(2).toInstant().toEpochMilli();

        @Override
        protected void bindView(@NonNull View itemView) {
            viewBinding = ViewSubsItemBinding.bind(itemView);
        }

        public Holder bind(Subscription subscription, OnClickListener onClickListener, boolean isMaps) {
            if (viewBinding == null) return this;
            viewBinding.subsHeader.subsName.setText(subscription.fullName);
            Instant instant = Instant.ofEpochMilli(subscription.registerAt);
            viewBinding.subsHeader.subsTime.setText(formatterDate.format(instant));
            viewBinding.subsHeader.subsLocation.setText(subscription.addressName);
            viewBinding.subsHeader.subsPackName.setText(subscription.packName);
            if (onClickListener != null) {
                viewBinding.subsButton.setVisibility(View.VISIBLE);
                viewBinding.subsButton.setOnClickListener(v -> onClickListener.onClick(subscription));
            } else {
                viewBinding.subsButton.setVisibility(View.INVISIBLE);
                viewBinding.subsButton.setOnClickListener(null);
            }
            if (isMaps) {
                viewBinding.subsButton.setText(R.string.open_maps);
                viewBinding.subsButton.setBackgroundColor(Color.GREEN);
            } else {
                viewBinding.subsButton.setText(R.string.pick_trash);
                if (subscription.lastPicker > beforeDays) {
                    viewBinding.subsButton.setText("Selesai Angkut");
                    viewBinding.subsButton.setBackgroundColor(Color.BLUE);
                } else {
                    viewBinding.subsButton.setText("Angkut Sampah");
                    viewBinding.subsButton.setBackgroundColor(Color.GREEN);
                }
            }
            return this;
        }
    }

    public interface OnClickListener {
        void onClick(Subscription subscription);
    }
}

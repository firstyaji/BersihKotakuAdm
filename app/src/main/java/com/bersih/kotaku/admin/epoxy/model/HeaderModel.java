package com.bersih.kotaku.admin.epoxy.model;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bersih.kotaku.admin.R;
import com.bersih.kotaku.admin.databinding.ViewHeaderBinding;

public class HeaderModel extends EpoxyModelWithHolder<HeaderModel.Holder> {
    private final String title;
    private final OnClickListener onClickListener;

    public HeaderModel(String title, OnClickListener onClickListener) {
        this.title = title;
        this.onClickListener = onClickListener;
    }

    @Override
    protected Holder createNewHolder(@NonNull ViewParent parent) {
        return new Holder().bind(title, onClickListener);
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);
        holder.bind(title, onClickListener);
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.view_header;
    }

    public static class Holder extends EpoxyHolder {
        private ViewHeaderBinding viewBinding;

        @Override
        protected void bindView(@NonNull View itemView) {
            viewBinding = ViewHeaderBinding.bind(itemView);
        }

        public Holder bind(String title, OnClickListener onClickListener) {
            if (viewBinding == null) return this;
            if (onClickListener != null) {
                viewBinding.headerNavigationButton.setVisibility(View.VISIBLE);
                viewBinding.headerNavigationButton.setOnClickListener(v -> onClickListener.onClick());
            } else {
                viewBinding.headerNavigationButton.setVisibility(View.INVISIBLE);
                viewBinding.headerNavigationButton.setOnClickListener(null);
            }
            viewBinding.headerTitle.setText(title);
            return this;
        }
    }

    public interface OnClickListener {
        void onClick();
    }
}

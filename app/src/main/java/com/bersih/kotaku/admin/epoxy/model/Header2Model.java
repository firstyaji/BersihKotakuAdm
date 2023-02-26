package com.bersih.kotaku.admin.epoxy.model;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bersih.kotaku.admin.R;
import com.bersih.kotaku.admin.databinding.ViewHeader1Binding;

public class Header2Model extends EpoxyModelWithHolder<Header2Model.Holder> {
    private final String title;
    private final OnClickListener onClickListener;

    public Header2Model(String title, OnClickListener onClickListener) {
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
        return R.layout.view_header2;
    }

    public static class Holder extends EpoxyHolder {
        private ViewHeader1Binding viewBinding;

        @Override
        protected void bindView(@NonNull View itemView) {
            viewBinding = ViewHeader1Binding.bind(itemView);
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
            viewBinding.headerTitlehome.setText(title);
            return this;
        }
    }

    public interface OnClickListener {
        void onClick();
    }
}

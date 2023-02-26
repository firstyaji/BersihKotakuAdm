package com.bersih.kotaku.admin.epoxy.model;

import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;

import com.airbnb.epoxy.EpoxyHolder;
import com.airbnb.epoxy.EpoxyModelWithHolder;
import com.bersih.kotaku.admin.R;
import com.bersih.kotaku.admin.databinding.ViewPickerItemBinding;
import com.bersih.kotaku.admin.firebase.model.Picker;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PickerModel extends EpoxyModelWithHolder<PickerModel.Holder> {
    private final Picker picker;

    public PickerModel(Picker picker) {
        this.picker = picker;
    }

    @Override
    protected Holder createNewHolder(@NonNull ViewParent parent) {
        return new Holder().bind(picker);
    }

    @Override
    public void bind(@NonNull Holder holder) {
        super.bind(holder);
        holder.bind(picker);
    }

    @Override
    protected int getDefaultLayout() {
        return R.layout.view_picker_item;
    }

    public static class Holder extends EpoxyHolder {
        private ViewPickerItemBinding viewBinding;
        private final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd MMM yyyy")
                .withZone(ZoneId.systemDefault());
        private final DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("hh:mm a")
                .withZone(ZoneId.systemDefault());

        @Override
        protected void bindView(@NonNull View itemView) {
            viewBinding = ViewPickerItemBinding.bind(itemView);
        }

        public Holder bind(Picker picker) {
            if (viewBinding == null) return this;
            viewBinding.pickerTitle.setText(picker.title);
            Instant instant = Instant.ofEpochMilli(picker.createdAt);
            viewBinding.pickerDate.setText(formatterDate.format(instant));
            viewBinding.pickerTime.setText(formatterTime.format(instant));
            return this;
        }
    }
}

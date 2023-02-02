package com.openclassrooms.savemytrip.todolist;

import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.savemytrip.R;
import com.openclassrooms.savemytrip.databinding.ActivityTodoListBinding;
import com.openclassrooms.savemytrip.databinding.ActivityTodoListItemBinding;
import com.openclassrooms.savemytrip.models.Item;

import java.lang.ref.WeakReference;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ActivityTodoListItemBinding binding;

    // FOR DATA
    private WeakReference<ItemAdapter.Listener> callbackWeakRef;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        // ----- /!\_/!\ --> déclaration du binding ! ----------
        binding = ActivityTodoListItemBinding.bind(itemView);
    }


    public void updateWithItem(Item item, ItemAdapter.Listener callback) {
        this.callbackWeakRef = new WeakReference<ItemAdapter.Listener>(callback);
        this.binding.activityTodoListItemText.setText(item.getText());
        this.binding.activityTodoListItemRemove.setOnClickListener(this);

        switch (item.getCategory()) {
            case 0: // TO VISIT
                binding.activityTodoListItemImage.setBackgroundResource(R.drawable.ic_room_black_24px);
                break;
            case 1: // IDEAS
                binding.activityTodoListItemImage.setBackgroundResource(R.drawable.ic_lightbulb_outline_black_24px);
                break;
            case 2: // RESTAURANTS
                binding.activityTodoListItemImage.setBackgroundResource(R.drawable.ic_local_cafe_black_24px);
                break;
        }

        if (item.getSelected()) {
            binding.activityTodoListItemText.setPaintFlags(binding.activityTodoListItemText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.activityTodoListItemText.setPaintFlags(binding.activityTodoListItemText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public void onClick(View view) {
        ItemAdapter.Listener callback = callbackWeakRef.get();
        if (callback != null) callback.onClickDeleteButton(getAdapterPosition());
    }
}
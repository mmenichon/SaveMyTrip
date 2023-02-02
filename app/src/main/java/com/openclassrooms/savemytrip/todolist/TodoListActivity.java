package com.openclassrooms.savemytrip.todolist;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.savemytrip.R;
import com.openclassrooms.savemytrip.databinding.ActivityTodoListBinding;
import com.openclassrooms.savemytrip.injections.Injection;
import com.openclassrooms.savemytrip.injections.ViewModelFactory;
import com.openclassrooms.savemytrip.models.Item;
import com.openclassrooms.savemytrip.models.User;
import com.openclassrooms.savemytrip.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public class TodoListActivity extends AppCompatActivity implements ItemAdapter.Listener {

    private ActivityTodoListBinding binding;

    private ItemViewModel itemViewModel;
    private ItemAdapter adapter;
    private List<Item> items;

    private static final int USER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTodoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.configureSpinner();
        this.configureRecyclerView();
        this.configureViewModel();

        this.getCurrentUser(USER_ID);
        this.getItem(USER_ID);

        binding.todoListActivityButtonAdd.setOnClickListener(view -> {
            createItem();
        });
    }


    // -------------------
    // ACTIONS
    // -------------------
    public void onClickAddButton() {
        binding.todoListActivityButtonAdd.setOnClickListener(view -> {
            createItem();
        });
    }

    @Override
    public void onClickDeleteButton(int position) {
        this.deleteItem(this.adapter.getItem(position));
    }

    // -------------------
    // DATA
    // -------------------
    private void configureViewModel() {
        ViewModelFactory factory = Injection.provideViewModelFactory(this);
        this.itemViewModel = ViewModelProviders.of(this, factory).get(ItemViewModel.class);
        this.itemViewModel.init(USER_ID);
    }

    private void getCurrentUser(int userId) {
        this.itemViewModel.getUser(userId).observe(this, this::updateHeader);
    }

    private void getItem(int userId) {
        this.itemViewModel.getItems(userId).observe(this, this::updateItemsList);
    }

    // 3 - Create a new item
    private void createItem() {
        Item item = new Item(this.binding.todoListActivityEditText.getText().toString(),
                this.binding.todoListActivitySpinner.getSelectedItemPosition(), USER_ID);
        binding.todoListActivityEditText.setText("");
        itemViewModel.createItem(item);
    }

    // 3 - Delete an item
    private void deleteItem(Item item) {
        this.itemViewModel.deleteItem(item.getId());
    }

    // 3 - Update an item (selected or not)
    private void updateItem(Item item) {
        item.setSelected(!item.getSelected());
        this.itemViewModel.updateItem(item);
    }

    // -------------------
    // UI
    // -------------------
    private void configureSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.todoListActivitySpinner.setAdapter(adapter);
    }

    // 4 - Configure RecyclerView
    private void configureRecyclerView() {
        this.adapter = new ItemAdapter(this);
        binding.todoListActivityRecyclerView.setAdapter(this.adapter);
        binding.todoListActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ItemClickSupport.addTo(binding.todoListActivityRecyclerView, R.layout.activity_todo_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> this.updateItem(this.adapter.getItem(position)));
    }

    // 5 - Update view (username & picture)
    private void updateHeader(User user) {
        this.binding.todoListActivityHeaderProfileText.setText(user.getUsername());
        Glide.with(this).load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(this.binding.todoListActivityHeaderProfileImage);
    }

    // 6 - Update the list of items
    private void updateItemsList(List<Item> items) {
        this.adapter.updateData(items);
    }

}

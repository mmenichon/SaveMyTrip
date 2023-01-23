package com.openclassrooms.savemytrip.todolist;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.openclassrooms.savemytrip.R;
import com.openclassrooms.savemytrip.databinding.ActivityTodoListBinding;
import com.openclassrooms.savemytrip.injections.ViewModelFactory;
import com.openclassrooms.savemytrip.models.Item;
import com.openclassrooms.savemytrip.models.User;

import java.util.List;

public class TodoListActivity extends AppCompatActivity {

    private ActivityTodoListBinding binding;

    private ItemViewModel itemViewModel;
    private ItemAdapter adapter;
    private static final int USER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTodoListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configureViewModel();
        initView();

        getCurrentUser();
        getItem();
    }

//    @Override
//    public void onClickDeleteButton(Item item) {
//        deleteItem(item);
//    }
//
//    @Override
//    public void onItemClick(Item item) {
//        updateItem(item);
//    }

    // -------------------
    // DATA
    // -------------------
    private void configureViewModel() {
        this.itemViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ItemViewModel.class);
        this.itemViewModel.init(USER_ID);
    }

    private void getCurrentUser() {
        LiveData<User> userLiveData = itemViewModel.getUser();
        if (userLiveData != null) {
            userLiveData.observe(this, this::updateView);
        }
    }

    private void getItem() {
        this.itemViewModel.getItems(USER_ID).observe(this, this::updateItemsList);
    }

    // 3 - Create a new item
    private void createItem() {
        itemViewModel.createItem(binding.todoListActivityEditText.getText().toString(), binding.todoListActivitySpinner.getSelectedItemPosition(), USER_ID);
        binding.todoListActivityEditText.setText("");
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

    private void initView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        configureSpinner();
        binding.todoListActivityButtonAdd.setOnClickListener(view -> {
            createItem();
        });
        configureRecyclerView();
    }

    private void configureSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.todoListActivitySpinner.setAdapter(adapter);
    }

    // 4 - Configure RecyclerView
    private void configureRecyclerView() {
        this.adapter = new ItemAdapter((ItemAdapter.Listener) this);
        binding.todoListActivityRecyclerView.setAdapter(this.adapter);
        binding.todoListActivityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // 5 - Update view (username & picture)
    private void updateView(User user) {
        if (user == null) return;
        binding.todoListActivityHeaderProfileText.setText(user.getUsername());
        Glide.with(this).load(user.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(binding.todoListActivityHeaderProfileImage);
    }

    // 6 - Update the list of items
    private void updateItemsList(List<Item> items) {
        this.adapter.updateData(items);
    }
}

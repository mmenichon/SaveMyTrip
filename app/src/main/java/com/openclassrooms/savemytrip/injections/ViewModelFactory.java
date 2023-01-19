package com.openclassrooms.savemytrip.injections;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.savemytrip.database.dao.SaveMyTripDatabase;
import com.openclassrooms.savemytrip.repositories.ItemDataRepository;
import com.openclassrooms.savemytrip.repositories.UserDataRepository;
import com.openclassrooms.savemytrip.todolist.ItemViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewModelFactory  implements ViewModelProvider.Factory {

    private final ItemDataRepository itemDataSource;

    private final UserDataRepository userDataSource;

    private final Executor executor;

    private static ViewModelFactory factory;

    public ViewModelFactory(Context context) {
        SaveMyTripDatabase database = SaveMyTripDatabase.getInstance(context);
        this.itemDataSource = new ItemDataRepository(database.itemDao());
        this.userDataSource = new UserDataRepository(database.userDao());
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static ViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(context);
                }
            }
        }
        return factory;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ItemViewModel.class)) {
            return (T) new ItemViewModel(itemDataSource, userDataSource, executor);
        }
        throw new IllegalArgumentException("ViewModel inconnu");
    }
}

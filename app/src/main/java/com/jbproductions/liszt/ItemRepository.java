package com.jbproductions.liszt;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Repository class to manage the ViewModel's access to data.
 * Currently, this simply routes data provided by the ItemDao. In a future implementation, this
 * class will handle deciding whether to fetch online data or rely on data cached in local storage.
 */
public class ItemRepository {

    private ItemDao mItemDao;
    private LiveData<List<Item>> mListItems;

    ItemRepository(Application application) {
        ItemRoomDatabase db = ItemRoomDatabase.getDatabase(application);
        mItemDao = db.itemDao();
        mListItems = mItemDao.getAllItems();
    }

    LiveData<List<Item>> getListItems() {
        return mListItems;
    }

    void insert(Item item) {
        ItemRoomDatabase.databaseWriteExecutor.execute(() -> {
            mItemDao.insert(item);
        });
    }

    void delete(Item item) {
        ItemRoomDatabase.databaseWriteExecutor.execute(() -> {
            mItemDao.deleteItem(item);
        });
    }
}

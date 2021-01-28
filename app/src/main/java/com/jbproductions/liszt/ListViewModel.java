package com.jbproductions.liszt;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ListViewModel extends AndroidViewModel {

    private ItemRepository itemRepository;

    private final LiveData<List<Item>> itemList;

    public ListViewModel (Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
        itemList = itemRepository.getListItems();
    }

    LiveData<List<Item>> getItemList() { return itemList; }

    // Create wrapper methods so that the implementation is segmented from the UI
    public void insert(Item item) { itemRepository.insert(item); }
    public void delete(Item item) { itemRepository.delete(item); }
}

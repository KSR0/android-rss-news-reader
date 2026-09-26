package etu.ecole.fluxrssmobile;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class RssFeedViewModel extends ViewModel {
    private List<ItemModel> items;
    private int currentItemIndex = 0;
    private final MutableLiveData<ItemModel> currentItemLiveData = new MutableLiveData<>();


    public RssFeedViewModel() {
        this.items = new ArrayList<>();
    }

    public void previousItem() {
        currentItemIndex = (currentItemIndex - 1 + items.size()) % items.size();
        currentItemLiveData.setValue(items.get(currentItemIndex));
    }

    public void nextItem() {
        currentItemIndex = (currentItemIndex + 1 + items.size()) % items.size();
        currentItemLiveData.setValue(items.get(currentItemIndex));
    }

    public void addItem(ItemModel item) {
        items.add(item);
        if (items.size() == 1) {
            currentItemLiveData.postValue(item);
        }
    }

    public MutableLiveData<ItemModel> getCurrentItemLiveData() {
        return currentItemLiveData;
    }

    public void clearOldItems() {
        this.items = new ArrayList<>();
    }
}

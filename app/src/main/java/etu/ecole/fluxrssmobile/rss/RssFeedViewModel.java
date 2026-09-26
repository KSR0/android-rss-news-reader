package etu.ecole.fluxrssmobile.rss;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import etu.ecole.fluxrssmobile.BitmapUtility;
import etu.ecole.fluxrssmobile.database.RssItemEntity;

public class RssFeedViewModel extends ViewModel {
    private List<RssItemModel> items;
    private int currentItemIndex = 0;
    private final MutableLiveData<RssItemModel> currentItemLiveData = new MutableLiveData<>();


    public RssFeedViewModel() {
        this.items = new ArrayList<>();
    }

    public void previousItem() {
        if (items.isEmpty()) return;
        currentItemIndex = (currentItemIndex - 1 + items.size()) % items.size();
        currentItemLiveData.setValue(items.get(currentItemIndex));
    }

    public void nextItem() {
        if (items.isEmpty()) return;
        currentItemIndex = (currentItemIndex + 1 + items.size()) % items.size();
        currentItemLiveData.setValue(items.get(currentItemIndex));
    }

    public void addItem(RssItemModel item) {
        items.add(item);
        if (items.size() == 1) {
            currentItemLiveData.postValue(item);
        }
    }

    public List<RssItemModel> getItems() {
        return items;
    }

    public MutableLiveData<RssItemModel> getCurrentItemLiveData() {
        return currentItemLiveData;
    }

    public void clearOldItems() {
        this.items = new ArrayList<>();
    }

    public void loadFromCache(List<RssItemEntity> cachedItems) {
        items.clear();
        for (RssItemEntity entity : cachedItems) {
            Bitmap image = BitmapUtility.bytesToBitmap(entity.imageBytes);
            items.add(new RssItemModel(entity.title, entity.date, entity.description, image));
        }
        if (!items.isEmpty()) {
            currentItemIndex = 0;
            currentItemLiveData.postValue(items.get(0));
        }
    }
}

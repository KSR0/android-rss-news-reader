package etu.ecole.fluxrssmobile;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class RSSFeedViewModel extends ViewModel {
    private List<ItemModel> items;
    private ItemModel currentItem;

    public RSSFeedViewModel() {
        this.items = new ArrayList<>();
    }

    public void previousItem() {

    }

    public void nextItem() {

    }

    public void addItem(ItemModel item) {
        if (currentItem == null) {
            currentItem = item;
        }
        items.add(item);
    }

    public List<ItemModel> getItems() {
        return items;
    }
}

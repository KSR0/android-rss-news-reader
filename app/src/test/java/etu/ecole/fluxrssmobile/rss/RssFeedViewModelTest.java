package etu.ecole.fluxrssmobile.rss;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;

import etu.ecole.fluxrssmobile.database.RssItemEntity;

public class RssFeedViewModelTest {
    // Nécessaire car RssFeedViewModel utilise LiveData
    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();


    @Test
    public void loadFromCache_mapsEachFieldToTheRightProperty() {
        RssFeedViewModel viewModel = new RssFeedViewModel();
        RssItemEntity entity = new RssItemEntity("url", "Title", "Sun, 18 Oct 2026", "Description", null);

        viewModel.loadFromCache(Collections.singletonList(entity));

        RssItemModel item = viewModel.getCurrentItemLiveData().getValue();
        assertEquals("Title", item.getTitle());
        assertEquals("Description", item.getDescription());
        assertEquals("Sun, 18 Oct 2026", item.getDate());
    }
}

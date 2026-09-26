package etu.ecole.fluxrssmobile;

import android.content.Context;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import etu.ecole.fluxrssmobile.database.AppDatabase;
import etu.ecole.fluxrssmobile.database.RssItemEntity;
import etu.ecole.fluxrssmobile.rss.RssFeedViewModel;
import etu.ecole.fluxrssmobile.rss.RssItemModel;
import etu.ecole.fluxrssmobile.rss.RssSaxHandler;
import etu.ecole.fluxrssmobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RssSaxHandler rssSaxhandler;
    private ExecutorService executor;
    private RssFeedViewModel viewModel;
    private Map<Integer, String> menuUrls;
    private AppDatabase db;
    private String currentCategoryUrl;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private Map<Integer, String> buildMenuUrls() {
        Map<Integer, String> map = new HashMap<>();
        map.put(R.id.action_news, getString(R.string.rss_url_news));
        map.put(R.id.action_international, getString(R.string.rss_url_international));
        map.put(R.id.action_economy, getString(R.string.rss_url_economy));
        map.put(R.id.action_culture, getString(R.string.rss_url_culture));
        map.put(R.id.action_sport, getString(R.string.rss_url_sport));
        map.put(R.id.action_planets, getString(R.string.rss_url_planets));
        map.put(R.id.action_pixels, getString(R.string.rss_url_pixels));
        map.put(R.id.action_sciences, getString(R.string.rss_url_sciences));
        map.put(R.id.action_opinions, getString(R.string.rss_url_opinions));
        return map;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            viewModel.clearOldItems();
            startRssThread();
            return true;
        }

        String url = menuUrls.get(id);
        if (url != null) {
            currentCategoryUrl = url;
            rssSaxhandler.setUrl(url);
            viewModel.clearOldItems();
            startRssThread();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RssFeedViewModel.class);
        binding.setViewModel(viewModel);

        db = AppDatabase.getInstance(this);

        menuUrls = buildMenuUrls();

        viewModel.getCurrentItemLiveData().observe(this, item -> {
            resetDisplay(item.getTitle(), item.getDescription(), item.getDate(), item.getImage());
        });

        executor = Executors.newSingleThreadExecutor();
        rssSaxhandler = new RssSaxHandler(viewModel);
        currentCategoryUrl = getString(R.string.rss_url_international);
        rssSaxhandler.setUrl(currentCategoryUrl);
        startRssThread();
    }

    private void startRssThread() {
        String categoryUrl = currentCategoryUrl;

        executor.execute(() -> {
            if (NetworkUtility.isNetworkAvailable(getBaseContext())) {
                try {
                    rssSaxhandler.processFeed();

                    // Save to DB when done loading
                    List<RssItemEntity> entities = convertToEntities(categoryUrl, viewModel.getItems());
                    db.itemDao().clearCategory(categoryUrl);
                    db.itemDao().insertAll(entities);
                } catch (Exception e) {
                    Log.e("RSS", "startRssThread exception", e);
                }
            } else {
                List<RssItemEntity> cachedItems = db.itemDao().getItemsForCategory(categoryUrl);
                if (cachedItems.isEmpty()) {
                    runOnUiThread(() ->
                            Toast.makeText(this, R.string.no_cache_available, Toast.LENGTH_LONG).show()
                    );
                } else {
                    viewModel.loadFromCache(cachedItems);
                }
            }

        });
    }

    public void resetDisplay(String title, String description, String date, Bitmap image) {
        binding.imageTitle.setText(title);
        binding.imageDescription.setText(description);
        binding.imageDate.setText(date);
        binding.imageDisplay.setImageBitmap(image);
    }

    private List<RssItemEntity> convertToEntities(String categoryUrl, List<RssItemModel> items) {
        List<RssItemEntity> entities = new ArrayList<>();
        for (RssItemModel item : items) {
            byte[] imageBytes = BitmapUtility.bitmapToBytes(item.getImage());
            entities.add(new RssItemEntity(categoryUrl, item.getTitle(), item.getDate(), item.getDescription(), imageBytes));
        }
        return entities;
    }

}
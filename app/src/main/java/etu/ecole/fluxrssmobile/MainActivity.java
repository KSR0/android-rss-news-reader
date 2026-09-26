package etu.ecole.fluxrssmobile;

import android.graphics.Bitmap;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import etu.ecole.fluxrssmobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RssSaxHandler rssSaxhandler;
    private ExecutorService executor;
    private RssFeedViewModel viewModel;
    private Map<Integer, String> menuUrls;

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

        menuUrls = buildMenuUrls();

        viewModel.getCurrentItemLiveData().observe(this, item -> {
            resetDisplay(item.getTitle(), item.getDescription(), item.getDate(), item.getImage());
        });

        executor = Executors.newSingleThreadExecutor();
        rssSaxhandler = new RssSaxHandler(viewModel);
        rssSaxhandler.setUrl("https://www.lemonde.fr/international/rss_full.xml");
        startRssThread();
    }

    private void startRssThread() {
        executor.execute(() -> {
            try {
                rssSaxhandler.processFeed();
            } catch (Exception e) {
                Log.e("RSS", "startRssThread exception", e);
            }
        });
    }

    public void resetDisplay(String title, String description, String date, Bitmap image) {
        binding.imageTitle.setText(title);
        binding.imageDescription.setText(description);
        binding.imageDate.setText(date);
        binding.imageDisplay.setImageBitmap(image);
    }

}
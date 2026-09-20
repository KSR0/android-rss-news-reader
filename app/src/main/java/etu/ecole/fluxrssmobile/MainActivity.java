package etu.ecole.fluxrssmobile;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import etu.ecole.fluxrssmobile.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RSSFeedViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RSSFeedViewModel rssFeedViewModel = new ViewModelProvider(this).get(RSSFeedViewModel.class);
        binding.setViewModel(viewModel);
        this.viewModel = viewModel;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            MyRSSsaxHandler handler = new MyRSSsaxHandler(rssFeedViewModel);
            handler.setUrl("https://www.lemonde.fr/international/rss_full.xml");
            handler.processFeed();
        });
    }

    public void resetDisplay(String title, String date, Bitmap image, String description) {

    }

}
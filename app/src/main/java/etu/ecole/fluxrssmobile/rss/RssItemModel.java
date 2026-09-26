package etu.ecole.fluxrssmobile.rss;

import android.graphics.Bitmap;

public class RssItemModel {
    private final String title;
    private final String description;
    private final String date;
    private final Bitmap image;

    public RssItemModel(String title, String description, String date, Bitmap image) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public Bitmap getImage() {
        return image;
    }
}

package etu.ecole.fluxrssmobile;

import android.graphics.Bitmap;

public class ItemModel {
    private String title;
    private String description;
    private String date;
    private Bitmap image;

    public ItemModel(String title, String description, String date, Bitmap image) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }
}

package etu.ecole.fluxrssmobile.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rssItems")
public class RssItemEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String categoryUrl;
    public String title;
    public String date;
    public String description;
    public byte[] imageBytes;

    public RssItemEntity(String categoryUrl, String title, String date, String description, byte[] imageBytes) {
        this.categoryUrl = categoryUrl;
        this.title = title;
        this.date = date;
        this.description = description;
        this.imageBytes = imageBytes;
    }
}
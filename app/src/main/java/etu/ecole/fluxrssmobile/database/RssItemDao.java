package etu.ecole.fluxrssmobile.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RssItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<RssItemEntity> items);

    @Query("SELECT * FROM rssItems WHERE categoryUrl = :categoryUrl")
    List<RssItemEntity> getItemsForCategory(String categoryUrl);

    @Query("DELETE FROM rssItems WHERE categoryUrl = :categoryUrl")
    void clearCategory(String categoryUrl);
}
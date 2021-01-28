package com.jbproductions.liszt;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class to represent a SQLite table of list items
 * Currently, each item only contains a String with the item's name
 */
@Entity(tableName = "item_table")
public class Item {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "item")
    private String mItem;

    public Item(@NonNull String item) {this.mItem = item;}

    public String getItem(){ return this.mItem; }
}

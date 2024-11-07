package de.fhkiel.temi.robogguide.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "texts")
public class Texts {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String text;

    @DatabaseField
    private boolean detailed;

   // int users_id;
   @DatabaseField
   private int locations_id;

    @DatabaseField
    private int items_id;

    @DatabaseField
    private int transfers_id;

    public Texts() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isDetailed() {
        return detailed;
    }

    public void setDetailed(boolean detailed) {
        this.detailed = detailed;
    }

    public int getLocations_id() {
        return locations_id;
    }

    public void setLocations_id(int locations_id) {
        this.locations_id = locations_id;
    }

    public int getItems_id() {
        return items_id;
    }

    public void setItems_id(int items_id) {
        this.items_id = items_id;
    }

    public int getTransfers_id() {
        return transfers_id;
    }

    public void setTransfers_id(int transfers_id) {
        this.transfers_id = transfers_id;
    }
}

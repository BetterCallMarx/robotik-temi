package de.fhkiel.temi.robogguide.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "transfers")
public class Transfers {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String title;

    @DatabaseField
    private int location_from;

    @DatabaseField
    private int location_to;
    //int users_id;


    public Transfers() {
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

    public int getLocation_from() {
        return location_from;
    }

    public void setLocation_from(int location_from) {
        this.location_from = location_from;
    }

    public int getLocation_to() {
        return location_to;
    }

    public void setLocation_to(int location_to) {
        this.location_to = location_to;
    }
}

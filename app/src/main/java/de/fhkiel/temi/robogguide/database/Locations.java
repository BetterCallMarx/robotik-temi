package de.fhkiel.temi.robogguide.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "locations")
public class Locations {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private boolean important;

    @DatabaseField
    private int places_id;

    public Locations() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public int getPlaces_id() {
        return places_id;
    }

    public void setPlaces_id(int places_id) {
        this.places_id = places_id;
    }
}

package de.fhkiel.temi.robogguide.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "places")
public class Places {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String name;

    public Places() {
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
}

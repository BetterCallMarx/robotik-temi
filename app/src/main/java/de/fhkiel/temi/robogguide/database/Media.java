package de.fhkiel.temi.robogguide.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "media")
public class Media {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String url;

    @DatabaseField
    private int texts_id;

    public Media() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTexts_id() {
        return texts_id;
    }

    public void setTexts_id(int texts_id) {
        this.texts_id = texts_id;
    }
}

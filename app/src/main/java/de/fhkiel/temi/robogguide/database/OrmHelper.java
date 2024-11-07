package de.fhkiel.temi.robogguide.database;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class OrmHelper extends OrmLiteSqliteOpenHelper{

    private static final String DATABASE_NAME = "roboguide.db";
    private static final int DATABASE_VERSION = 1;


    //Data Access Object for each individual table
    private Dao<Places, Integer> placesDao = null;
    private Dao<Items, Integer> itemsDao = null;
    private Dao<Locations, Integer> locationsDao = null;
    private Dao<Media, Integer> mediaDao = null;
    private Dao<Texts, Integer> textsDao = null;
    private Dao<Transfers, Integer> transfersDao = null;



    public OrmHelper(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int databaseVersion) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            //create Tables on create
            TableUtils.createTable(connectionSource,Places.class);
            TableUtils.createTable(connectionSource,Items.class);
            TableUtils.createTable(connectionSource,Locations.class);
            TableUtils.createTable(connectionSource,Media.class);
            TableUtils.createTable(connectionSource,Texts.class);
            TableUtils.createTable(connectionSource,Transfers.class);

        }catch(SQLException | java.sql.SQLException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {
            TableUtils.dropTable(connectionSource,Places.class,true);
            TableUtils.dropTable(connectionSource,Items.class,true);
            TableUtils.dropTable(connectionSource,Locations.class,true);
            TableUtils.dropTable(connectionSource,Media.class,true);
            TableUtils.dropTable(connectionSource,Texts.class,true);
            TableUtils.dropTable(connectionSource,Transfers.class,true);
            onCreate(database,connectionSource);


        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }

    }


    //getDao Method for each Table
    public Dao<Places, Integer> getPlacesDao() throws SQLException, java.sql.SQLException {
        if(placesDao == null){
            placesDao = getDao(Places.class);
        }
        return placesDao;
    }
    public Dao<Items, Integer> getItemsDao() throws SQLException, java.sql.SQLException {
        if(itemsDao == null){
            itemsDao = getDao(Items.class);
        }
        return itemsDao;
    }
    public Dao<Locations, Integer> getLocationsDao() throws SQLException, java.sql.SQLException {
        if(locationsDao == null){
            locationsDao = getDao(Locations.class);
        }
        return locationsDao;
    }
    public Dao<Media, Integer> getMediaDao() throws SQLException, java.sql.SQLException {
        if(mediaDao == null){
            mediaDao = getDao(Media.class);
        }
        return mediaDao;
    }
    public Dao<Texts, Integer> getTextsDao() throws SQLException, java.sql.SQLException {
        if(textsDao == null){
            textsDao = getDao(Texts.class);
        }
        return textsDao;
    }
    public Dao<Transfers, Integer> getTransfersDao() throws SQLException, java.sql.SQLException {
        if(transfersDao == null){
            transfersDao = getDao(Transfers.class);
        }
        return transfersDao;
    }


    //used to close the database
    @Override
    public void close(){
        super.close();
        placesDao = null;
        itemsDao = null;
        locationsDao = null;
        mediaDao = null;
        textsDao = null;
        transfersDao = null;

    }


}

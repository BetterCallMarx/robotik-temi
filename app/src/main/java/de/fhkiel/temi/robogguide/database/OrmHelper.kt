package de.fhkiel.temi.robogguide.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import java.sql.SQLException

class OrmHelper(context : Context) : OrmLiteSqliteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        private const val DATABASE_NAME = "roboguide.db"
        private const val DATABASE_VERSION = 1
        private const val TAG = "OrmHelper"
    }

    // DAOs for each table
    private var itemsDao: Dao<Items, Int>? = null
    private var locationsDao: Dao<Locations, Int>? = null
    private var mediaDao: Dao<Media, Int>? = null
    private var placesDao: Dao<Places, Int>? = null
    private var textsDao: Dao<Texts, Int>? = null
    private var transfersDao: Dao<Transfers, Int>? = null





    //function called upon creating database
    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {


        try {
            TableUtils.createTableIfNotExists(connectionSource, Items::class.java)
            TableUtils.createTableIfNotExists(connectionSource, Locations::class.java)
            TableUtils.createTableIfNotExists(connectionSource, Media::class.java)
            TableUtils.createTableIfNotExists(connectionSource, Places::class.java)
            TableUtils.createTableIfNotExists(connectionSource, Texts::class.java)
            TableUtils.createTableIfNotExists(connectionSource, Transfers::class.java)
        }catch (e:SQLException){
            e.printStackTrace()
        }

    }



    //called when the database is upgraded
    override fun onUpgrade(
        database: SQLiteDatabase?,
        connectionSource: ConnectionSource?,
        oldVersion: Int,
        newVersion: Int
    ) {
        if(oldVersion<2){
            TableUtils.dropTable<Items,Int>(connectionSource, Items::class.java, true)
            TableUtils.dropTable<Locations,Int>(connectionSource, Locations::class.java, true)
            TableUtils.dropTable<Media,Int>(connectionSource, Media::class.java, true)
            TableUtils.dropTable<Places,Int>(connectionSource, Places::class.java, true)
            TableUtils.dropTable<Texts,Int>(connectionSource, Texts::class.java, true)
            TableUtils.dropTable<Transfers,Int>(connectionSource, Transfers::class.java, true)

        }
    }

    // DAO for each table
    @Throws(SQLException::class)
    fun getItemsDao(): Dao<Items, Int> {
        if (itemsDao == null) {
            itemsDao = getDao(Items::class.java)
        }
        return itemsDao!!
    }

    @Throws(SQLException::class)
    fun getLocationsDao(): Dao<Locations, Int> {
        if (locationsDao == null) {
            locationsDao = getDao(Locations::class.java)
        }
        return locationsDao!!
    }

    @Throws(SQLException::class)
    fun getMediaDao(): Dao<Media, Int> {
        if (mediaDao == null) {
            mediaDao = getDao(Media::class.java)
        }
        return mediaDao!!
    }


    @Throws(SQLException::class)
    fun getPlacesDao(): Dao<Places, Int> {
        if (placesDao == null) {
            placesDao = getDao(Places::class.java)
        }
        return placesDao!!
    }


    @Throws(SQLException::class)
    fun getTextsDao(): Dao<Texts, Int> {
        if (textsDao == null) {
            textsDao = getDao(Texts::class.java)
        }
        return textsDao!!
    }


    @Throws(SQLException::class)
    fun getTransfersDao(): Dao<Transfers, Int> {
        if (transfersDao == null) {
            transfersDao = getDao(Transfers::class.java)
        }
        return transfersDao!!
    }




    //function to close Database
    override fun close() {
        super.close()
        itemsDao = null
        locationsDao = null
        mediaDao = null
        placesDao = null
        textsDao = null
        transfersDao = null
    }

}
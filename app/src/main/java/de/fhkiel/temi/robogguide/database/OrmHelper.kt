package de.fhkiel.temi.robogguide.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.sql.SQLException

private const val DATABASE_NAME = "roboguide.db"
private const val DATABASE_VERSION = 1
private const val TAG = "OrmHelper"

class OrmHelper(val context: Context) :
    OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    // DAOs for each table
    val itemsDao: Dao<Items, Int> = DaoManager.createDao(connectionSource, Items::class.java)
    val locationsDao: Dao<Locations, Int> =
        DaoManager.createDao(connectionSource, Locations::class.java)
    val mediaDao: Dao<Media, Int> = DaoManager.createDao(connectionSource, Media::class.java)
    val placesDao: Dao<Places, Int> = DaoManager.createDao(connectionSource, Places::class.java)
    val textsDao: Dao<Texts, Int> = DaoManager.createDao(connectionSource, Texts::class.java)
    val transfersDao: Dao<Transfers, Int> =
        DaoManager.createDao(connectionSource, Transfers::class.java)

    /**
     * copy Database at init of onject
     */
    init {
        copyDatabase()
    }

    /**
     * Method to copy the Database
     *
     */
    private fun copyDatabase() {

        val inputStream: InputStream = context.assets.open(databaseName)
        val outFile = File("${context.applicationInfo.dataDir}/databases/$databaseName")
        val outputStream: OutputStream = FileOutputStream(outFile)

        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }


    /**
     * function called upon creation of database, mandatory override from inheritance of ORMLiteSqlHelper
     *
     * @param database
     * @param connectionSource
     */
    override fun onCreate(database: SQLiteDatabase?, connectionSource: ConnectionSource?) {

    }

    /**
     * called when database is upgraded
     *
     * @param database
     * @param connectionSource
     * @param oldVersion
     * @param newVersion
     */
    override fun onUpgrade(
        database: SQLiteDatabase?,
        connectionSource: ConnectionSource?,
        oldVersion: Int,
        newVersion: Int
    ) {


    }
}



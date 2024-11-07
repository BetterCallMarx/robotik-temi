package de.fhkiel.temi.robogguide.database

import android.content.Context
import android.util.Log
import de.fhkiel.temi.robogguide.real.Item
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Place
import de.fhkiel.temi.robogguide.real.Text
import de.fhkiel.temi.robogguide.real.Transfer
import java.sql.SQLException

object DataLoader {

    private lateinit var ormhelper : OrmHelper
    private lateinit var database: DatabaseHelper

    //Lists to hold the data of each table
    private val itemsList: MutableList<Items> = mutableListOf()
    private val locationsList: MutableList<Locations> = mutableListOf()
    private val mediaList: MutableList<Media> = mutableListOf()
    private val placesList: MutableList<Places> = mutableListOf()
    private val textsList: MutableList<Texts> = mutableListOf()
    private val transfersList: MutableList<Transfers> = mutableListOf()

    var places: MutableList<Place> = mutableListOf()
        private set

    var transfers: MutableList<Transfer> = mutableListOf()

    fun initData(context: Context) {
        val databaseName = "roboguide.db"
        database = DatabaseHelper(context, databaseName)
        Log.i("Checkpoint", "Hallo from Checkpoint before")
        ormhelper = OrmHelper(context)
        loadData()
        Log.i("Checkpoint2", "Hallo from Checkpoint after")
        fillData()
    }

    private fun fillData(){
        places.clear()
        places.addAll(placesList.map { dp -> Place(
            dp.name,
            getLocations(dp)
            ) })

        transfers.clear()
        transfers.addAll(transfersList
            .map { dtr ->
            Transfer(
                dtr.title,
                getLocation(dtr.location_from),
                getLocation(dtr.location_to),
                getTextsForTransfers(dtr)
            )
        })
    }

    private fun getLocation( locationId: Int) =
        locationsList.filter { dl -> dl.id == locationId }
            .map { dl ->
                Location(
                    dl.id,
                    dl.name,
                    dl.important != 0,
                    getTextsForLocation(dl),
                    getItems(dl)
                )
            }.single()

    private fun getLocations(dp: Places):List<Location> {
        return locationsList
            .filter { dl -> dp.id == dl.places_id }
            .map { dl ->
                Location(
                    dl.id,
                    dl.name,
                    dl.important != 0,
                    getTextsForLocation(dl),
                    getItems(dl)
                )
            }
    }

    private fun getItems(dl: Locations) : List<Item> {
        return itemsList
            .filter { di -> di.locations_id == dl.id }
            .map { di ->
                Item(
                    di.name,
                    getTextsForItem(di)
                )
            }
    }

    private fun getTextsForItem(di: Items) : List<Text> {
        return textsList
            .filter { dt -> dt.items_id == di.id }
            .map { dt ->
                Text(
                    dt.title,
                    dt.text,
                    dt.detailed != 0,
                    mediaList.filter { dm -> dm.texts_id == dt.id }
                        .map { dm ->
                            dm.url
                        }
                )
            }
    }

    private fun getTextsForTransfers(dtr: Transfers) : List<Text> {
        return textsList
            .filter { dt -> dt.transfers_id == dtr.id }
            .map { dt ->
                Text(
                    dt.title,
                    dt.text,
                    dt.detailed != 0,
                    mediaList.filter { dm -> dm.texts_id == dt.id }
                        .map { dm ->
                            dm.url
                        }
                )
            }
    }

    private fun getTextsForLocation(dl: Locations) : List<Text> {
        return textsList
            .filter { dt -> dt.locations_id == dl.id }
            .map { dt ->
                Text(
                    dt.title,
                    dt.text,
                    dt.detailed != 0,
                    mediaList.filter { dm -> dm.texts_id == dt.id }
                        .map { dm ->
                            dm.url
                        }
                )
            }
    }

    private fun loadData(){
        try {

            itemsList.clear()
            itemsList.addAll(ormhelper.itemsDao.queryForAll())
            Log.i("ItemsDao" ,"$itemsList")

            locationsList.clear()
            locationsList.addAll(ormhelper.locationsDao.queryForAll())

            Log.i("LocationsDao" ,"$locationsList")

            mediaList.clear()
            mediaList.addAll(ormhelper.mediaDao.queryForAll())
            Log.i("MediaDao" ,"$mediaList")

            placesList.clear()
            placesList.addAll(ormhelper.placesDao.queryForAll())
            Log.i("PlacesDao" ,"$placesList")

            textsList.clear()
            textsList.addAll(ormhelper.textsDao.queryForAll())
            Log.i("TextsDao" ,"$textsList")

            transfersList.clear()
            transfersList.addAll(ormhelper.transfersDao.queryForAll())
            Log.i("TransfersDao" ,"$transfersList")





        }catch (e: SQLException){
            e.printStackTrace()
        }
    }

}
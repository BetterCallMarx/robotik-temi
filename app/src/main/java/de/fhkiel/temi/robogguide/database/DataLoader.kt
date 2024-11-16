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

    private lateinit var ormhelper: OrmHelper

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

    /**
     * Method to initate and load the Data
     *
     * @param context
     */
    fun initData(context: Context) {
        val databaseName = "roboguide.db"
        ormhelper = OrmHelper(context)
        loadData()
        fillData()
    }

    /**
     * Fills the list with the data from the database
     *
     */
    private fun fillData() {
        places.clear()
        places.addAll(placesList.map { dp ->
            Place(
                dp.name,
                getLocations(dp)
            )
        })

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

    /**
     * helper method used in fillData()
     *
     * @param locationId
     */
    private fun getLocation(locationId: Int) =
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

    /**
     * Helper method used in fillData()
     *
     * @param dp
     * @return returns a locations of list
     */
    private fun getLocations(dp: Places): List<Location> {
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

    /**
     * Helper Method used in fillData()
     *
     * @param dl
     * @return returns a list of items
     */
    private fun getItems(dl: Locations): List<Item> {
        return itemsList
            .filter { di -> di.locations_id == dl.id }
            .map { di ->
                Item(
                    di.name,
                    getTextsForItem(di)
                )
            }
    }

    /**
     * Helper method, used in fillData()
     *
     * @param di
     * @return return a list of texts for the item data class
     */
    private fun getTextsForItem(di: Items): List<Text> {
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

    /**
     * Helper Method used in fillData()
     *
     * @param dtr
     * @return returns a list of texts for transfers
     */
    private fun getTextsForTransfers(dtr: Transfers): List<Text> {
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

    /**
     * Helper method, used in fillData()
     *
     * @param dl
     * @return returns a list of texts for locations
     */
    private fun getTextsForLocation(dl: Locations): List<Text> {
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

    /**
     * Method that loads data from the ORM DAO into the list of the data classes under real
     *
     */
    private fun loadData() {
        try {

            itemsList.clear()
            itemsList.addAll(ormhelper.itemsDao.queryForAll())
            Log.i("ItemsDao", "$itemsList")

            locationsList.clear()
            locationsList.addAll(ormhelper.locationsDao.queryForAll())

            Log.i("LocationsDao", "$locationsList")

            mediaList.clear()
            mediaList.addAll(ormhelper.mediaDao.queryForAll())
            Log.i("MediaDao", "$mediaList")

            placesList.clear()
            placesList.addAll(ormhelper.placesDao.queryForAll())
            Log.i("PlacesDao", "$placesList")

            textsList.clear()
            textsList.addAll(ormhelper.textsDao.queryForAll())
            Log.i("TextsDao", "$textsList")

            transfersList.clear()
            transfersList.addAll(ormhelper.transfersDao.queryForAll())
            Log.i("TransfersDao", "$transfersList")


        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

}
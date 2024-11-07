package de.fhkiel.temi.robogguide

import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import de.fhkiel.temi.robogguide.database.Items
import de.fhkiel.temi.robogguide.database.Locations
import de.fhkiel.temi.robogguide.database.Places
import de.fhkiel.temi.robogguide.database.Texts
import de.fhkiel.temi.robogguide.database.Transfers
import de.fhkiel.temi.robogguide.real.Location
import org.json.JSONObject

class TourHelper(
    /*
    val locations: List<Locations>,
    val texts: List<Texts>,
    val items: List<Items>,
    val transfers: List<Transfers>,
    val locationsForTour: List<String>,
    */

    val mRobot: Robot

    ) : OnGoToLocationStatusChangedListener {
    /*
    lateinit var currentLocation : Location

    private var currentLocationText: String = ""

    //The List containing the list that are going to be visited, is filled according to short or long tour
    private var locationsToVisit: MutableList<Location> = mutableListOf()

    //lists that holds the Texts, that are supposed to be spoken out
    private var textsForLocations: Map<String, Texts?> =  mapOf<String,Texts>()
    private var textsForItems: Map<Items,Texts?> = mapOf<Items,Texts>()
    private var textsForTransfers: Map<Transfers,Texts> = mapOf<Transfers,Texts>()





    fun prepareShortTour(){

        for(location in locations){
            if(location.important == 1){
                //locationsToVisit.add(location)
            }
        }

    }



    init {
        mRobot.addOnGoToLocationStatusChangedListener(this)

    }

    fun shortTour(plocationsToVisit: List<Location>){

        locationsToVisit = plocationsToVisit.toMutableList()

        if(locationsToVisit.isNotEmpty()){
            currentLocation.name = locationsToVisit[0].name
            mRobot.goTo(currentLocation.name)
        }
    }


    //create a map by
    fun mapLocationToText() : Map<String, Texts?> {

        val textByLocationID = texts
            .filter{it.locations_id != null}
            .associateBy { it.locations_id!!}

        return locations.associate { location ->
            location.name to textByLocationID[location.id]
        }
    }


    fun shortTour(){

        Log.i("TourHelper", "$locationsForTour")


        textsForLocations = mapLocationToText()

        if(locationsToVisit.isNotEmpty()){
            currentLocation.name = locationsToVisit[0].name
            mRobot.goTo(currentLocation.name)
        }

    }


    /*
    fun shortTour(locationsForTour: List<Locations>){

        if(locationsForTour.isNotEmpty()){
            if(locationsForTour[0].important == 1){
                currentLocation = locationsForTour[0].name
                mRobot.goTo(currentLocation)

            }
        }

    }
    */


    fun speakText(text: String, isShowOnConversationLayer: Boolean = true){
        mRobot.let { robot ->
            val ttsRequest: TtsRequest = TtsRequest.create(speech = text, isShowOnConversationLayer = isShowOnConversationLayer)
            robot.speak(ttsRequest)
        }
    }

    //function to get the texts of the individuals items of the corresponding locations
    fun getItemsForCurrentLocations(): Map<Items, Texts?> {

        val itemsCurrentLocation: MutableList<Items> = mutableListOf()
        val itemsCurrentLocationTexts: MutableList<Texts> = mutableListOf()
        for(item in items){
            if(item.locations_id == currentLocation.id){
                itemsCurrentLocation.add(item)
            }
        }

        for(text in texts){

            for(item in itemsCurrentLocation){
                if(text.items_id == item.id){
                    itemsCurrentLocationTexts.add(text)
                }
            }
        }

        val textsByItemID = itemsCurrentLocationTexts.associateBy {it.items_id}

        return itemsCurrentLocation.associateWith { item -> textsByItemID[item.id] }

    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        if(status == OnGoToLocationStatusChangedListener.COMPLETE){

            //TODO fill items text
            //TODO speak(locations) the text about the location first
            //textsForItems = getItemsForCurrentLocations()

            textsForLocations[currentLocation.name]?.let { speakText(it.text) }

            //TODO speak(items) talk about each item
            for(item in textsForItems){
                item.value?.let { speakText(it.text) }
            }


            val nextLocationIndex = locationsForTour.indexOf(currentLocation.name)+1

            if(nextLocationIndex<locationsForTour.size){
                currentLocation = locationsToVisit[nextLocationIndex]
                mRobot.goTo(currentLocation.name)
                //TODO speak(transfer) speak about transfer

            }
        }
    }
**/
    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        TODO("Not yet implemented")
    }
}
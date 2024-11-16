package de.fhkiel.temi.robogguide

import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Text
import de.fhkiel.temi.robogguide.real.Transfer
import java.io.Serializable
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class TourManager(
    val mRobot: Robot,
    val inputTransfers: List<Transfer>
) : OnGoToLocationStatusChangedListener, Robot.TtsListener {
    lateinit var locationsToVisit: MutableList<Location>
    lateinit var currentLocation: Location
    var detailedFlag: Boolean = false
    var speaking: Boolean = false

    /**
     * On initiation add necessary Listeners
     */
    init {
        mRobot.addOnGoToLocationStatusChangedListener(this)
        mRobot.addTtsListener(this)
        Log.i("TourManager", "Registered")
    }

    /**
     * creates the list for a short tour and starts it
     *
     * @param listLocations list of locations, from which the location for the tour are filtered
     * @param detailed if the tour is detailed or not
     */
    fun createShortTour(listLocations: MutableList<Location>, detailed: Boolean) {
        //filter by important locations
        detailedFlag = detailed
        locationsToVisit = listLocations.filter { dl -> dl.important }.toMutableList()
        Log.i("Locations", "$locationsToVisit")

        if (locationsToVisit.isNotEmpty()) {
            currentLocation = locationsToVisit[0]
            mRobot.goTo(currentLocation.name)
            Log.i("Going to", currentLocation.name)
        }

    }

    /**
     * creates list for a long tour and starts the tour
     *
     * @param listLocations list of locations that are assigned to locationsToVisit
     * @param detailed whether the tour is detailed or not
     */
    fun createLongTour(listLocations: MutableList<Location>, detailed: Boolean) {
        //filter by important locations
        detailedFlag = detailed
        locationsToVisit = listLocations
        Log.i("Inside createLongTour", locationsToVisit.toString())

        if (locationsToVisit.isNotEmpty()) {
            currentLocation = locationsToVisit[0]
            mRobot.goTo(currentLocation.name)
        }

    }

    /**
     * creates a list for a individual tour
     *
     * @param listLocations list of locations that are assigned to locationsToVisit
     * @param detailed whether the tour is detailed or not
     */
    fun createIndTour(listLocations: MutableList<Location>, detailed: Boolean) {
        detailedFlag = detailed
        locationsToVisit = listLocations
        Log.i("Inside createIndTour", locationsToVisit.toString())
        if (locationsToVisit.isNotEmpty()) {
            currentLocation = locationsToVisit[0]
            mRobot.goTo(currentLocation.name)
        }

    }

    /**
     * simple method to output a string as speech
     *
     * @param text what is to be spoken
     */
    fun speak(text: String) {
        speaking = true
        val ttsRequest: TtsRequest = TtsRequest.create(speech = text, false)
        mRobot.speak(ttsRequest)

    }

    /**
     * Listener method belonging to the method, used to determine if a locations is reached or not
     * If a location is reached call the custom tourStopListener method
     * @param location that is being targeted
     * @param status
     * @param descriptionId
     * @param description
     */
    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {

        if (status == OnGoToLocationStatusChangedListener.COMPLETE) {
            tourStopListener()
        }
    }

    /**
     * Listener for the status of a speech
     *
     * @param ttsRequest
     */
    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        if (ttsRequest.status == TtsRequest.Status.COMPLETED) {
            speaking = false
        }
    }

    /**
     * Method to speak a list of texts, interacting with the ttsListener
     * so robot is stationary while speaking
     *
     * @param texts the list of texts to be spoken out
     */
    fun speakTexts(texts: List<Text>) {
        speaking = true
        Log.i("Textstospeak", "Start")

        texts.forEach { t ->
            Log.i("Textstospeak", t.text)
            if (t.detailed == detailedFlag) {
                speak(t.text)
                while (speaking) {
                    sleep(100)
                }
            }

        }
    }

    /**
     * Method to speak a list of text, without halting its movement
     *
     * @param texts the list of texts to be spoken out
     */
    fun speakTextsTransfer(texts: List<Text>) {
        texts.forEach { t ->
            if (t.detailed == detailedFlag) {
                speak(t.text)
            }

        }
    }


    private var tourStopListener: () -> Unit = {}

    /**
     * Method to register a function to the custom listener
     *
     * @param function
     */
    fun registerAsTourStopListener(function: () -> Unit) {
        tourStopListener = function
    }

}
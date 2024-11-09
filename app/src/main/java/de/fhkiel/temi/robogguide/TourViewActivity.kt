package de.fhkiel.temi.robogguide

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnRobotReadyListener
import de.fhkiel.temi.robogguide.database.DataLoader
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Place
import de.fhkiel.temi.robogguide.real.Text
import de.fhkiel.temi.robogguide.real.Transfer
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class TourViewActivity() : AppCompatActivity(), OnRobotReadyListener {

    private lateinit var mTourManager: TourManager
    private lateinit var mRobot: Robot
    private var isShort: Boolean = false
    var isLong: Boolean = false
    var isInd: Boolean = false
    var isUndetailed: Boolean = false
    var isDetailed: Boolean = false

    private lateinit var stopButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        isShort = intent.getBooleanExtra("isShort", false)
        isLong = intent.getBooleanExtra("isLong", false)
        isInd = intent.getBooleanExtra("isIndividual", false)
        isUndetailed = intent.getBooleanExtra("isUndetailed", false)
        isDetailed = intent.getBooleanExtra("isDetailed", false)

        val selectedLocationsStr = intent.getStringArrayListExtra("selectedLocations")
        val selectedLocations = DataLoader.places[1].locations.filter { dl ->
            selectedLocationsStr?.contains(dl.name) == true
        }.toMutableList()

        val currentLocationName: TextView = findViewById(R.id.currentLocation)
        val currentItemName: TextView = findViewById(R.id.currentItem)

        // Find the Start and Stop Buttons
        val startButton: Button = findViewById(R.id.btnStart)
        stopButton = findViewById(R.id.btnStop)

        startButton.setOnClickListener {
            // Hide the start button when clicked
            startButton.visibility = Button.GONE

            // Show the stop button
            stopButton.visibility = Button.VISIBLE

            // Start the tour
            mTourManager = TourManager(mRobot, DataLoader.transfers)
            mTourManager.createShortTour(DataLoader.places[1].locations.toMutableList(), true)
            mTourManager.registerAsTourStopListener { doTourStop() }
        }

        // Stop the tour when Stop button is clicked
        stopButton.setOnClickListener {
            stopTour()
        }
    }

    fun doTourStop() {
        thread {
            // Speak out every text for the location
            mTourManager.speakTexts(mTourManager.currentLocation.texts)

            sleep(500)

            // Speak every location for each item
            mTourManager.currentLocation.items.forEach {
                mTourManager.speakTexts(it.texts)
            }

            Log.i("Arrived", "Bin angekommen")
            val nextLocationIndex: Int = (mTourManager.locationsToVisit.indexOf(mTourManager.currentLocation)) + 1

            if (nextLocationIndex < mTourManager.locationsToVisit.size) {
                val nextLocation: Location = mTourManager.locationsToVisit[nextLocationIndex]
                Log.i("Next", "Als nächstes $nextLocation")
                mTourManager.mRobot.goTo(nextLocation.name)

                val transferText: List<Transfer> = mTourManager.inputTransfers.filter { tr ->
                    tr.locationFrom == mTourManager.currentLocation && tr.locationTo == nextLocation
                }

                sleep(1000)
                transferText.forEach { t -> mTourManager.speakTextsTransfer(t.texts) }

                mTourManager.currentLocation = nextLocation
            }
        }
    }

    // Funktion zum Stoppen der Tour
    private fun stopTour() {


        // Stop-Button unsichtbar machen
        stopButton.visibility = Button.GONE


    }

    override fun onStart() {
        super.onStart()
        Robot.getInstance().addOnRobotReadyListener(this)
    }

    override fun onStop() {
        super.onStop()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            mRobot = Robot.getInstance()
            mRobot.hideTopBar() // Hide top action bar
            val activityInfo: ActivityInfo = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            Robot.getInstance().onStart(activityInfo)
        }
    }
}

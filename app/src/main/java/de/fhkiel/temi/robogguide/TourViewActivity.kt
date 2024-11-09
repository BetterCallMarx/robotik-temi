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

class TourViewActivity(): AppCompatActivity(), OnRobotReadyListener {

    private lateinit var mTourManager: TourManager
    private lateinit var mRobot: Robot
    private var isShort: Boolean = false
    var isLong: Boolean = false
    var isInd : Boolean = false
    var isUndetailed: Boolean = false
    var isDetailed: Boolean = false



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        isShort = intent.getBooleanExtra("isShort", false)
        isLong = intent.getBooleanExtra("isLong",false)
        isInd = intent.getBooleanExtra("isIndividual",false)

        isUndetailed = intent.getBooleanExtra("isUndetailed",false)
        isDetailed = intent.getBooleanExtra("isDetailed",false)

        val selectedLocationsStr = intent.getStringArrayListExtra("selectedLocations")


        val selectedLocations = DataLoader.places[1].locations.filter { dl ->
            selectedLocationsStr?.contains(dl.name) == true
        }.toMutableList()



        //TODO ABfrage über art und dann ausführung von createtour und  register



        //mTourManager.createLongTour(selectedLocations, true)
        //mTourManager.registerAsTourStopListener{doTourStop()}


        val currentLocationName: TextView = findViewById(R.id.currentLocation)
        val currentItemName: TextView = findViewById(R.id.currentItem)

/*
        val mediaItems : List<Text> = DataLoader.places[2].locations[1].texts
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val adapter = MediaAdapter(this, mediaItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


 */
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            mTourManager = TourManager(mRobot,DataLoader.transfers)
            mTourManager.createShortTour(DataLoader.places[1].locations.toMutableList(), true)
            mTourManager.registerAsTourStopListener { doTourStop() }


        }

    }



    fun doTourStop(){
        thread {

            //speak out every text for the location
            mTourManager.speakTexts(mTourManager.currentLocation.texts)



            sleep(500)

            //speak every location for each item
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
        if (isReady){
            mRobot = Robot.getInstance()
            mRobot.hideTopBar()        // hide top action bar
            // hide pull-down bar
            val activityInfo: ActivityInfo = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            Robot.getInstance().onStart(activityInfo)
        }

    }

}
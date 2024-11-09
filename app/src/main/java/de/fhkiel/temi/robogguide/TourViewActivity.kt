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
    var selectedPlace: String = ""
    var indexP: Int = 0
    private lateinit var selectedLocations : MutableList<Location>



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        selectedPlace = intent.getStringExtra("selectedPlace").toString()

        isShort = intent.getBooleanExtra("isKurzSelected", false)
        isLong = intent.getBooleanExtra("isLang",false)
        isInd = intent.getBooleanExtra("isIndividuell",false)


        isUndetailed = intent.getBooleanExtra("isEinfachSelected",false)
        isDetailed = intent.getBooleanExtra("isAusführlichSelected",false)

        val selectedLocationsStr = intent.getStringArrayListExtra("selectedLocations")



        val placeHolder = DataLoader.places.filter { dp -> dp.name == selectedPlace }
        indexP = DataLoader.places.indexOf(placeHolder[0])

        Log.i("test" , selectedLocationsStr.toString())

        selectedLocations = DataLoader.places[indexP].locations.filter { dl ->
            selectedLocationsStr?.contains(dl.name) == true
        }.toMutableList()

        Log.i("test2" , selectedLocations.toString())
        Log.i("test3" , isInd.toString())





        //TODO ABfrage über art und dann ausführung von createtour und  register



        //mTourManager.createLongTour(selectedLocations, true)
        //mTourManager.registerAsTourStopListener{doTourStop()}



/*
        val mediaItems : List<Text> = DataLoader.places[2].locations[1].texts
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val adapter = MediaAdapter(this, mediaItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


 */
        findViewById<Button>(R.id.btnStart).setOnClickListener {

            if(isShort && !isLong){isShort = true}
            if(!isShort && isLong){isShort = false}

            if(isDetailed && !isUndetailed){isDetailed = true}
            if(!isDetailed && isUndetailed){isDetailed = false}

            if(isInd){
                isDetailed = false
                isShort = false
                Log.i("test3", isDetailed.toString())
                Log.i("test4", isShort.toString())

            }


            if(isShort){
                mTourManager = TourManager(mRobot,DataLoader.transfers)
                mTourManager.createShortTour(DataLoader.places[indexP].locations.toMutableList(),isDetailed)
                mTourManager.registerAsTourStopListener { doTourStop() }
            }else if (!isShort){
                mTourManager = TourManager(mRobot,DataLoader.transfers)
                mTourManager.createLongTour(DataLoader.places[indexP].locations.toMutableList(),isDetailed)
                mTourManager.registerAsTourStopListener { doTourStop() }
            }else if(isInd){
                mTourManager = TourManager(mRobot,DataLoader.transfers)
                mTourManager.createIndTour(selectedLocations, isDetailed)
                mTourManager.registerAsTourStopListener { doTourStop() }

            }


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
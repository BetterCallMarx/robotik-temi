package de.fhkiel.temi.robogguide

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robotemi.sdk.Robot
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import com.squareup.okhttp.internal.framed.FrameReader
import de.fhkiel.temi.robogguide.database.DataLoader
import de.fhkiel.temi.robogguide.real.Item
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Place
import de.fhkiel.temi.robogguide.real.Text
import de.fhkiel.temi.robogguide.real.Transfer
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import com.squareup.picasso.Picasso;

class TourViewActivity(): AppCompatActivity(), OnRobotReadyListener, OnGoToLocationStatusChangedListener {

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
    private lateinit var locationsText: TextView
    private lateinit var itemsText: TextView
    private lateinit var imageItem: ImageView
    var stoppedFlag: Boolean = false



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        locationsText = findViewById<TextView>(R.id.currentLocation)
        itemsText = findViewById<TextView>(R.id.currentItem)
        imageItem = findViewById<ImageView>(R.id.itemImage)

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


        val btnStop = findViewById<Button>(R.id.btnStop)
        btnStop.visibility = Button.GONE
        findViewById<Button>(R.id.btnStop).setOnClickListener(){
            mRobot.cancelAllTtsRequests()
            mRobot.stopMovement()
            stoppedFlag = true
        }

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            btnStop.visibility = Button.VISIBLE

            if(isShort && !isLong){isShort = true}
            if(!isShort && isLong){isShort = false; isLong = true}

            if(isDetailed && !isUndetailed){isDetailed = true}
            if(!isDetailed && isUndetailed){isDetailed = false}

            if(isInd){
                isDetailed = false
                isShort = false
            }


            if(isShort){
                mTourManager = TourManager(mRobot,DataLoader.transfers)
                mTourManager.createShortTour(DataLoader.places[indexP].locations.toMutableList(),isDetailed)
                mTourManager.registerAsTourStopListener { doTourStop() }
            }else if (isLong){
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


            runOnUiThread {
                locationsText.text = mTourManager.currentLocation.name
            }

            //speak out every text for the location
            mTourManager.speakTexts(mTourManager.currentLocation.texts)



            sleep(500)



            //speak every location for each item
            mTourManager.currentLocation.items.forEachIndexed { itemIndex, it ->
                runOnUiThread {
                    itemsText.text = it.name
                    it.texts.forEach { t ->
                        t.mediaUrls.forEach { m ->
                            if (!m.contains("youtube")) {
                                Picasso.get().load(m).into(imageItem)
                            }
                        }
                    }
                }
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
            }else if(nextLocationIndex == mTourManager.locationsToVisit.size){
                mTourManager.speak("Das war es mit der Tour, ich hoffe es hat Ihnen gefallen. Bitte bewerten wie sie die Tour fanden")
                sleep(2000)
                val intent = Intent(this, FeedbackActivity::class.java)
                startActivity(intent)
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
            mRobot.addOnGoToLocationStatusChangedListener(this)
        }

    }

    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {

        if(status == OnGoToLocationStatusChangedListener.ABORT)
        {
            mRobot.stopMovement()
            mTourManager.speak("Tut mir Leid, es ist wohl ein Fehler aufgetreten, bitte starte die Tour neu")
            sleep(1000)
            mRobot.cancelAllTtsRequests()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)


        }


    }

}
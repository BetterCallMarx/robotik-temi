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

class TourViewActivity() : AppCompatActivity(), OnRobotReadyListener,
    OnGoToLocationStatusChangedListener {

    private lateinit var mTourManager: TourManager
    private lateinit var mRobot: Robot
    private var isShort: Boolean = false
    var isLong: Boolean = false
    var isInd: Boolean = false
    var isUndetailed: Boolean = false
    var isDetailed: Boolean = false
    var selectedPlace: String = ""
    var indexP: Int = 0
    private lateinit var selectedLocations: MutableList<Location>
    private lateinit var locationsText: TextView
    private lateinit var itemsText: TextView
    private lateinit var imageItem: ImageView
    private lateinit var progressBar: ProgressBar
    var stoppedFlag: Boolean = false

    /**
     * Method called upon creation of this activity
     *
     * @param savedInstanceState
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        locationsText = findViewById<TextView>(R.id.currentLocation)
        itemsText = findViewById<TextView>(R.id.currentItem)
        imageItem = findViewById<ImageView>(R.id.itemImage)
        progressBar = findViewById<ProgressBar>(R.id.tourProgress)
        progressBar.max = 10

        selectedPlace = intent.getStringExtra("selectedPlace").toString()

        isShort = intent.getBooleanExtra("isKurzSelected", false)
        isLong = intent.getBooleanExtra("isLang", false)
        isInd = intent.getBooleanExtra("isIndividuell", false)


        isUndetailed = intent.getBooleanExtra("isEinfachSelected", false)
        isDetailed = intent.getBooleanExtra("isAusführlichSelected", false)

        val selectedLocationsStr = intent.getStringArrayListExtra("selectedLocations")


        val placeHolder = DataLoader.places.filter { dp -> dp.name == selectedPlace }
        indexP = DataLoader.places.indexOf(placeHolder[0])


        selectedLocations = DataLoader.places[indexP].locations.filter { dl ->
            selectedLocationsStr?.contains(dl.name) == true
        }.toMutableList()


        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        btnCancel.visibility = Button.GONE
        btnStop.visibility = Button.GONE
        /**
         * Button to pause the Tour
         */
        findViewById<Button>(R.id.btnStop).setOnClickListener() {
            btnStart.visibility = Button.VISIBLE
            btnStart.text = "Tour neustarten"
            btnCancel.visibility = Button.VISIBLE
        }
        /**
         * Button to cancel the tour entirely and return to the Mainactivity
         */
        findViewById<Button>(R.id.btnCancel).setOnClickListener(){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        /**
         * Button to start the tour, upon which
         * the tour is created, based of the settings from the previous activity and
         * the custom listener is registered
         */
        findViewById<Button>(R.id.btnStart).setOnClickListener {
            btnStop.visibility = Button.VISIBLE
            btnStart.visibility = Button.GONE
            if (isShort && !isLong) {
                isShort = true
            }
            if (!isShort && isLong) {
                isShort = false; isLong = true
            }

            if (isDetailed && !isUndetailed) {
                isDetailed = true
            }
            if (!isDetailed && isUndetailed) {
                isDetailed = false
            }

            if (isInd) {
                isShort = false
                isLong = false
            }

            if (isShort) {
                mTourManager = TourManager(mRobot, DataLoader.transfers)
                mTourManager.createShortTour(
                    DataLoader.places[indexP].locations.toMutableList(), isDetailed
                )
                mTourManager.registerAsTourStopListener { doTourStop() }
                progressBar.max = mTourManager.locationsToVisit.size
            } else if (isLong) {
                mTourManager = TourManager(mRobot, DataLoader.transfers)
                mTourManager.createLongTour(
                    DataLoader.places[indexP].locations.toMutableList(), isDetailed
                )
                mTourManager.registerAsTourStopListener { doTourStop() }
                progressBar.max = mTourManager.locationsToVisit.size

            } else if (isInd) {
                mTourManager = TourManager(mRobot, DataLoader.transfers)
                mTourManager.createIndTour(selectedLocations, isDetailed)
                mTourManager.registerAsTourStopListener { doTourStop() }
                progressBar.max = mTourManager.locationsToVisit.size

            }

        }

    }

    /**
     * Method that handles the mechanism of a tour, at the end of a tour the FeedbackActivity is called
     * This method is added to the custom listener method
     *
     *
     */
    fun doTourStop() {
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
                                Log.i("Picasso", "Test")
                                Picasso.get().load(m).into(imageItem)
                                sleep(500)
                            }
                        }
                    }

                }
                mTourManager.speakTexts(it.texts)
            }



            Log.i("Arrived", "Bin angekommen")
            val nextLocationIndex: Int =
                (mTourManager.locationsToVisit.indexOf(mTourManager.currentLocation)) + 1

            runOnUiThread {
                progressBar.progress = nextLocationIndex
            }
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
            } else if (nextLocationIndex == mTourManager.locationsToVisit.size) {
                mTourManager.speak("Das war es mit der Tour, ich hoffe es hat Ihnen gefallen. Bitte bewerten wie sie die Tour fanden")
                sleep(2000)
                val intent = Intent(this, FeedbackActivity::class.java)
                startActivity(intent)
            }

        }

    }

    /**
     * method called upon start of robot
     *
     */
    override fun onStart() {
        super.onStart()
        Robot.getInstance().addOnRobotReadyListener(this)
    }

    /**
     * Method called upon stop of robot
     *
     */
    override fun onStop() {
        super.onStop()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }

    /**
     * Method called upon destruct of robot
     *
     */
    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * Method called readiness of robot, instansiated the robot and adds listeners
     *
     * @param isReady
     */
    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            mRobot = Robot.getInstance()
            mRobot.hideTopBar()        // hide top action bar
            // hide pull-down bar
            val activityInfo: ActivityInfo =
                packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            Robot.getInstance().onStart(activityInfo)
            mRobot.addOnGoToLocationStatusChangedListener(this)
        }

    }

    /**
     * Listener for the status of the movement of the robot, if a destination is aborted, the user is requested to start a new Tour
     *
     * @param location
     * @param status
     * @param descriptionId
     * @param description
     */
    override fun onGoToLocationStatusChanged(
        location: String, status: String, descriptionId: Int, description: String
    ) {

        if (status == OnGoToLocationStatusChangedListener.ABORT) {
            mRobot.stopMovement()
            mTourManager.speak("Tut mir Leid, es ist wohl ein Fehler aufgetreten, bitte starte die Tour neu")
            sleep(1000)
            mRobot.cancelAllTtsRequests()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }


    }

}
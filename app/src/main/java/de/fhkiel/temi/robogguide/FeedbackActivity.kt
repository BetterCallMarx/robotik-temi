package de.fhkiel.temi.robogguide

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Activity zur aufnahme vom feedback der benutzer.
 * Der benutzer kann zwischen gut, neural und schlecht wählen, und das feedback
 * wird in einer datei gespeichert. nach der auswahl wird der benutzer zur MainActivity
 * zurückgeleitet.
 */
class FeedbackActivity : AppCompatActivity() {

    // buttons für die Feedback auswahl
    private lateinit var buttonGood: Button
    private lateinit var buttonNeutral: Button
    private lateinit var buttonBad: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        // Initialisierung der Buttons aus dem layout
        buttonGood = findViewById(R.id.button_good)
        buttonNeutral = findViewById(R.id.button_neutral)
        buttonBad = findViewById(R.id.button_bad)

        // Setze Klick Listener für jeden Button
        buttonGood.setOnClickListener {
            selectFeedback(buttonGood, Color.GREEN, "Gut")
        }

        buttonNeutral.setOnClickListener {
            selectFeedback(buttonNeutral, Color.YELLOW, "Neutral")
        }

        buttonBad.setOnClickListener {
            selectFeedback(buttonBad, Color.RED, "Schlecht")
        }
    }

    /**
     * kümmert sich um  die Auswahl eines Feedbacks, passt die button farbem an, speichert das feedback
     * und navigiert zurück zur MainActivity.
     */
    private fun selectFeedback(selectedButton: Button, color: Int, feedback: String) {
        // setzt die fabe für der button
        selectedButton.setBackgroundColor(color)

        // setzt die standartfarbe der nicht ausgewählten buttons zurück
        if (selectedButton != buttonGood) {
            buttonGood.setBackgroundColor(getColor(android.R.color.holo_green_light))
        }
        if (selectedButton != buttonNeutral) {
            buttonNeutral.setBackgroundColor(getColor(android.R.color.holo_orange_light))
        }
        if (selectedButton != buttonBad) {
            buttonBad.setBackgroundColor(getColor(android.R.color.holo_red_light))
        }

        // speichert das Feedback in einer Datei
        saveFeedbackToFile(feedback)
        createAndSaveTxtFile("feedbacks", "test.txt", "test")

        // naviegiert zurück zur MainActivity
        intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Speichert das Feedback des Benutzers in einer Textdatei. Der Dateiname
     * enthält das aktuelle Datum.
     *
     * @param feedback Der text des feedbacks der gespeichert wird.
     */
    private fun saveFeedbackToFile(feedback: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Datumsformat
        val date = dateFormat.format(Date()) // Aktuelles Datum
        val fileName = "$date-feedback.txt" // Dateiname mit Datum

        // Datei im internen Speicher erstellen oder öffnen
        val file = File(filesDir, fileName)
        file.appendText("Feedback: $feedback\n") // Feedback anhängen
    }

    /**
     * erstellt die Textdatei in einem Unterverzeichnis und speichert den angegebenen inhalt.
     *
     * @param subDir Der name des unterverzeichnis, in dem die datei erstellt wird.
     * @param fileName Der name der datei die erstellt wird.
     * @param content Der inhalt, der in die datei geschrieben wird.
     * @return true wenn die Datei erfolgreich erstellt wurde, andernfalls false
     */
    private fun createAndSaveTxtFile(subDir: String, fileName: String, content: String): Boolean {
        try {
            // erstellt das Unterverzeichnis im internen speicherverzeichnis
            val directory = File(filesDir, subDir)
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    println("Failed to create directory: $directory")
                    return false
                }
            }

            // erstellt die datei innerhalb des unterverzeichnises und schreibe den inhalt
            val file = File(directory, fileName)
            file.writeText(content) // Inhalt in die Datei schreiben
            println("File created successfully at: ${file.absolutePath}")
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}

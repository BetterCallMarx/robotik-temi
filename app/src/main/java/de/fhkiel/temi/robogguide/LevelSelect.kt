package de.fhkiel.temi.robogguide

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import de.fhkiel.temi.robogguide.database.DataLoader

class LevelSelect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_select)

        DataLoader.initData(this)

        val placesSpinner: Spinner = findViewById(R.id.places_spinner)

        val placeNames = DataLoader.places.map { it.name }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, placeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        placesSpinner.adapter = adapter
    }
}

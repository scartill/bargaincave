package ru.bargaincave.warehouse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.bargaincave.warehouse.databinding.ActivitySorterMainBinding

class SorterMainActivity : AppCompatActivity() {
    private lateinit var b : ActivitySorterMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivitySorterMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        b.newLot.setOnClickListener {
            val intent = Intent(this, NewLotActivity::class.java)
            startActivity(intent)
        }
    }
}
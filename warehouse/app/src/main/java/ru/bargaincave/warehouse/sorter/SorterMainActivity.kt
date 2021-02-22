package ru.bargaincave.warehouse.sorter

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
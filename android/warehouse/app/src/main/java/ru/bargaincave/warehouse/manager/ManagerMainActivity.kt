package ru.bargaincave.warehouse.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.databinding.ActivityManagerMainBinding
import androidx.recyclerview.widget.LinearLayoutManager




class ManagerMainActivity : AppCompatActivity() {
    private lateinit var b: ActivityManagerMainBinding
    private val lla: LotListAdapter = LotListAdapter()
    private val lots: MutableList<Lot> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityManagerMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        b.lotList.layoutManager = linearLayoutManager
        b.lotList.adapter = lla

        b.lotsLoadProgress.visibility = View.VISIBLE
        Amplify.DataStore.query(
            Lot::class.java,
            {
                lots.clear()
                while (it.hasNext()) {
                    val lot = it.next()
                    Log.i("Cave", "Lot $lot")
                    lots.add(lot)
                }

                runOnUiThread {
                    Log.i("Cave", "Lots collected ${lots.size}")
                    lla.submitList(lots)
                    b.lotsLoadProgress.visibility = View.GONE
                }
            },
            { error ->
                Log.e("Cave", "Could not query DataStore", error)

                runOnUiThread {
                    b.managerMainError.text = error.message
                    b.lotsLoadProgress.visibility = View.GONE
                }
            }
        )

        /*
        // TODO: move to after-login code
        Amplify.DataStore.observe(
            Lot::class.java,
            { Log.i("Cave", "Observation began.") },
            { Log.i("Cave", it.item().toString()) },
            { Log.e("Cave", "Observation failed.", it) },
            { Log.i("Cave", "Observation complete.") }
        )
        */
    }
}

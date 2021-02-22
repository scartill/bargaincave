package ru.bargaincave.warehouse.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.databinding.ActivityManagerMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.datastore.DataStoreItemChange

class ManagerMainActivity : AppCompatActivity() {
    private lateinit var b: ActivityManagerMainBinding
    private val lla: LotListAdapter = LotListAdapter(::onLotClick)
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
                synchronized(lots) {
                    lots.clear()
                    while (it.hasNext()) {
                        val lot = it.next()
                        Log.i("Cave", "Lot $lot")
                        lots.add(lot)
                    }
                }

                runOnUiThread {
                    Log.i("Cave", "Lots collected ${lots.size}")
                    lla.submitList(lots)
                    b.lotsLoadProgress.visibility = View.GONE
                    startObserve()
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
    }

    private fun startObserve() {
        Amplify.DataStore.observe(
            Lot::class.java,
            {
                Log.i("Cave", "Observation began.")
            },
            { change ->
                val lot = change.item()
                val type = change.type()
                Log.i("Cave", "Item changed $lot $type")

                synchronized(lots) {
                    when(type) {
                        DataStoreItemChange.Type.CREATE -> {
                            Log.d("Cave", "Creating item")
                            lots.add(lot)
                        }
                        DataStoreItemChange.Type.UPDATE -> {
                            lots.indexOfFirst { e -> e.id == lot.id }.let { inx ->
                                Log.d("Cave", "Updating item $inx")
                                lots[inx] = lot
                            }
                        }
                        DataStoreItemChange.Type.DELETE -> {
                            Log.d("Cave", "Deleting item $lot")
                            lots.removeIf { e -> e.id == lot.id }
                        }
                    }
                }

                runOnUiThread {
                    lla.submitList(lots)
                    lla.notifyDataSetChanged()
                }
            },
            { error ->
                Log.e("Cave", "Observation failed.", error)
                runOnUiThread {
                    b.managerMainError.text = error.message
                }
            },
            {
                Log.i("Cave", "Observation complete.")
            }
        )
    }

    private fun onLotClick(lot: Lot) {
        val intent = Intent(this, LotApproveActivity::class.java)
        intent.putExtra("lot_id", lot.id)
        startActivity(intent)
    }
}

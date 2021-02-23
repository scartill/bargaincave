package ru.bargaincave.warehouse.manager

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.R
import ru.bargaincave.warehouse.databinding.ActivityLotApproveBinding
import java.io.File

class LotApproveActivity : AppCompatActivity() {
    private lateinit var b: ActivityLotApproveBinding
    private var lot: Lot? = null
    private var loaded : Boolean = false
    private var priceValid : Boolean = false
    private var publishing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityLotApproveBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        setGUI()

        b.price.addTextChangedListener {
            priceValid = !it.isNullOrBlank()
            setGUI()
        }

        val lotId = intent.getStringExtra("lot_id")
        Where.matches(Lot.ID.eq(lotId))
        Amplify.DataStore.query(
            Lot::class.java,
            Where.matches(Lot.ID.eq(lotId)),
            {
                if (it.hasNext()) {
                    lot = it.next()
                    Log.i("Cave", "Loaded $lot")

                    runOnUiThread {
                        val na = getString(R.string.unknown)
                        lot?.let { lot ->
                            b.laFruit.text = lot.fruit ?: na
                            b.laWeight.text = lot.weightKg?.toString() ?: na
                            b.laComment.text = lot.comment ?: na

                            lot.photo?.also {
                                val outputDir = File(applicationContext.cacheDir, "/image")
                                outputDir.mkdir()
                                val photoFile = File.createTempFile("lot_photo_", ".jpg", outputDir)
                                photoFile.deleteOnExit()

                                Amplify.Storage.downloadFile(
                                    lot.photo,
                                    photoFile,
                                    {
                                        Log.i("Cave", "Photo download complete")

                                        BitmapFactory.decodeFile(photoFile.path)?.also { bitmap ->
                                            b.laPhoto.setImageBitmap(bitmap)
                                        }

                                        loaded = true
                                        setGUI()
                                    },
                                    { error ->
                                        Log.e("Cave", "Photo Download failed", error)
                                        runOnUiThread {
                                            b.laError.text = error.message
                                            b.laProgress.visibility = View.GONE
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                else
                {
                    Log.i("Cave", "A lot was not found")
                    runOnUiThread {
                        b.laError.text = getString(R.string.unable_to_load)
                    }
                }
            },
            { error ->
                Log.e("Cave", "Could not query DataStore", error)
                runOnUiThread {
                    b.laError.text = error.message
                    b.laProgress.visibility = View.GONE
                }
            }
        )

        b.publish.setOnClickListener {
            lot?.run {
                publishing = true
                setGUI()

                val payload = "{ \"publish_lot_by_id\": \"${id}\" }"
                Log.i("Cave", "Telegraphing $payload")

                val options = RestOptions.builder()
                    .addPath("/telegrampublish")
                    .addBody(payload.toByteArray())
                    .build()

                Amplify.API.post(options,
                    {
                        Log.i("Cave", "POST succeeded")
                        runOnUiThread {
                            publishing = false
                            setGUI()
                        }
                    },
                    { error ->
                        Log.e("Cave", "Could execute API request", error)
                        runOnUiThread {
                            b.laError.text = error.message
                            publishing = false
                            setGUI()
                        }
                    }
                )
            }
        }
    }

    private fun setGUI() {
        b.publish.isEnabled = loaded && priceValid && !publishing
        b.delete.isEnabled = false
        b.price.isEnabled = loaded

        val inProgress = !loaded || publishing
        b.laProgress.visibility = if(inProgress) View.VISIBLE else View.GONE
    }
}
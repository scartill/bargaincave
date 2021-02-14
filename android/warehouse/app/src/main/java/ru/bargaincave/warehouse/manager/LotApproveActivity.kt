package ru.bargaincave.warehouse.manager

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.generated.model.Lot
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageDownloadFileOptions
import ru.bargaincave.warehouse.R
import ru.bargaincave.warehouse.databinding.ActivityLotApproveBinding
import java.io.File

class LotApproveActivity : AppCompatActivity() {
    private lateinit var b: ActivityLotApproveBinding
    private var loaded : Boolean = false
    private var priceValid : Boolean = false

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
                    val lot = it.next()
                    Log.i("Cave", "Loaded $lot")

                    runOnUiThread {
                        val na = getString(R.string.unknown)
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

                                    BitmapFactory.decodeFile(photoFile.path)?.also {
                                        b.laPhoto.setImageBitmap(it)
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
            
        }
    }

    private fun setGUI() {
        b.publish.isEnabled = loaded && priceValid
        b.delete.isEnabled = false
        b.price.isEnabled = loaded
        b.laProgress.visibility = if(loaded) View.GONE else View.VISIBLE
    }
}

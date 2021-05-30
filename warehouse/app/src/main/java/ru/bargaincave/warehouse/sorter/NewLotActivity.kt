package ru.bargaincave.warehouse.sorter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.FruitInfo
import ru.bargaincave.warehouse.R
import ru.bargaincave.warehouse.databinding.ActivityNewLotBinding
import ru.bargaincave.warehouse.media.LotMedia
import ru.bargaincave.warehouse.media.LotPhoto
import ru.bargaincave.warehouse.media.LotPhotoListAdapter
import ru.bargaincave.warehouse.media.S3Uploader
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate


const val BITMAP_TARGET_WIDTH = 1024


class NewLotActivity : AppCompatActivity() {

    private lateinit var b: ActivityNewLotBinding
    private lateinit var currentPhotoPath: String
    private var photos = mutableListOf<LotPhoto>()
    private lateinit var photoLA: LotPhotoListAdapter
    private lateinit var fruitAdapter: ArrayAdapter<String>
    private lateinit var varietyAdapter: ArrayAdapter<String>
    private lateinit var conditionsAdapter: ArrayAdapter<String>
    private lateinit var calibersAdapter: ArrayAdapter<Int>
    private lateinit var originsAdapter: ArrayAdapter<String>

    private lateinit var arrivalDate : LocalDate
    private lateinit var expirationDate : LocalDate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentPhotoPath = ""

        b = ActivityNewLotBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        photoLA = LotPhotoListAdapter(applicationContext)

        b.submit.isEnabled = false
        b.progressBar.visibility = View.GONE

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        b.photoList.layoutManager = linearLayoutManager
        b.photoList.adapter = photoLA
        photoLA.submitList(photos)

        fruitAdapter = createSpinAdapter(FruitInfo.Sorts.keys.toMutableList())
        b.spinFruit.adapter = fruitAdapter

        val initialSorts = FruitInfo.Sorts[fruitAdapter.getItem(0)] ?: arrayListOf(getString(R.string.unknown))
        varietyAdapter = createSpinAdapter(initialSorts.toMutableList())
        b.spinVariety.adapter = varietyAdapter

        b.spinFruit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                val na = getString(R.string.unknown)
                val fruit = parent?.getItemAtPosition(position).toString()
                val sorts = FruitInfo.Sorts[fruit] ?: arrayListOf(na)
                varietyAdapter.clear()
                varietyAdapter.addAll(sorts)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // Empty
                return
            }
        }

        conditionsAdapter = createSpinAdapter(FruitInfo.Conditions.toMutableList())
        b.condition.adapter = conditionsAdapter

        calibersAdapter = createSpinAdapter(FruitInfo.Calibers.toMutableList())
        b.caliber.adapter = calibersAdapter

        originsAdapter = createSpinAdapter(FruitInfo.Origins.toMutableList())
        b.origin.adapter = originsAdapter

        arrivalDate = LocalDate.now()
        b.arrival.init(arrivalDate.year, arrivalDate.monthValue - 1, arrivalDate.dayOfMonth) {
            _, year, month, day ->
                arrivalDate = LocalDate.of(year, month + 1, day)
                Log.i("Cave", "Arrival $arrivalDate")
        }

        expirationDate = LocalDate.now()
        b.expiration.init(expirationDate.year, expirationDate.monthValue - 1, expirationDate.dayOfMonth) {
            _, year, month, day ->
                expirationDate = LocalDate.of(year, month + 1, day)
        }

        val photoRequester = registerForActivityResult(ActivityResultContracts.TakePicture()) { taken ->
            try {
                if (taken) {
                    Log.i("Cave", "Scaling the image")
                    BitmapFactory.decodeFile(currentPhotoPath)?.also {
                        val ratio = it.width.toDouble() / it.height.toDouble()
                        val w = BITMAP_TARGET_WIDTH
                        val h = (BITMAP_TARGET_WIDTH / ratio).toInt()
                        val scaled = it.scale(w, h)

                        val out = FileOutputStream(currentPhotoPath)
                        scaled.compress(Bitmap.CompressFormat.JPEG, 100, out)
                        out.flush()
                        out.close()
                    }

                    val filename = File(currentPhotoPath).name
                    Log.i("Cave", "Rendering a preview for $filename")
                    photos.add(LotPhoto(filename))
                    photoLA.submitList(photos)
                    b.submit.isEnabled = true
                }
            } catch (error: Exception) {
                b.error.text = error.message
                Log.e("Cave", "Unable to render a preview", error)
            }
        }

        b.photo.setOnClickListener {
            Log.i("Cave", "Taking a photo")
            try {
                val outputDir = File(applicationContext.cacheDir, "/image")
                outputDir.mkdir()
                val outputFile = File.createTempFile("lot_photo_", ".jpg", outputDir)
                outputFile.deleteOnExit()

                currentPhotoPath = outputFile.path

                val photoURI = FileProvider.getUriForFile(this, "ru.bargaincave.warehouse.fileprovider", outputFile)
                photoRequester.launch(photoURI)
            } catch (error: Exception) {
                b.error.text = error.message
                Log.e("Cave", "Unable to take a photo", error)
            }
        }

        b.submit.setOnClickListener {
            setGUI(false)

            val submitting = submit()

            if(!submitting) {
                // Restoring GUI is async submit wasn't launched
                setGUI(true)
            }
        }
    }

    private fun <T> createSpinAdapter(list : MutableList<T>) : ArrayAdapter<T> {
        val result = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            list
        )
        result.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return result
    }

    private fun setGUI(enabled: Boolean) {
        b.spinFruit.isEnabled = enabled
        b.spinVariety.isEnabled = enabled
        b.photo.isEnabled = enabled
        b.totalWeightKg.isEnabled = enabled
        b.caliber.isEnabled = enabled
        b.palletLabel.isEnabled = enabled
        b.condition.isEnabled = enabled
        b.origin.isEnabled = enabled
        b.arrival.isEnabled = enabled
        b.expiration.isEnabled = enabled
        b.comment.isEnabled = enabled

        b.submit.isEnabled = enabled
        b.progressBar.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun submit() : Boolean {
        if (photos.size == 0) {
            b.photo.requestFocus()
            return false
        }

        val builder = Lot.builder()

        builder.state("NEW")
        builder.fruit(b.spinFruit.selectedItem.toString())
        builder.variety(b.spinVariety.selectedItem.toString())

        val tw = b.totalWeightKg.text.toString().toDoubleOrNull()
        if (tw == null) {
            Toast.makeText(applicationContext, getString(R.string.total_weight_required), Toast.LENGTH_SHORT).show()
            b.totalWeightKg.requestFocus()
            return false
        }
        builder.totalWeightKg(tw)
        builder.caliber(b.caliber.selectedItem.toString().toInt())
        builder.palletWeightKg(b.palletWeighKg.text.toString().toDoubleOrNull() ?: 0.0)
        builder.condition(b.condition.selectedItem.toString())
        builder.origin(b.origin.selectedItem.toString())
        builder.arrival(arrivalDate.toString())
        builder.expiration(expirationDate.toString())

        builder.comment(b.comment.text.toString())

        Log.i("cave", "Launching async submit")
        submitAsync(builder, photos)

        return true
    }

    private fun submitAsync(builder: Lot.BuildStep, photos: List<LotPhoto>) {
        Log.i("cave", "submitting a new lot")
        setGUI(false)

        val media = LotMedia(photos)
        val resourcesJson = LotMedia.describe(media)

        val s3 = S3Uploader(media)
        s3.uploadAsync(applicationContext,
            {
                val item = builder
                    .resources(resourcesJson)
                    .build()

                Amplify.DataStore.save(
                    item,
                    { success ->
                        Log.i("Cave", "Saved item " + success.item().fruit.toString())
                        runOnUiThread {
                            Toast.makeText(applicationContext, getString(R.string.submit_ok), Toast.LENGTH_LONG).show()
                            finish()
                        }
                    },
                    { error ->
                        Log.e("Cave", "Could not save item to DataStore", error)
                    }
                )
            },
            { error ->
                Log.i("Cave", "Photos upload failed")
                runOnUiThread {
                    b.error.text = error.message
                    setGUI(true)
                }
            }
        )
    }
}

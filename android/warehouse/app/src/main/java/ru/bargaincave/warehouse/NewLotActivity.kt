package ru.bargaincave.warehouse

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.FileProvider
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Fruit
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.databinding.ActivityNewLotBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_IMAGE_CAPTURE = 100

class NewLotActivity : AppCompatActivity() {

    private lateinit var b: ActivityNewLotBinding
    private lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentPhotoPath = ""

        b = ActivityNewLotBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        val spinner: Spinner = b.spinFruit
        ArrayAdapter.createFromResource(
            this,
            R.array.fruit_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        b.photo.setOnClickListener {
            Log.i("Cave", "Taking a photo")
            try {
                val outputDir = File(applicationContext.cacheDir, "/image")
                outputDir.mkdir()
                val outputFile = File.createTempFile("lot_photo_", ".jpg", outputDir)
                outputFile.deleteOnExit()
                currentPhotoPath = outputFile.path
                Log.i("Cave", "Using temp file $currentPhotoPath")

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                val photoURI = FileProvider.getUriForFile(this, "ru.bargaincave.warehouse.fileprovider", outputFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (error: Exception) {
                b.error.text = error.message
                Log.e("Cave", "Unable to take a photo", error)
            }
        }

        b.submit.setOnClickListener {
            val fruit = b.spinFruit.selectedItem.toString()

            val weight = b.weight.text.toString().toFloatOrNull()

            if (weight == null) {
                b.weight.requestFocus()
                return@setOnClickListener
            }

            val item: Lot = Lot.builder()
                .fruit(fruit)
                .weightKg(weight)
                .comment("Some Israeli Class B Mango")
                .build()

            Amplify.DataStore.save(
                item,
                { success -> Log.i("Cave", "Saved item: " + success.item().fruit.toString()) },
                { error -> Log.e("Cave", "Could not save item to DataStore", error) }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("Cave", "Rendering a preview")
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            b.imageView.setImageBitmap(imageBitmap)

            Log.i("Cave", "Photo path $currentPhotoPath")
        }
    }
}
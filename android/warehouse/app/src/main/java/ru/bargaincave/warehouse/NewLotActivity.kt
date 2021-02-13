package ru.bargaincave.warehouse

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.coroutineScope
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Lot
import kotlinx.coroutines.launch
import ru.bargaincave.warehouse.databinding.ActivityNewLotBinding
import java.io.File
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

        b.submit.isEnabled = false
        b.progressBar.visibility = View.GONE

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

                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    it.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
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
            setGUI(false)

            val fruit = b.spinFruit.selectedItem.toString()

            if (currentPhotoPath == "") {
                Log.i("cave", "unable: photo is required")
                b.photo.requestFocus()
                return@setOnClickListener
            }

            val weight = b.weight.text.toString().toFloatOrNull()

            if (weight == null) {
                Log.i("Cave", "Unable: weight is required")
                b.weight.requestFocus()
                return@setOnClickListener
            }

            val comment = b.comment.text.toString()
            Log.i("cave", "Launching async submit")

            this.lifecycle.coroutineScope.launch {
                asyncSubmit(fruit, currentPhotoPath, weight, comment)
            }.also {
                it.invokeOnCompletion { error ->
                    runOnUiThread {
                        if (error == null) {
                            Log.i("cave", "Submit successful")
                            Toast.makeText(applicationContext, getString(R.string.submit_ok), Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            b.error.text = error.message
                            Log.e("cave", "unable to submit", error)
                            setGUI(true)
                        }
                    }
                }
            }
        }
    }

    private fun setGUI(enabled: Boolean) {
        b.submit.isEnabled = enabled
        b.spinFruit.isEnabled = enabled
        b.photo.isEnabled = enabled
        b.weight.isEnabled = enabled
        b.comment.isEnabled = enabled
        b.progressBar.visibility = if(enabled) View.GONE else View.VISIBLE
    }

    private fun asyncSubmit(fruit: String, currentPhotoPath: String, weight: Float, comment: String) {
        Log.i("cave", "submitting a new lot")

        val photoFile = File(currentPhotoPath)
        val s3key = photoFile.name
        Amplify.Storage.uploadFile(
            s3key,
            photoFile,
            { result ->
                Log.i("Cave", "Successfully uploaded: " + result.key)

                val item: Lot = Lot.builder()
                    .fruit(fruit)
                    .photo(s3key)
                    .weightKg(weight)
                    .comment(comment)
                    .build()

                Amplify.DataStore.save(
                    item,
                    { success ->
                        Log.i("Cave", "Saved item: " + success.item().photo.toString())
                    },
                    { error ->
                        Log.e("Cave", "Could not save item to DataStore", error)
                        throw error
                    }
                )
            },
            { error ->
                Log.e("Cave", "Upload failed", error)
                throw error
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Log.i("Cave", "Rendering a preview")
                BitmapFactory.decodeFile(currentPhotoPath)?.also {
                    b.imageView.setImageBitmap(it)
                    b.submit.isEnabled = true
                }
            }
        } catch (error: Exception) {
            b.error.text = error.message
            Log.e("Cave", "Unable to render a preview", error)
        }
    }
}
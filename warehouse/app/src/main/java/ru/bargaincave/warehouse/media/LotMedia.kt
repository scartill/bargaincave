package ru.bargaincave.warehouse.media

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.options.StorageUploadFileOptions
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.bargaincave.warehouse.databinding.ItemLotPhotoBinding
import java.io.File

class LotPhoto(var photoFile: String)

class LotPhotoHolder(private val b: ItemLotPhotoBinding): RecyclerView.ViewHolder(b.root) {

    companion object {
        fun create(viewGroup: ViewGroup): LotPhotoHolder {
            val b = ItemLotPhotoBinding.inflate(LayoutInflater.from(viewGroup.context))
            return LotPhotoHolder(b)
        }
    }

    private var currentPhoto: LotPhoto? = null

    fun bind(ctx: Context, photo: LotPhoto) {
        currentPhoto = photo
        currentPhoto?.photoFile?.let { filename ->
            Log.d("Cave", "Rendering $filename")
            val outputDir = File(ctx.cacheDir, "/image")
            val filepath = File(outputDir, filename).path
            BitmapFactory.decodeFile(filepath)?.also {
                b.imageView.setImageBitmap(it)
            }
        }
    }
}

class LotPhotoListAdapter(val ctx: Context) : ListAdapter<LotPhoto, LotPhotoHolder>(LotPhotoDiffCallback) {
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LotPhotoHolder {
        return LotPhotoHolder.create(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: LotPhotoHolder, position: Int) {
        viewHolder.bind(ctx, getItem(position))
    }
}

object LotPhotoDiffCallback: DiffUtil.ItemCallback<LotPhoto>() {
    override fun areItemsTheSame(oldItem: LotPhoto, newItem: LotPhoto): Boolean {
        return oldItem.photoFile == newItem.photoFile
    }

    override fun areContentsTheSame(oldItem: LotPhoto, newItem: LotPhoto): Boolean {
        return oldItem.photoFile == newItem.photoFile
    }
}

class S3Uploader(val media: LotMedia) {

    fun uploadAsync(ctx: Context, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        uploadFrom(ctx, 0, onSuccess, onError)
    }

    private fun uploadFrom(ctx: Context, index: Int, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        try {
            val photos = media.photos
            val photoKey = media.photos[index].photoFile
            val outputDir = File(ctx.cacheDir, "/image")
            val photoFile = File(outputDir, photoKey)
            Log.i("Cave", "Uploading $index ($photoKey)")

            val options = StorageUploadFileOptions.builder()
                .accessLevel(StorageAccessLevel.PUBLIC)
                .build()

            Log.i("Cave", "Uploading $photoKey")
            Amplify.Storage.uploadFile(photoKey, photoFile, options,
                {
                    Log.i("Cave", "Upload completed")
                    if (index < photos.size - 1) {
                        uploadFrom(ctx, index + 1, onSuccess, onError)
                    } else {
                        onSuccess()
                    }
                },
                { error ->
                    Log.e("Cave", "Upload failed", error)
                    onError(error)
                }
            )
        }
        catch(error: Exception) {
            Log.e("Cave", "Upload failed", error)
            onError(error)
        }
    }
}

class S3Downloader(val media: LotMedia) {
    fun downloadAsync(ctx: Context, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        downloadFrom(ctx, 0, onSuccess, onError)
    }

    private fun downloadFrom(ctx: Context, index: Int, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        try {
            val photos = media.photos
            val photoKey = media.photos[index].photoFile
            val outputDir = File(ctx.cacheDir, "/image")
            outputDir.mkdir()
            val photoFile = File(outputDir, photoKey)
            photoFile.deleteOnExit()

            Log.d("Cave", "Downloading $index ($photoKey)")
            Amplify.Storage.downloadFile(
                photoKey,
                photoFile,
                {
                    Log.i("Cave", "Photo download complete")
                    if (index < photos.size - 1) {
                        downloadFrom(ctx, index + 1, onSuccess, onError)
                    } else {
                        onSuccess()
                    }
                },
                { error ->
                    Log.e("Cave", "Photo Download failed", error)
                    onError(error)
                }
            )
        }
        catch(error: Exception) {
            Log.e("Cave", "Upload failed", error)
            onError(error)
        }
    }
}

// NB: 'photos' field is needed for JSON deserialization
class LotMedia(val photos: List<LotPhoto>) {
    companion object {
        fun describe(media: LotMedia): String {
            return Klaxon().toJsonString(media)
        }

        fun restore(json: String): LotMedia? {
            return Klaxon().parse<LotMedia>(json)
        }
    }
}

package ru.bargaincave.warehouse.media

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.StorageAccessLevel
import com.amplifyframework.storage.StorageException
import com.amplifyframework.storage.options.StorageUploadFileOptions
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

    fun bind(photo: LotPhoto) {
        currentPhoto = photo
        currentPhoto?.photoFile.let { filename ->
            BitmapFactory.decodeFile(filename)?.also {
                b.imageView.setImageBitmap(it)
            }
        }
    }
}

class LotPhotoListAdapter(): ListAdapter<LotPhoto, LotPhotoHolder>(LotPhotoDiffCallback) {
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LotPhotoHolder {
        return LotPhotoHolder.create(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: LotPhotoHolder, position: Int) {
        viewHolder.bind(getItem(position))
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

object S3Helper {
    suspend fun UploadPhotosAsync(photos: List<LotPhoto>) {

        photos.forEach { photo ->
            val photoFile = File(photo.photoFile)
            val s3key = photoFile.name

            val options = StorageUploadFileOptions.builder()
                .accessLevel(StorageAccessLevel.PUBLIC)
                .build()

          //  val upload = Amplify.Storage.uploadFile(s3key, photoFile, options)

            try {
           //     val result = upload.result()
           //     Log.i("Cave", "Successfully uploaded: ${result.key}")
            } catch (error: StorageException) {
                Log.e("Cave", "Upload failed", error)
            }
        }
    }
}
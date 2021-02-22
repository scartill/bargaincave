package ru.bargaincave.warehouse.manager

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.databinding.ItemLotBinding

class LotViewHolder(
    private val b: ItemLotBinding,
    private val onClick: (Lot) -> Unit): RecyclerView.ViewHolder(b.root) {

    companion object {
        fun create(viewGroup: ViewGroup, onClick: (Lot) -> Unit): LotViewHolder {
            val b = ItemLotBinding.inflate(LayoutInflater.from(viewGroup.context))
            return LotViewHolder(b, onClick)
        }
    }

    private var currentLot: Lot? = null

    fun bind(lot: Lot) {
        currentLot = lot
        // TODO: fruit name is not localized
        b.itemFruit.text = lot.fruit
        b.itemWeight.text = lot.weightKg.toString()
        b.itemComment.text = if(lot.comment.isNotEmpty()) lot.comment else "------"

        b.root.setOnClickListener {
            currentLot?.let {
                onClick(it)
            }
        }
    }
}

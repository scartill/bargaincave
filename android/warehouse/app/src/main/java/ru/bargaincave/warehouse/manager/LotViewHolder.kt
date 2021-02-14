package ru.bargaincave.warehouse.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.databinding.ItemLotBinding

class LotViewHolder(binding: ItemLotBinding): RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(viewGroup: ViewGroup): LotViewHolder {
            val b = ItemLotBinding.inflate(LayoutInflater.from(viewGroup.context))
            return LotViewHolder(b)
        }
    }

    private val b: ItemLotBinding = binding

    fun bind(lot: Lot) {
        // TODO: fruit name is not localized
        b.itemFruit.text = lot.fruit
        b.itemWeight.text = lot.weightKg.toString()
        b.itemComment.text = if(lot.comment.isNotEmpty()) lot.comment else "------"
    }
}

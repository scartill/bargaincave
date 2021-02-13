package ru.bargaincave.warehouse.manager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.databinding.ItemLotBinding

class ViewHolder(binding: ItemLotBinding): RecyclerView.ViewHolder(binding.root) {
    val b: ItemLotBinding = binding

    fun bind(lot: Lot) {
        // TODO: fruit name is not localized
        b.itemFruit.text = lot.fruit
        b.itemWeight.text = lot.weightKg.toString()
        b.itemComment.text = if(lot.comment.isNotEmpty()) lot.comment else "------"
    }
}

class LotListAdapter: ListAdapter<Lot, ViewHolder>(LotDiffCallback) {
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val b = ItemLotBinding.inflate(LayoutInflater.from(viewGroup.context))
        return ViewHolder(b)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}

object LotDiffCallback: DiffUtil.ItemCallback<Lot>() {
    override fun areItemsTheSame(oldItem: Lot, newItem: Lot): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Lot, newItem: Lot): Boolean {
        return oldItem.id == newItem.id
    }
}

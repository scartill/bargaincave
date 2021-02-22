package ru.bargaincave.warehouse.manager

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.amplifyframework.datastore.generated.model.Lot

class LotListAdapter(private val onClick: (Lot) -> Unit): ListAdapter<Lot, LotViewHolder>(LotDiffCallback) {
    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LotViewHolder {
        return LotViewHolder.create(viewGroup, onClick)
    }

    override fun onBindViewHolder(viewHolder: LotViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
    }
}

object LotDiffCallback: DiffUtil.ItemCallback<Lot>() {
    override fun areItemsTheSame(oldItem: Lot, newItem: Lot): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Lot, newItem: Lot): Boolean {
        return oldItem.id == newItem.id &&
                oldItem.comment == newItem.comment

    }
}

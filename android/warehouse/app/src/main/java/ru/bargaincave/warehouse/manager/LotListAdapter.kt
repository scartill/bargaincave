package ru.bargaincave.warehouse.manager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.bargaincave.warehouse.R

class LotListAdapter: RecyclerView.Adapter<LotListAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView : TextView

        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_lot, viewGroup, false)
        /*
        val b = ItemLotBinding.inflate(LayoutInflater.from(viewGroup.context))
        val view = b.root
         */

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = "test"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = 10
}
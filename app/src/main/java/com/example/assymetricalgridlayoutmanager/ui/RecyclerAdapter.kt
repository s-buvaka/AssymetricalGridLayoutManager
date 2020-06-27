package com.example.assymetricalgridlayoutmanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.assymetricalgridlayoutmanager.R
import kotlinx.android.synthetic.main.list_item_view.view.*

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private var items: List<Int> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateAdapter(_items: List<Int>) {
        items = _items
        notifyDataSetChanged()
    }

    class ViewHolder(private val rootView: View) : RecyclerView.ViewHolder(rootView) {

        fun bind(color: Int) {
            rootView.spanItem.setBackgroundColor(color)
            rootView.textView.text = adapterPosition.toString()
            rootView.setOnClickListener {
                Toast.makeText(
                    rootView.context,
                    "Click on $adapterPosition",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

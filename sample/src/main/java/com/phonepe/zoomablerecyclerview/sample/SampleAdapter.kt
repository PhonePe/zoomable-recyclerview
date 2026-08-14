package com.phonepe.zoomablerecyclerview.sample

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SampleAdapter(private val items: List<SampleItem>) :
    RecyclerView.Adapter<SampleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val background: FrameLayout = view.findViewById(R.id.itemBackground)
        val number: TextView = view.findViewById(R.id.itemNumber)
        val label: TextView = view.findViewById(R.id.itemLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sample, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(item.color)
        }
        holder.background.background = drawable
        holder.number.text = item.number.toString()
        holder.label.text = item.label
    }

    override fun getItemCount() = items.size
}

data class SampleItem(val number: Int, val color: Int, val label: String)

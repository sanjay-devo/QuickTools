package com.indiacybercafe.quicktools

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.indiacybercafe.quicktools.databinding.ItemTopToolBinding

class TopToolAdapter(
    private val tools: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<TopToolAdapter.TopToolViewHolder>() {

    private var typeface: android.graphics.Typeface? = null

    class TopToolViewHolder(val binding: ItemTopToolBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopToolViewHolder {
        if (typeface == null) {
            typeface = android.graphics.Typeface.createFromAsset(
                parent.context.assets,
                "fonts/Poppins-Bold.ttf"
            )
        }
        val binding = ItemTopToolBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TopToolViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopToolViewHolder, position: Int) {
        val tool = tools[position]
        holder.binding.tvToolName.text = tool.name
        holder.binding.ivToolIcon.setImageResource(tool.iconResId)
        holder.binding.tvToolName.typeface = typeface

        holder.itemView.setOnClickListener {
            onItemClick(tool)
        }
    }

    override fun getItemCount(): Int = tools.size
}
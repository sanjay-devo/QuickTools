package com.indiacybercafe.quicktools

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.indiacybercafe.quicktools.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var typeface: android.graphics.Typeface? = null

    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        if (typeface == null) {
            typeface = android.graphics.Typeface.createFromAsset(
                parent.context.assets,
                "fonts/Poppins-Bold.ttf"
            )
        }
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryName.text = category.name
        holder.binding.ivCategoryIcon.setImageResource(category.iconResId)
        holder.binding.tvCategoryName.typeface = typeface

        holder.itemView.setOnClickListener {
            onItemClick(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}
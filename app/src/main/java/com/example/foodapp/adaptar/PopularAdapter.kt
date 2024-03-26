package com.example.foodapp.adaptar

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.foodapp.DetailsActivity
import com.example.foodapp.databinding.PopularItemBinding

class PopularAdapter (private val items:List<String>,private val price:List<String>,private val image:List<Int>,private val requireContext:Context) : RecyclerView.Adapter<PopularAdapter.PouplerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PouplerViewHolder {
        return PouplerViewHolder(PopularItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

        override fun onBindViewHolder(holder: PouplerViewHolder, position: Int) { // Phần trả tiền xóa và tạo chuông
            val item = items[position]
            val images = image[position]
            val price = price[position]
            holder.bind(item,price, images)

            holder.itemView.setOnClickListener {
                val intent = Intent(requireContext, DetailsActivity::class.java)
                intent.putExtra("MenuItemName",item)
                intent.putExtra("MenuItemImage",images)
                requireContext.startActivity(intent)
            }
    }
    override fun getItemCount(): Int {
       return items.size
    }
    class PouplerViewHolder (private val binding: PopularItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val imagesView = binding.imageView6
        fun bind(item: String,price: String, images: Int) {
            binding.foodImage.text = item
            binding.PricePopular.text = price
            imagesView.setImageResource(images)
        }
    }
}
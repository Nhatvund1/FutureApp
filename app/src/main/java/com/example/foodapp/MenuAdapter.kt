package com.example.foodapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.MenuItemBinding
import com.example.foodapp.model.MenuItem

class MenuAdapter(
    private val menuItems:List<MenuItem>,
    private val requireContext: Context

) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int =  menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                   openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
        val menuItem = menuItems[position]
            // ý định mở chi tiết hoạt động và truyền dữ liệu
            val intent  = Intent(requireContext,DetailsActivity::class.java).apply {
                putExtra("MenuItemName",menuItem.foodName)
                putExtra("MenuItemImage",menuItem.foodImage)
                putExtra("MenuItemDescription",menuItem.foodDescription)
                putExtra("MenuItemIngredients",menuItem.foodIngredient)
                putExtra("MenuItemPrice",menuItem.foodPrice)
            }
            // bắt đầu kích hoạt details activity
            requireContext.startActivity(intent)
        }
        // đặt dữ liệu vào Recyclerview tên mặt hàng, giá cả, hình ảnh
        fun bind(position: Int) {
            val menuItem = menuItems[position]
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuPrice.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)


            }
        }

    }




}






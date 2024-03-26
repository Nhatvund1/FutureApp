package com.example.foodapp.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodapp.R
import com.example.foodapp.adaptar.BuyAgainAdapter
import com.example.foodapp.databinding.FragmentHistoryBinding
import com.example.foodapp.model.OrderDetails
import com.example.foodapp.recentOrderItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItem: MutableList<OrderDetails> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        // initialize  firebase Auth
        auth = FirebaseAuth.getInstance()
        // initialize  firebase database

        database = FirebaseDatabase.getInstance()
        // Truy xuất và hiển thị người dùng
        retrieveBuyHistory()
        // recent buy Button Click

        binding.recentbuyitem.setOnClickListener {
            seeItemsRecentBuy()
        }
        // setupRecyclerView()
        return binding.root
    }

    // function to see items recent buy
    private fun seeItemsRecentBuy() {
        listOfOrderItem.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), recentOrderItems::class.java)
            intent.putExtra("RecentBuyOrderItem", recentBuy)// Fix recentBuy -> listOfOrderItem
            startActivity(intent)
        }
    }

    // function to retrieve see items  buy history
    private fun retrieveBuyHistory() {
        binding.recentbuyitem.visibility = View.INVISIBLE
        // Lấy ID của người dùng hiện tại
        userId = auth.currentUser?.uid ?: ""
        val buyItemReference: DatabaseReference =
            database.reference.child("user").child(userId).child("BuyHistory")
        val shortingQuery = buyItemReference.orderByChild("currentTime")

        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItem.add(it)
                    }
                }

                listOfOrderItem.reverse()
                if (listOfOrderItem.isNotEmpty()) {
                    // display the most recent order details
                    setDataInRecentBuyItem()
                    setPreviousBuyItemRecyclerView()
                    // setup to recyclerview with previous order details
                }
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }

    // function to  display the most recent order details
    private fun setDataInRecentBuyItem() {
        binding.recentbuyitem.visibility = View.VISIBLE
        val recentOderItem = listOfOrderItem.firstOrNull()
        recentOderItem?.let {
            with(binding) {
                buyAgainFoodName.text = it.foodNames?.firstOrNull() ?: ""
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull() ?: ""
                val image = it.foodImages?.firstOrNull() ?: ""
                val uri = Uri.parse(image)
                Glide.with(requireContext()).load(uri).into(buyAgainFoodImage)
                // Hiển thị nút recentbuyitem

                listOfOrderItem.reverse()
                if(listOfOrderItem.isNotEmpty()){

                }

            }
        }
    }

    // function to setup  recyclerview with previous order details
    private fun setPreviousBuyItemRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for (i in 1 until listOfOrderItem.size) {
            listOfOrderItem[i].foodNames?.firstOrNull()?.let {
                buyAgainFoodName.add(it)
                listOfOrderItem[i].foodPrices?.firstOrNull()?.let {
                    buyAgainFoodPrice.add(it)
                    listOfOrderItem[i].foodImages?.firstOrNull()?.let {
                        buyAgainFoodImage.add(it)
                    }
                }
                // Thiết lập RecyclerView một lần ngoài vòng lặp
                val rv = binding.BuyAgainRecyclerView
                rv.layoutManager = LinearLayoutManager(requireContext())
                buyAgainAdapter = BuyAgainAdapter(
                    buyAgainFoodName,
                    buyAgainFoodPrice,
                    buyAgainFoodImage,
                    requireContext()
                )
                rv.adapter = buyAgainAdapter
            }
        }

    }
}


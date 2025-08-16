package com.example.firebasechatapp.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class CommonAdapter(
    private val inflate: (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
    private val onBind: (ViewBinding, Any, Int) -> Unit
) : ListAdapter<Any, CommonAdapter.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBind(holder.binding, getItem(position), position)
    }

    class ViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Any>() {
            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
                return oldItem == newItem
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
                oldItem == newItem
        }
    }
}
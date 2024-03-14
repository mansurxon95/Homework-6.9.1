package com.mansurdev.homework_691.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mansurdev.homework_691.databinding.ItemRcCategorySettingBinding
import com.mansurdev.homework_691.databinding.ItemRcWordBinding
import com.mansurdev.homework_691.databinding.ItemRcWordSettingBinding
import com.mansurdev.homework_691.entitiy.Category
import com.mansurdev.homework_691.entitiy.Word

class AdapterCategorySetting(var onClik: OnClik): ListAdapter<Category, AdapterCategorySetting.VH>(MyDiffUtill()) {

    inner class VH(var itemview : ItemRcCategorySettingBinding): RecyclerView.ViewHolder(itemview.root){


        fun onBind(user: Category){

            itemview.wordTrans.text = user.categoryName

            itemview.btnNext.setOnClickListener {
                onClik.view(user,itemview.btnNext)
            }
        }

    }

    class MyDiffUtill: DiffUtil.ItemCallback<Category>(){
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return  oldItem.categoryId == newItem.categoryId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return  oldItem.equals(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRcCategorySettingBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    interface OnClik{
        fun view(contact: Category,view: View){

        }
    }


}
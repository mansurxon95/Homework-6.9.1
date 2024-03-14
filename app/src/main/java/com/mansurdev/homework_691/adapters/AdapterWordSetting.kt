package com.mansurdev.homework_691.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mansurdev.homework_691.databinding.ItemRcWordBinding
import com.mansurdev.homework_691.databinding.ItemRcWordSettingBinding
import com.mansurdev.homework_691.entitiy.Word

class AdapterWordSetting(var onClik: OnClik): ListAdapter<Word, AdapterWordSetting.VH>(MyDiffUtill()) {

    inner class VH(var itemview : ItemRcWordSettingBinding): RecyclerView.ViewHolder(itemview.root){


        fun onBind(user: Word){

            itemview.word.text = user.word
            itemview.wordTrans.text = user.wordTranslation

            itemview.btnNext.setOnClickListener {
                onClik.view(user,itemview.btnNext)
            }
        }

    }

    class MyDiffUtill: DiffUtil.ItemCallback<Word>(){
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return  oldItem.wordId == newItem.wordId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return  oldItem.equals(newItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemRcWordSettingBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    interface OnClik{
        fun view(contact: Word,view: View){

        }
    }


}
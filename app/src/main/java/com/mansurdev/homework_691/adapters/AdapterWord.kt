package com.mansurdev.homework_691.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mansurdev.homework_691.databinding.ItemRcWordBinding
import com.mansurdev.homework_691.entitiy.Word

class AdapterWord(var onClik: OnClik): ListAdapter<Word, AdapterWord.VH>(MyDiffUtill()) {

    inner class VH(var itemview : ItemRcWordBinding): RecyclerView.ViewHolder(itemview.root){


        fun onBind(user: Word){

            itemview.word.text = user.word
            itemview.wordTrans.text = user.wordTranslation

            itemview.btnNext.setOnClickListener {
                onClik.view(user)
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
        return VH(ItemRcWordBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }

    interface OnClik{
        fun view(contact: Word){

        }
    }


}
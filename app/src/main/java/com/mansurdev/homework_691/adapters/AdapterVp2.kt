package com.mansurdev.homework_691.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mansurdev.homework_691.WordRcFragment

class AdapterVp2(var list:List<Int>, fragmentManager: FragmentActivity): FragmentStateAdapter(fragmentManager){
    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return  WordRcFragment.newInstance(list[position])
    }

}
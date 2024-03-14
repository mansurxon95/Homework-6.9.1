package com.mansurdev.homework_691

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.mansurdev.homework_691.adapters.AdapterWord
import com.mansurdev.homework_691.database.TranslationDatabase
import com.mansurdev.homework_691.databinding.FragmentWordRcBinding
import com.mansurdev.homework_691.entitiy.Word
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val ARG_PARAM1 = "param1"


class WordRcFragment : Fragment() {

    private var param1: Int? = null
    lateinit var binding: FragmentWordRcBinding
    lateinit var database: TranslationDatabase
    lateinit var adapterWord: AdapterWord

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordRcBinding.inflate(inflater,container,false)

        database = TranslationDatabase.getInstance(requireContext())
        adapterWord = AdapterWord(object :AdapterWord.OnClik{
            override fun view(contact: Word) {
                super.view(contact)
                var bundle = Bundle()
                bundle.putSerializable("word",contact)
                findNavController().navigate(R.id.wordviewFragment,bundle)

            }
        })
        binding.wordRcView.adapter = adapterWord

        database.WordDao().getAllWordcategoryId(param1!!)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({t->
                       adapterWord.submitList(t)

            },{et->

            })




        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: Int) =
            WordRcFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }
}
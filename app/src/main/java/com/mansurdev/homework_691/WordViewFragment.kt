package com.mansurdev.homework_691

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.mansurdev.homework_691.database.TranslationDatabase
import com.mansurdev.homework_691.databinding.FragmentWordViewBinding
import com.mansurdev.homework_691.entitiy.Word
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WordViewFragment : Fragment() {
    lateinit var binding: FragmentWordViewBinding
    lateinit var database: TranslationDatabase
    var word:Word?=null





    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordViewBinding.inflate(inflater,container,false)
        database = TranslationDatabase.getInstance(requireContext())

        word = arguments?.getSerializable("word") as Word

        binding.wordName.text = word?.word
        binding.wordTrans.text = word?.wordTranslation
        binding.image.setImageURI(word?.wordImage!!.toUri())
        if (word?.wordStatus==0){
            binding.btnFavorite.setImageResource(R.drawable.favorite)
        }else{
            binding.btnFavorite.setImageResource(R.drawable.favorite_select)
        }

        binding.btnFavorite.setOnClickListener {
            if (word?.wordStatus==0){
            database.WordDao().updateWordStatus(word?.wordId!!,1)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.btnFavorite.setImageResource(R.drawable.favorite_select)
                },{})

            }else{
                database.WordDao().updateWordStatus(word?.wordId!!,0)
                    .subscribeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        binding.btnFavorite.setImageResource(R.drawable.favorite)
                    },{})

            }
        }




        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfig = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfig)
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.title = "${word?.word}"
        var actvity = activity as MainActivity
        actvity.setSupportActionBar(binding.toolbar)
        super.onViewCreated(view, savedInstanceState)
    }


}
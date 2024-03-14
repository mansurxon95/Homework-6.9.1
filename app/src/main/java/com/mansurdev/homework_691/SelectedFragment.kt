package com.mansurdev.homework_691

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.mansurdev.homework_691.adapters.AdapterWord
import com.mansurdev.homework_691.database.TranslationDatabase
import com.mansurdev.homework_691.databinding.FragmentSelectedBinding
import com.mansurdev.homework_691.entitiy.Word
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.flowable.FlowableBlockingSubscribe.subscribe
import io.reactivex.schedulers.Schedulers


class SelectedFragment : Fragment(), MenuProvider {

    lateinit var binding: FragmentSelectedBinding
    lateinit var adapterWord: AdapterWord
    lateinit var database: TranslationDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
          findNavController().popBackStack()
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectedBinding.inflate(inflater,container,false)
        database = TranslationDatabase.getInstance(requireContext())

        adapterWord = AdapterWord(object :AdapterWord.OnClik{
            override fun view(contact: Word) {
                super.view(contact)
                var bundle = Bundle()
                bundle.putSerializable("word",contact)
                findNavController().navigate(R.id.wordviewFragment,bundle)
            }
        })
        binding.rcView.adapter = adapterWord
        database.WordDao().getAllWordStatus(1)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                       adapterWord.submitList(it)
            },{

            })


        binding.btnMain.setOnClickListener {
            findNavController().navigateUp()
        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.addMenuProvider(this,viewLifecycleOwner)
        binding.toolbar.title = "My Dictionary"
        var actvity = activity as MainActivity
        actvity.setSupportActionBar(binding.toolbar)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.editmenu,menu)

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId==R.id.edit_menu){
            findNavController().navigate(R.id.action_selectedFragment_to_settinsFragment )
            return true
        }
        return false
    }
}
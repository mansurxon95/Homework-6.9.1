package com.mansurdev.homework_691

import android.annotation.SuppressLint
import android.graphics.Color
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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.mansurdev.homework_691.adapters.AdapterVp2
import com.mansurdev.homework_691.database.TranslationDatabase
import com.mansurdev.homework_691.databinding.FragmentHomBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class HomFragment : Fragment(),MenuProvider {

    lateinit var binding: FragmentHomBinding
    lateinit var adapterVp2: AdapterVp2
    lateinit var database: TranslationDatabase
    var backnum = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled= false
            backnum++

            Handler(Looper.getMainLooper()).postDelayed({
                if (backnum!=2){
                    Toast.makeText(requireContext(), "Dasturdan chiqish uchun 2 marta bosing", Toast.LENGTH_SHORT).show()
                }
                this.isEnabled = true

            },500)

            // Handle the back button event
        }
    }

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomBinding.inflate(inflater,container,false)
        database = TranslationDatabase.getInstance(requireContext())


        val allCategoryId = database.CategoryDao().getAllCategoryId()

        adapterVp2 = AdapterVp2(allCategoryId,requireActivity())
        binding.vp2.adapter = adapterVp2
        TabLayoutMediator(binding.tabLayout,binding.vp2){tab, position->
            database.CategoryDao().getAllCategory()
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    tab.setText(it[position].categoryName)
                },{

                })

        }.attach()


        binding.btnHeard.setOnClickListener {
            findNavController().navigate(R.id.action_homFragment_to_selectedFragment)
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
           findNavController().navigate(R.id.action_homFragment_to_settinsFragment )
           return true
       }
        return false
    }


}
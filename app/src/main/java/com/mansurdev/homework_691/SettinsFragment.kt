package com.mansurdev.homework_691

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.mansurdev.homework_691.adapters.AdapterCategorySetting
import com.mansurdev.homework_691.database.TranslationDatabase
import com.mansurdev.homework_691.databinding.DialogContexLayoutBinding
import com.mansurdev.homework_691.databinding.DialogLayoutBinding
import com.mansurdev.homework_691.databinding.FragmentSettinsBinding
import com.mansurdev.homework_691.entitiy.Category
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SettinsFragment : Fragment(), MenuProvider {
    lateinit var binding: FragmentSettinsBinding
    lateinit var database: TranslationDatabase
    lateinit var adapter: AdapterCategorySetting



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
        binding = FragmentSettinsBinding.inflate(inflater,container,false)
        database = TranslationDatabase.getInstance(requireContext())

        adapter = AdapterCategorySetting(object : AdapterCategorySetting.OnClik{
            override fun view(contact: Category,view: View) {
                super.view(contact,view)
                val popupMenu: PopupMenu = PopupMenu(requireContext(),view)
                popupMenu.menuInflater.inflate(R.menu.contex_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.edit ->{
                            var alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setCancelable(false)
                            var dialogview = DialogLayoutBinding.inflate(LayoutInflater.from(binding.root.context),
                                null,false)
                            dialogview.categoryName.setText(contact.categoryName)
                            alertDialog.setView(dialogview.root)

                            var shov = alertDialog.show()

                            dialogview.btnCancel.setOnClickListener {
                                shov.dismiss()

                            }
                            dialogview.btnSave.setOnClickListener {
                                val toString = dialogview.categoryName.text.trim().toString()
                                if (toString.isNotEmpty()){

                                    database.CategoryDao().updateCategory(Category(contact.categoryId,toString))
                                        .subscribeOn(Schedulers.io())
                                        .subscribeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            shov.dismiss()
                                        },{

                                        })

                                }




                            }
                        }

                        R.id.delete ->{
                            var alertDialog = AlertDialog.Builder(requireContext())
                            alertDialog.setCancelable(false)
                            var dialogview = DialogContexLayoutBinding.inflate(LayoutInflater.from(binding.root.context),
                                null,false)
                            dialogview.massage.text = "Bu so’zni o’chirasizmi?"
                            alertDialog.setView(dialogview.root)

                            var shov = alertDialog.show()

                            dialogview.btnCancel.setOnClickListener {
                                shov.dismiss()

                            }
                            dialogview.btnSave.setOnClickListener {



                                database.CategoryDao().deleteCategory(contact.categoryId!!)
                                    .subscribeOn(Schedulers.io())
                                    .subscribeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        shov.dismiss()
                                    },{

                                    })


                            }

                        }
                             }
                    true
                })
                popupMenu.show()


            }
        })
        binding.rcView.adapter = adapter
        database.CategoryDao().getAllCategory()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.submitList(it)
            },{

            })


        binding.btnHeard.setOnClickListener {
            findNavController().navigate(R.id.action_settinsFragment_to_wordsFragment)
        }





        return binding.root
    }










    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.addMenuProvider(this,viewLifecycleOwner)
            val navController = findNavController()
            val appBarConfig = AppBarConfiguration(navController.graph)
            binding.toolbar.setupWithNavController(navController, appBarConfig)
            binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.title = "Settings"
        var actvity = activity as MainActivity
        actvity.setSupportActionBar(binding.toolbar)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.addmenu,menu)

    }

    @SuppressLint("CheckResult")
    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

        if (menuItem.itemId==R.id.add_menu){
            var alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setCancelable(false)
            var dialogview = DialogLayoutBinding.inflate(LayoutInflater.from(binding.root.context),
                null,false)
            alertDialog.setView(dialogview.root)
            var shov = alertDialog.show()
            dialogview.btnCancel.setOnClickListener {
                shov.dismiss()
            }

            dialogview.btnSave.setOnClickListener {
                var name = dialogview.categoryName.text.trim().toString()

                if (name.isNotEmpty()){
                    database.CategoryDao().insertCategory(Category(null,name))
                        .subscribeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            shov.dismiss()
                        },{

                        })

                }
            }





        }
        return false
    }
}
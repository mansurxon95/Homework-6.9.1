package com.mansurdev.homework_691

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.ContextMenu
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.camera.core.processing.SurfaceProcessorNode.In
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.mansurdev.homework_691.adapters.AdapterWordSetting
import com.mansurdev.homework_691.database.TranslationDatabase
import com.mansurdev.homework_691.databinding.DialogContexLayoutBinding
import com.mansurdev.homework_691.databinding.DialogLayoutBinding
import com.mansurdev.homework_691.databinding.FragmentWordsBinding
import com.mansurdev.homework_691.entitiy.Category
import com.mansurdev.homework_691.entitiy.Word
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
class WordsFragment : Fragment(), MenuProvider {
    lateinit var binding: FragmentWordsBinding
    lateinit var database: TranslationDatabase
    lateinit var adapter: AdapterWordSetting


    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordsBinding.inflate(inflater,container,false)
        database = TranslationDatabase.getInstance(requireContext())

        adapter = AdapterWordSetting(object : AdapterWordSetting.OnClik{
            override fun view(contact: Word,view: View) {
                super.view(contact,view)

                val popupMenu: PopupMenu = PopupMenu(requireContext(),view)
                popupMenu.menuInflater.inflate(R.menu.contex_menu,popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.edit ->{
                            var bundle = Bundle()
                            bundle.putInt("wordId",contact.wordId!!)
                            findNavController().navigate(R.id.action_wordsFragment_to_addWordFragment,bundle)
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



                                database.WordDao().deleteWord(contact.wordId!!)
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
        database.WordDao().getAllWord()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                adapter.submitList(it)
            },{

            })


        binding.btnMain.setOnClickListener {
            findNavController().navigateUp()
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

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId==R.id.add_menu){
            findNavController().navigate(R.id.action_wordsFragment_to_addWordFragment )
            return true
        }
        return false
    }
}
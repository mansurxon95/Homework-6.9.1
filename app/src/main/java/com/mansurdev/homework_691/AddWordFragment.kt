package com.mansurdev.homework_691

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.mansurdev.homework_691.database.TranslationDatabase
import com.mansurdev.homework_691.databinding.FragmentAddWordBinding
import com.mansurdev.homework_691.entitiy.Word
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AddWordFragment : Fragment() {
    lateinit var binding: FragmentAddWordBinding
    lateinit var database: TranslationDatabase
    var imageUri: Uri?=null
    var imageCapture: ImageCapture?=null
    val TAG = "CameaXApp"
    private val FILNAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"



    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddWordBinding.inflate(inflater,container,false)
        database = TranslationDatabase.getInstance(requireContext())

        var wordId = arguments?.getInt("wordId")
        if (wordId!=null){
            var word = database.WordDao().getAllWordId(wordId)
            binding.word.setText(word.word)
            binding.wordTrans.setText(word.wordTranslation)
            binding.image.setImageURI(word.wordImage?.toUri())
        }

        database.CategoryDao().getAllCategoryString()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it
                       val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,it)
                binding.categorySpinner.adapter = adapter
            },{})

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.image.setOnClickListener {
            addimage()
        }

        binding.btnSave.setOnClickListener {

            var word = binding.word.text.trim().toString()
            var wordTrans = binding.wordTrans.text.trim().toString()
            var category = binding.categorySpinner.selectedItemPosition


            if (word.isNotEmpty() && wordTrans.isNotEmpty()&&imageUri!=null){

                if (wordId!=null) {
                    database.WordDao()
                        .insertWord(Word(null, category, imageUri.toString(), word, wordTrans, 0))
                    findNavController().popBackStack()
                }else{
                    var word1 = database.WordDao().getAllWordId(wordId!!)

                    database.WordDao().updateWord(Word(word1.wordId, category, imageUri.toString(), word, wordTrans, word1.wordStatus))

                    findNavController().popBackStack()
                }
            }else Toast.makeText(
                requireContext(),
                "Barcha maydonlarni toldirish shart",
                Toast.LENGTH_SHORT
            ).show()

        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val navController = findNavController()
        val appBarConfig = AppBarConfiguration(navController.graph)
        binding.toolbar.setupWithNavController(navController, appBarConfig)
        binding.toolbar.setNavigationIcon(R.drawable.back)
        binding.toolbar.title = "So’z qo’shish"
        var actvity = activity as MainActivity
        actvity.setSupportActionBar(binding.toolbar)
        super.onViewCreated(view, savedInstanceState)
    }

    private fun addimage() {



        startcamera(android.Manifest.permission.CAMERA)



        var count = false

        binding.btnCameraFront.setOnClickListener {


            if (count){
                lifecycleScope.launch {
                    startCamera(binding.previewViewCamera, CameraSelector.DEFAULT_BACK_CAMERA)
                }
                count = false
            }else{
                lifecycleScope.launch {
                    startCamera(binding.previewViewCamera,CameraSelector.DEFAULT_FRONT_CAMERA)
                }
                count = true
            }


        }

        binding.btnCamera.setOnClickListener {
            takePhoto()
            binding.layout1.visibility = View.VISIBLE
            binding.layout2.visibility = View.GONE
        }

        binding.btnCameraGallery.setOnClickListener {
            openGallery(android.Manifest.permission.READ_MEDIA_IMAGES)
            binding.layout1.visibility = View.VISIBLE
            binding.layout2.visibility = View.GONE
        }



    }


    private fun startcamera(permission: String) {
        if (!checkReadContactsPermission(binding.root.context, permission))
        {
            requestPermissions(permission)

        }else{
            binding.layout1.visibility = View.GONE
            binding.layout2.visibility = View.VISIBLE
            lifecycleScope.launch {
                startCamera(binding.previewViewCamera,CameraSelector.DEFAULT_BACK_CAMERA)
            }


        }

    }


    private fun openGallery(permission: String) {

        if (!checkReadContactsPermission(binding.root.context, permission))
        {
            requestPermissions(permission)

        }else {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
    }

    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                //qaytaradigan qiymat URI
                imageUri = data?.data
                binding.image.setImageURI(imageUri)
            }
        }


    private suspend fun startCamera(previewViewCamera: PreviewView, cameraSelector: CameraSelector){
        val cameraProvider = ProcessCameraProvider.getInstance(binding.root.context).await()

        val preview = Preview.Builder().build()

        preview.setSurfaceProvider(previewViewCamera.surfaceProvider) // TODO: 111111

        imageCapture = ImageCapture.Builder().build()


        try {

            cameraProvider.unbindAll()
            var camera = cameraProvider.bindToLifecycle(
                this,cameraSelector,preview,imageCapture
            )

        } catch (e:Exception){
            Toast.makeText(binding.root.context, "UseCase-ni ulash amalga oshmadi", Toast.LENGTH_SHORT).show()
        }

    }





    fun takePhoto(){

        val name = SimpleDateFormat(FILNAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())


        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            if (Build.VERSION.SDK_INT> Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"Pictures/CameraX-Image")
            }else{
                // ???
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(binding.root.context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()


        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback{

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "suratga olishdagi xatolik: ${exception.message}",exception )
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val msg = "Suratga olish muvaffaqiyatli yakunlandi${outputFileResults.savedUri}"
                    imageUri = outputFileResults.savedUri
                    binding.image.setImageURI(outputFileResults.savedUri)       // TODO: o'rnatiladigan rasim joyini tanlang
                    Toast.makeText(binding.root.context, "$msg", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "$msg")
                }



            }
        )

    }



    fun checkReadContactsPermission(context: Context, permission: String): Boolean {
        if (Build.VERSION.SDK_INT<33&&(
                    permission==android.Manifest.permission.READ_MEDIA_AUDIO||
                            permission==android.Manifest.permission.READ_MEDIA_VIDEO||
                            permission==android.Manifest.permission.READ_MEDIA_IMAGES)){

       return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        }else return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermissions(permission: String){
        if (Build.VERSION.SDK_INT<33&&(
                    permission==android.Manifest.permission.READ_MEDIA_AUDIO||
                            permission==android.Manifest.permission.READ_MEDIA_VIDEO||
                            permission==android.Manifest.permission.READ_MEDIA_IMAGES)){
            requestPermissions2(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }else requestPermissions2(permission)
    }


    private fun requestPermissions2(permission: String) {

        // pastdagi satr joriy faoliyatda ruxsat so'rash uchun ishlatiladi.
        // bu usul ish vaqti ruxsatnomalarida xatoliklarni hal qilish uchun ishlatiladi
        Dexter.withContext(binding.root.context)
            // pastdagi satr ilovamizda talab qilinadigan ruxsatlar sonini so'rash uchun ishlatiladi.
            .withPermissions(
                permission
            )
            // ruxsatlarni qo'shgandan so'ng biz tinglovchi bilan usulni chaqiramiz.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // bu usul barcha ruxsatlar berilganda chaqiriladi
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // hozir ishlayapsizmi
                        Toast.makeText(binding.root.context, "Barcha ruxsatlar berilgan, xarakatni davom etirishingiz mumki", Toast.LENGTH_SHORT).show()

                        if (checkReadContactsPermission(binding.root.context,permission)) {

                            if (permission==android.Manifest.permission.CAMERA){
                                startcamera(permission)
                            }else if (permission==android.Manifest.permission.READ_MEDIA_IMAGES||permission==android.Manifest.permission.READ_EXTERNAL_STORAGE){
                                openGallery(permission)
                            }
                            // Ruxsat berilgan, kontakt ma'lumotlariga kirishingiz mumkin
                            // Kerakli harakatlar tugallanishi uchun shu yerga kod yozing
                        }

                    }
                    // har qanday ruxsatni doimiy ravishda rad etishni tekshiring
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // ruxsat butunlay rad etilgan, biz foydalanuvchiga dialog xabarini ko'rsatamiz.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(list: List<PermissionRequest>, permissionToken: PermissionToken) {
                    // foydalanuvchi ba'zi ruxsatlarni berib, ba'zilarini rad qilganda bu usul chaqiriladi.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener {
                // biz xato xabari uchun tost xabarini ko'rsatmoqdamiz.
                Toast.makeText(binding.root.context, "Error occurred! ", Toast.LENGTH_SHORT).show()
            }
            // pastdagi satr bir xil mavzudagi ruxsatlarni ishga tushirish va ruxsatlarni tekshirish uchun ishlatiladi
            .onSameThread().check()

    }

    // quyida poyabzal sozlash dialog usuli
    // dialog xabarini ko'rsatish uchun ishlatiladi.
    private fun showSettingsDialog() {
        // biz ruxsatlar uchun ogohlantirish dialogini ko'rsatmoqdamiz
        val builder = AlertDialog.Builder(binding.root.context)

        // pastdagi satr ogohlantirish dialogining sarlavhasidir.
        builder.setTitle("Need Permissions")

        // pastdagi satr bizning muloqotimiz uchun xabardir
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            // bu usul musbat tugmani bosganda va shit tugmasini bosganda chaqiriladi
            // biz foydalanuvchini ilovamizdan ilovamiz sozlamalari sahifasiga yo'naltirmoqdamiz.
            dialog.cancel()
            // quyida biz foydalanuvchini qayta yo'naltirish niyatimiz.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", null, "SetingsFragment") // TODO:
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            // bu usul foydalanuvchi salbiy tugmani bosganda chaqiriladi.
            dialog.cancel()
        }
        // dialog oynamizni ko'rsatish uchun quyidagi qatordan foydalaniladi
        builder.show()
    }



}
package com.sttptech.toshiba_lighting.Fragment.Scene.SceneCreate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.Adapter.DividerItemDecorator
import com.sttptech.toshiba_lighting.Adapter.SceneDeviceAdapter
import com.sttptech.toshiba_lighting.AppUtil.PermissionUtil
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.DialogFragment.AddPicture.AddPictureFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentSceneCreateBinding
import com.yalantis.ucrop.UCrop
import dev.weiqi.resof.colorIntOf
import dev.weiqi.resof.drawableOf
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class SceneCreateFragment : Fragment(), View.OnClickListener {
    
    
    companion object {
        private const val REQ_CODE = "REQ_CODE"
        
        private const val REQ_TAKE_PICTURE = 0
        private const val REQ_PICK_PICTURE = 1
        private const val REQ_CROP_PICTURE = 2
        
        private const val TAKE_PICTURE_NAME = "temp_picture.jpg"
        private const val CROP_PICTURE_NAME = "picture_cropped.jpg"
        
        const val SCENE_CREATE_FINISH = "scene_create_finish"
    }
    
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickerLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    
    private lateinit
    var vm: SceneCreateViewModel
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        clearTampFile()
        
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted)
                intentTakePicture()
            
        }
        
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { result ->
            if (result)
                crop(vm.imageUri.value)
        }
        
        pickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { resultUri -> crop(resultUri) }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[SceneCreateViewModel::class.java]
    }
    
    private lateinit var vb: FragmentSceneCreateBinding
    private var devAdapter: SceneDeviceAdapter = SceneDeviceAdapter()
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentSceneCreateBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        /** recycler view */
        vb.sceneCreateRecyclerView.run {
            layoutManager = LinearLayoutManager(context)
            adapter = devAdapter
            addItemDecoration(
                DividerItemDecorator(
                    drawableOf(R.drawable.divider_device_list)
                )
            )
        }
        
        setListener()
        observerVM()
    }
    
    /** Clear the temporary file */
    private fun clearTampFile() {
        val file = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        
        val takeFile: File = File(file, TAKE_PICTURE_NAME)
        val cropFile: File = File(file, CROP_PICTURE_NAME)
        if (takeFile.exists()) takeFile.delete()
        if (cropFile.exists()) cropFile.delete()
    }
    
    private fun intentTakePicture() {
        // 指定存擋路徑
        var file = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File(file, TAKE_PICTURE_NAME)
        val contentUri =
            FileProvider.getUriForFile(
                requireActivity(),
                requireActivity().packageName.toString() + ".provider",
                file
            )
        cameraLauncher.launch(contentUri)
        vm.imageUri.value = contentUri
    }
    
    private fun crop(sourceImageUri: Uri?) {
        if (sourceImageUri == null) return
        
        var file = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File(file, CROP_PICTURE_NAME)
        val destinationUri = Uri.fromFile(file)
        UCrop.of(sourceImageUri, destinationUri)
            .withAspectRatio(1f, 1f) // 設定裁減比例
            //                .withMaxResultSize(480, 270) // 設定結果尺寸不可超過指定寬高
            .start(
                requireActivity(),
                this,
                REQ_CROP_PICTURE
            )
        vm.imageUri.value = destinationUri
    }
    
    private fun handleCropResult(intent: Intent?) {
        if (intent == null) return
        
        val resultUri = UCrop.getOutput(intent) ?: return
        
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                BitmapFactory.decodeStream(
                    requireActivity().contentResolver.openInputStream(resultUri)
                )
            } else {
                val source = ImageDecoder.createSource(requireActivity().contentResolver, resultUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        
        if (bitmap == null) return
        
        val out = ByteArrayOutputStream()
        
        // compress image, because too large can't upload to server.
        bitmap = compressImage(bitmap)
//        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        
        vm.imageUri.value = resultUri
//        vm.imageByte.value = out.toByteArray()
    }
    
    private fun compressImage(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos) // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        var options = 90
        while (baos.toByteArray().size / 1024 > 1000) { // 循环判断如果压缩后图片是否大于1000kb, 大于继续压缩
            baos.reset() // 重置baos即清空baos
            image.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            ) // 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10 // 每次都减少10
        }
        
        Logger.i("compress image size to: ${baos.size() / 1024} KB")
        
        vm.imageByte.value = baos.toByteArray()
        
        val isBm =
            ByteArrayInputStream(baos.toByteArray()) // 把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        
        if (resultCode != Activity.RESULT_OK) return
        
        if (requestCode == REQ_CROP_PICTURE)
            handleCropResult(data)
        
    }
    
    override fun onClick(v: View?) {
        
        if (vm.touchBlocker.onTouch().not()) return
        
        when (v?.id) {
            
            /** back */
            R.id.sceneCreate_tvBack -> {
                Navigation.findNavController(requireView()).popBackStack()
            }
            
            /** save */
            R.id.sceneCreate_tvSave -> {
                if (vb.sceneCreateEtName.text.toString().trim().isEmpty()) {
                    Snackbar.make(requireView(), R.string.nameIsNotEmpty, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                        .show()
                    return
                }
                
                BaseApplication.loadingView
                    .show(parentFragmentManager, null)
                
                Thread {
                    val result = vm.saveScene(vb.sceneCreateEtName.text.toString())
                    requireActivity().runOnUiThread {
                        if (result) {
                            Snackbar.make(
                                requireView(),
                                R.string.sceneInsertSuccess,
                                Snackbar.LENGTH_SHORT
                            )
                                .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                .show()
                            
                            /** pop back stack & notify result */
                            findNavController().apply {
                                previousBackStackEntry!!
                                    .savedStateHandle.set(SCENE_CREATE_FINISH, true)
                            }
                                .popBackStack()
                        } else
                            Snackbar.make(
                                requireView(),
                                R.string.sceneInsertFail,
                                Snackbar.LENGTH_SHORT
                            )
                                .setBackgroundTint(colorIntOf(R.color.snackBar_fail))
                                .show()
                        
                        BaseApplication.loadingView.dismiss()
                    }
                }.start()
            }
            
            /** camera */
            R.id.sceneCreate_ibtCamera -> {
                AddPictureFragment().apply {
                    callback = object : AddPictureFragment.ButtonClickCallback {
                        
                        override fun onTakePicture() {
                            
                            if (PermissionUtil.checkPermission(
                                    requireActivity(),
                                    PermissionUtil.PERMISSION_REQUEST_CAMERA
                                )
                            )
                                intentTakePicture()
                            else {
                                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                            
                            dismiss()
                        }
                        
                        override fun onPickPicture() {
                            pickerLauncher.launch("image/*")
                            dismiss()
                        }
                    }
                }.show(parentFragmentManager, null)
            }
            
        }
    }
    
    private fun observerVM() {
        
        vm.deviceList.observe(viewLifecycleOwner) {
            devAdapter.submitList(it)
        }
        
        vm.imageUri.observe(viewLifecycleOwner) {
            if (it == null) {
                vb.sceneCreateImg.setImageURI(null)
            } else {
                vb.sceneCreateImg.setImageURI(vm.imageUri.value)
            }
            
            try {
                val fileLength = vm.imageUri.value?.toFile()?.length() ?: return@observe
                val fileSize = fileLength / 1024
                
                Logger.i("uri to file size: \n$fileSize KB")
                
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    }
    
    private fun setListener() {
        vb.sceneCreateTvBack.setOnClickListener(this)
        vb.sceneCreateTvSave.setOnClickListener(this)
        vb.sceneCreateIbtCamera.setOnClickListener(this)
        
        devAdapter.callback = object : SceneDeviceAdapter.DeviceSelectedCallback {
            
            override fun onSelected(dev: Device) {
                if (vm.selectList.value!!.contains(dev)) return
                vm.selectList.value!!.add(dev)
                
                Logger.i(
                    "Select device info: " +
                            "\n${vm.selectList.value.toString()}"
                )
            }
            
            override fun onUnselected(dev: Device) {
                if (vm.selectList.value!!.contains(dev))
                    vm.selectList.value!!.remove(dev)
                
                Logger.i(
                    "Select device info: " +
                            "\n${vm.selectList.value.toString()}"
                )
            }
        }
    }
}
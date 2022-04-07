package com.sttptech.toshiba_lighting.Fragment.Scene.SceneEdit

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.orhanobut.logger.Logger
import com.sttptech.toshiba_lighting.Adapter.DividerItemDecorator
import com.sttptech.toshiba_lighting.Adapter.SceneDeviceAdapter
import com.sttptech.toshiba_lighting.AppUtil.AppKey
import com.sttptech.toshiba_lighting.AppUtil.AppKey.SCENE_EDIT_FINISH
import com.sttptech.toshiba_lighting.AppUtil.AppKey.SCENE_SEQ
import com.sttptech.toshiba_lighting.AppUtil.PermissionUtil
import com.sttptech.toshiba_lighting.Application.BaseApplication
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.DialogFragment.AddPicture.AddPictureFragment
import com.sttptech.toshiba_lighting.R
import com.sttptech.toshiba_lighting.databinding.FragmentSceneEditBinding
import com.yalantis.ucrop.UCrop
import dev.weiqi.resof.colorIntOf
import dev.weiqi.resof.drawableOf
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class SceneEditFragment : Fragment(), View.OnClickListener {
    
    private lateinit var cameraLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickerLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    
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
    
    private lateinit var vm: SceneEditViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(this)[SceneEditViewModel::class.java]
        vm.init(arguments?.getInt(SCENE_SEQ))
    }
    
    private lateinit var vb: FragmentSceneEditBinding
    private val devAdapter = SceneDeviceAdapter(SceneDeviceAdapter.Action.EDIT)
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vb = FragmentSceneEditBinding.inflate(inflater, container, false)
        return vb.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // recycler view
        vb.sceneEditFragmentRecyclerView.run {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(
                DividerItemDecorator(
                    drawableOf(R.drawable.divider_device_list)
                )
            )
            adapter = devAdapter
        }
        
        setListener()
        observerVM()
    }
    
    /** Clear the temporary file */
    private fun clearTampFile() {
        val file = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        
        val takeFile = File(file, AppKey.TAKE_PICTURE_NAME)
        val cropFile = File(file, AppKey.CROP_PICTURE_NAME)
        if (takeFile.exists()) takeFile.delete()
        if (cropFile.exists()) cropFile.delete()
    }
    
    private fun intentTakePicture() {
        // 指定存擋路徑
        var file = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File(file, AppKey.TAKE_PICTURE_NAME)
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
        file = File(file, AppKey.CROP_PICTURE_NAME)
        val destinationUri = Uri.fromFile(file)
        UCrop.of(sourceImageUri, destinationUri)
            .withAspectRatio(1f, 1f) // 設定裁減比例
            //                .withMaxResultSize(480, 270) // 設定結果尺寸不可超過指定寬高
            .start(
                requireActivity(),
                this,
                AppKey.REQ_CROP_PICTURE
            )
    }
    
    private fun handleCropResult(intent: Intent?) {
        if (intent == null) return
        
        val resultUri = UCrop.getOutput(intent) ?: return
        vm.imageUri.postValue(resultUri)
        
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
        compressImage(bitmap)
    }
    
    private fun compressImage(image: Bitmap) {
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
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        
        if (resultCode != Activity.RESULT_OK) return
        
        if (requestCode == AppKey.REQ_CROP_PICTURE)
            handleCropResult(data)
        
    }
    
    private fun setListener() {
        vb.sceneEditFragmentTvBack.setOnClickListener(this)
        vb.sceneEditFragmentTvSave.setOnClickListener(this)
        vb.sceneEditFragmentIbtCamera.setOnClickListener(this)
        
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
    
    private fun observerVM() {
        
        // editText
        vm.scene.observe(viewLifecycleOwner) { vb.sceneEditFragmentEtName.setText(it.name) }
        
        // image view
        vm.imageUri.observe(viewLifecycleOwner) {
            if (it == null) {
                vb.sceneEditFragmentImg.setImageURI(null)
            } else {
                vb.sceneEditFragmentImg.setImageURI(it)
            }
            
            try {
                val fileLength = it.toFile().length()
                val fileSize = fileLength / 1024
                
                Logger.i("uri to file size: \n$fileSize KB")
                
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        
        // recycler view list item
        vm.deviceList.observe(viewLifecycleOwner) {
            devAdapter.submitList(it)
        }
        
        vm.selectList.observe(viewLifecycleOwner) {
            devAdapter.selectList = it.toList()
        }
    }
    
    override fun onClick(v: View?) {
        
        if (vm.touchBlocker.onTouch().not()) return
        
        when (v?.id) {
            
            /** back */
            R.id.sceneEditFragment_tvBack -> {
                findNavController().popBackStack()
            }
            
            /** save */
            R.id.sceneEditFragment_tvSave -> {
                
                val oldName = vm.scene.value?.name ?: return
                val newName = vb.sceneEditFragmentEtName.text.toString().trim()
                
                BaseApplication.loadingView.show(parentFragmentManager, null)
                Thread {
                    val result = vm.saveScene(
                        if (oldName != newName) newName else null
                    )
                    
                    activity?.runOnUiThread {
                        if (result) {
                            Snackbar.make(
                                requireView(),
                                R.string.sceneEditSuccess,
                                Snackbar.LENGTH_SHORT
                            )
                                .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                .show()
                            
                            findNavController().apply {
                                previousBackStackEntry!!
                                    .savedStateHandle.set(SCENE_EDIT_FINISH, true)
                            }
                                .popBackStack()
                        } else {
                            Snackbar.make(
                                requireView(),
                                R.string.sceneEditFail,
                                Snackbar.LENGTH_SHORT
                            )
                                .setBackgroundTint(colorIntOf(R.color.snackBar_success))
                                .show()
                        }
                        BaseApplication.loadingView.dismiss()
                    }
                }.start()
            }
            
            /** camera */
            R.id.sceneEditFragment_ibtCamera -> {
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
}
package com.livechat.view.media_viewer

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivityMediaViewerBinding
import com.livechat.extension.checkPermissions
import com.livechat.extension.downloadFile
import com.livechat.extension.showToast
import com.livechat.util.PermissionsUtil
import com.livechat.view.choose_media.fragment.ImageFragment
import com.livechat.view.choose_media.fragment.VideoFragment
import dagger.hilt.android.AndroidEntryPoint


/**
 * User: Quang Phúc
 * Date: 2023-05-03
 * Time: 05:10 PM
 */
@AndroidEntryPoint
class MediaViewerActivity : BaseActivity() {

    companion object {
        const val KEY_URL = "url"
        const val KEY_FILE_NAME = "fileName"
        const val KEY_TYPE = "type"
    }

    private lateinit var binding: ActivityMediaViewerBinding

    private var imageFragment: ImageFragment? = null
    private var videoFragment: VideoFragment? = null

    private var url = ""
    private var fileName = ""
    private var type = ""

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions.entries.all {
            it.value
        }
        if (isGranted) {
            downloadFile(url, fileName)
            showToast("Can download file")
        } else {
            showToast("Can't download file")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {
        url = intent.getStringExtra(KEY_URL) ?: ""
        fileName = intent.getStringExtra(KEY_FILE_NAME) ?: ""
        type = intent.getStringExtra(KEY_TYPE) ?: ""

        if (url.isBlank() || fileName.isBlank() || type.isBlank()) {
            return
        }

        if (type == Constants.IMAGE) {
            imageFragment = ImageFragment.newInstance(url)
            imageFragment?.let {
                supportFragmentManager.beginTransaction().replace(R.id.fragmentMedia, it).commit()
            }
        } else if (type == Constants.VIDEO) {
            videoFragment = VideoFragment.newInstance(url)
            videoFragment?.let {
                supportFragmentManager.beginTransaction().replace(R.id.fragmentMedia, it).commit()
            }
        }
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgSave.setOnClickListener {
            if (checkPermissions(PermissionsUtil.getStoragePermissions())) {
                downloadFile(url, fileName)
                showToast(R.string.start_download)
            } else {
                requestPermissionsLauncher.launch(PermissionsUtil.getStoragePermissions())
            }
        }

        binding.imgShare.setOnClickListener {

        }
    }

    override fun observeViewModel() {

    }
}
package com.livechat.view.choose_media

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.livechat.R
import com.livechat.base.BaseActivity
import com.livechat.databinding.ActivityChooseMediaBinding
import com.livechat.extension.getSimpleName
import com.livechat.extension.gone
import com.livechat.extension.showToast
import com.livechat.extension.toJson
import com.livechat.extension.visible
import com.livechat.model.FileModel
import com.livechat.view.choose_media.fragment.ChooseMediaResultFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-04-27
 * Time: 12:03 AM
 */
@AndroidEntryPoint
class ChooseMediaActivity : BaseActivity() {

    companion object{
        const val KEY_ITEMS = "ITEMS"
    }

    private lateinit var viewModel: ChooseMediaViewModel
    private lateinit var binding: ActivityChooseMediaBinding

    private var adapter: ChooseMediaAdapter? = null

    private var chooseMediaResultFragment: ChooseMediaResultFragment? = null

    private var media = ArrayList<FileModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ChooseMediaViewModel::class.java]
        binding = ActivityChooseMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        viewModel.getAllMedia()
    }

    override fun initView() {

    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.cvChoose.setOnClickListener {
            viewModel.checkFileSize()
        }
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                ChooseMediaState.Status.GET_MEDIA_SUCCESS -> {
                    media = it.data as ArrayList<FileModel>
                    adapter = ChooseMediaAdapter(this, media) { fileModel, position ->
                        viewModel.chooseMedia(position)
                    }
                    binding.rcMedia.adapter = adapter
                }

                ChooseMediaState.Status.CHOOSE_MEDIA_SUCCESS -> {
                    val position = it.data as Int
                    adapter?.notifyItemChanged(position)

                    val count = countChooseItem()
                    if (count == 0) {
                        binding.cvChoose.gone()
                    } else {
                        binding.tvCount.text = count.toString()
                        binding.cvChoose.visible()
                    }
                }

                ChooseMediaState.Status.CHECK_FILE_SIZE_SUCCESS -> {
                    chooseMediaResultFragment = ChooseMediaResultFragment.newInstance(media)
                    chooseMediaResultFragment?.let { fragment ->
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentResult, fragment)
                            .addToBackStack(ChooseMediaResultFragment.getSimpleName())
                            .commit()
                    }
                }

                ChooseMediaState.Status.CHECK_FILE_SIZE_ERROR -> {
                    showToast(R.string.maximum_total_file_size)
                }
            }
        }
    }

    private fun countChooseItem(): Int {
        var count = 0
        for (i in media) {
            if (i.isSelected) {
                count++
            }
        }
        return count
    }

    fun chooseMedia() {
        val intent = Intent()
        val chooseMedia = ArrayList<FileModel>()
        for (i in media) {
            if (i.isSelected) {
                chooseMedia.add(i)
            }
        }
        intent.putExtra(KEY_ITEMS, chooseMedia.toJson())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
package com.livechat.view.choose_media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentChooseMediaResultBinding
import com.livechat.extension.toJson
import com.livechat.model.FileModel
import com.livechat.view.choose_media.ChooseMediaActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * User: Quang Phúc
 * Date: 2023-05-01
 * Time: 10:57 PM
 */
@AndroidEntryPoint
class ChooseMediaResultFragment : BaseFragment(R.layout.fragment_choose_media_result) {

    companion object {
        private const val KEY_ITEMS = "ITEMS"

        fun newInstance(items: ArrayList<FileModel>): ChooseMediaResultFragment {
            val bundle = Bundle()
            bundle.putString(KEY_ITEMS, items.toJson())
            val fragment = ChooseMediaResultFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentChooseMediaResultBinding

    private var chooseMediaResultAdapter: ChooseMediaResultAdapter? = null

    private var items = ArrayList<FileModel>()
    private val fragments = ArrayList<Fragment>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseMediaResultBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun initView() {
        if (context == null || arguments == null) {
            return
        }

        val json = requireArguments().getString(KEY_ITEMS) ?: ""
        if (json.isBlank()) {
            return
        }

        val type = object : TypeToken<ArrayList<FileModel>>() {}.type
        items = Gson().fromJson(json, type)

        chooseMediaResultAdapter = ChooseMediaResultAdapter(this)
        for (i in items) {
            if (!i.isSelected) {
                continue
            }

            if (i.type == FileModel.Type.IMAGE) {
                val fragment = ImageFragment.newInstance(i.path)
                fragments.add(fragment)
                chooseMediaResultAdapter?.addFragment(fragment)
            } else if (i.type == FileModel.Type.VIDEO) {
                val fragment = VideoFragment.newInstance(i.path)
                fragments.add(fragment)
                chooseMediaResultAdapter?.addFragment(fragment)
            }
        }
        binding.viewPager2.isSaveEnabled = false
        binding.viewPager2.adapter = chooseMediaResultAdapter
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.tvDone.setOnClickListener {
            if (activity == null) {
                return@setOnClickListener
            }

            (activity as ChooseMediaActivity).chooseMedia()
        }

        binding.viewPager2.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                binding.tvCurrentPosition.text = "${position + 1}/${fragments.size}"
            }
        })
    }

    override fun observeViewModel() {

    }

    override fun onDestroy() {
        super.onDestroy()
        for (i in fragments) {
            if (i is VideoFragment) {
                i.releaseVideo()
            }
        }
    }
}
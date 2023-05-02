package com.livechat.view.choose_media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentImageBinding

/**
 * User: Quang Phúc
 * Date: 2023-05-02
 * Time: 10:25 AM
 */
class ImageFragment : BaseFragment(R.layout.fragment_image) {

    companion object {
        private const val KEY_PATH = "PATH"

        fun newInstance(path: String): ImageFragment {
            val bundle = Bundle()
            bundle.putString(KEY_PATH, path)
            val fragment = ImageFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentImageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageBinding.inflate(layoutInflater)
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

        val path = requireArguments().getString(KEY_PATH) ?: ""
        if (path.isBlank()) {
            return
        }

        Glide.with(requireContext()).load(path).placeholder(R.drawable.ic_android).into(binding.img)
    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }
}
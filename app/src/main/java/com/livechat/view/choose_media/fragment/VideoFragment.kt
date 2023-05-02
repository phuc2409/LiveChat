package com.livechat.view.choose_media.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentVideoBinding

/**
 * User: Quang Phúc
 * Date: 2023-05-02
 * Time: 10:26 AM
 */
class VideoFragment : BaseFragment(R.layout.fragment_video) {

    companion object {
        private const val KEY_PATH = "PATH"

        fun newInstance(path: String): VideoFragment {
            val bundle = Bundle()
            bundle.putString(KEY_PATH, path)
            val fragment = VideoFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentVideoBinding

    private var exoPlayer: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoBinding.inflate(layoutInflater)
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

        exoPlayer = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = exoPlayer
        exoPlayer?.addMediaItem(MediaItem.fromUri(path))
        exoPlayer?.prepare()
    }

    override fun handleListener() {

    }

    override fun observeViewModel() {

    }

    private fun pauseVideo() {
        exoPlayer?.pause()
    }

    fun releaseVideo() {
        exoPlayer?.release()
    }

    override fun onPause() {
        super.onPause()
        pauseVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseVideo()
    }
}
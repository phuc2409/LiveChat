package com.livechat.view.maps.search_location

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.livechat.R
import com.livechat.base.BaseFragment
import com.livechat.databinding.FragmentSearchLocationBinding
import com.livechat.extension.gone
import com.livechat.extension.showKeyboard
import com.livechat.extension.visible
import com.livechat.model.api.TextSearchResponseModel
import com.livechat.view.maps.MapsActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

/**
 * User: Quang Phúc
 * Date: 2023-06-07
 * Time: 11:41 PM
 */
@AndroidEntryPoint
class SearchLocationFragment : BaseFragment(R.layout.fragment_search_location) {

    companion object {
        private const val SEARCH_DELAY = 1000L

        fun newInstance(): SearchLocationFragment {
            return SearchLocationFragment()
        }
    }

    private lateinit var viewModel: SearchLocationViewModel
    private lateinit var binding: FragmentSearchLocationBinding

    private var adapter: SearchLocationAdapter? = null
    private var results = ArrayList<TextSearchResponseModel.Result>()

    private var timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SearchLocationViewModel::class.java]
        binding = FragmentSearchLocationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun initView() {
        binding.etSearch.showKeyboard()
    }

    override fun handleListener() {
        binding.imgBack.setOnClickListener {
            getMapsActivity()?.pressBack()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timer.cancel()
            }

            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString()
                if (keyword.isBlank()) {
                    return
                }

                timer = Timer()
                timer.schedule(object : TimerTask() {

                    override fun run() {
                        viewModel.search(keyword)
                    }
                }, SEARCH_DELAY)
            }
        })
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                SearchLocationState.Status.LOADING -> {
                    showLoading()
                }

                SearchLocationState.Status.SEARCH_SUCCESS -> {
                    results = it.data as ArrayList<TextSearchResponseModel.Result>

                    if (results.isEmpty()) {
                        showNoResult()
                    } else {
                        showResult(results)
                    }
                }

                SearchLocationState.Status.SEARCH_ERROR -> {
                    showNoResult()
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visible()
        binding.tvNoResult.gone()
        binding.rvSearch.gone()
    }

    private fun showNoResult() {
        binding.progressBar.gone()
        binding.tvNoResult.visible()
        binding.rvSearch.gone()
    }

    private fun showResult(result: ArrayList<TextSearchResponseModel.Result>) {
        adapter = SearchLocationAdapter(requireContext(), result) { resultModel, position ->
            getMapsActivity()?.showAddress(
                LatLng(
                    resultModel.geometry.location.lat,
                    resultModel.geometry.location.lng
                ), resultModel.formattedAddress
            )
            getMapsActivity()?.pressBack()
        }
        binding.rvSearch.adapter = adapter

        binding.progressBar.gone()
        binding.tvNoResult.gone()
        binding.rvSearch.visible()
    }

    private fun getMapsActivity(): MapsActivity? {
        return if (activity == null) {
            null
        } else {
            activity as MapsActivity
        }
    }
}

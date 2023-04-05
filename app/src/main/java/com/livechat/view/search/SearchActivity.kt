package com.livechat.view.search

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import com.livechat.base.BaseActivity
import com.livechat.common.Constants
import com.livechat.databinding.ActivitySearchBinding
import com.livechat.extension.gone
import com.livechat.extension.showToast
import com.livechat.extension.toJson
import com.livechat.extension.visible
import com.livechat.model.UserModel
import com.livechat.view.chat.ChatActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * User: Quang Phúc
 * Date: 2023-04-03
 * Time: 01:01 AM
 */
@AndroidEntryPoint
class SearchActivity : BaseActivity() {

    companion object {
        private const val SEARCH_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: SearchViewModel

    private var adapter: SearchAdapter? = null
    private var users = ArrayList<UserModel>()

    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
    }

    override fun initView() {

    }

    override fun handleListener() {
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
                        viewModel.findUsers(keyword)
                    }
                }, SEARCH_DELAY)
            }
        })
    }

    override fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it.status) {

                SearchState.Status.LOADING -> {
                    showLoading()
                }

                SearchState.Status.SEARCH_SUCCESS -> {
                    users = it.data as ArrayList<UserModel>

                    if (users.isEmpty()) {
                        showNoResult()
                    } else {
                        showResult(users)
                    }
                }

                SearchState.Status.SEARCH_ERROR -> {
                    val e = it.data as Exception

                    showNoResult()
                    e.message?.let { message ->
                        showToast(message)
                    }
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

    private fun showResult(users: ArrayList<UserModel>) {
        adapter = SearchAdapter(this, users) { userModel, position ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Constants.USER_MODEL, userModel.toJson())
            startActivity(intent)
        }
        binding.rvSearch.adapter = adapter

        binding.progressBar.gone()
        binding.tvNoResult.gone()
        binding.rvSearch.visible()
    }
}
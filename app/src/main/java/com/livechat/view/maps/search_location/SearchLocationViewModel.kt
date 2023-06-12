package com.livechat.view.maps.search_location

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.livechat.base.BaseViewModel
import com.livechat.model.api.TextSearchResponseModel
import com.livechat.repo.GoogleMapsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * User: Quang Phúc
 * Date: 2023-06-08
 * Time: 12:03 AM
 */
@HiltViewModel
class SearchLocationViewModel @Inject constructor(private val googleMapsRepo: GoogleMapsRepo) :
    BaseViewModel() {

    private val _state = MutableLiveData<SearchLocationState>()
    val state: LiveData<SearchLocationState> = _state

    private var textSearchResponseModel: TextSearchResponseModel? = null

    private var keyword = ""

    fun search(keyword: String) {
        this.keyword = keyword
        _state.postValue(SearchLocationState.loading())

        googleMapsRepo.getTextSearch(
            keyword = keyword,
            onSuccess = {
                textSearchResponseModel = it
                _state.postValue(SearchLocationState.searchSuccess(textSearchResponseModel?.results as ArrayList<TextSearchResponseModel.Result>))
            }, onError = {
                _state.postValue(SearchLocationState.searchError(it))
            })
    }

    fun searchNextPage() {
        googleMapsRepo.getTextSearch(
            keyword = keyword,
            pageToken = textSearchResponseModel?.nextPageToken ?: "",
            onSuccess = {
                textSearchResponseModel?.results?.addAll(it.results)
                textSearchResponseModel?.nextPageToken = it.nextPageToken
                _state.postValue(SearchLocationState.searchNextPageSuccess(textSearchResponseModel?.results as ArrayList<TextSearchResponseModel.Result>))
            }, onError = {
                _state.postValue(SearchLocationState.searchError(it))
            })
    }
}

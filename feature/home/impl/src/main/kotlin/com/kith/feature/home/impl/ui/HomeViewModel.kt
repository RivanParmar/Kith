package com.kith.feature.home.impl.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kith.core.model.data.Post
import com.kith.core.ui.KithPreviewData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
// TEMPORARY: This models what your database will return.
// Move this to :core:model when you set up your database.
data class HomeData(
    val balance: Int,
    val level: Int,
    val nextTierXp: Int,
    val posts: List<Post>
)

// TEMPORARY: This is your Data Layer.

// Move this class to your :core:data module when you integrate Room/Supabase.

class HomeRepository @Inject constructor() {
// In production, this Flow comes directly from your Room Database DAO.
// It emits data as fast as the database can read it from the local device.
    fun getHomeDataStream(): Flow<HomeData> = flow {
        emit(
            HomeData(
                balance = KithPreviewData.WALLET_BALANCE,
                level = KithPreviewData.WALLET_LEVEL,
                nextTierXp = KithPreviewData.NEXT_TIER_XP,
                posts = KithPreviewData.posts
            )
        )
    }
    suspend fun syncDataFromNetwork() {

// In 1-2 days, put your Supabase API call here.

// It will download new posts and save them into the Room database.

// You DO NOT return data here. Saving to Room automatically triggers

// the Flow above to emit the fresh data to your ViewModel!

    }

}


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()


// The UI State is now purely reactive. It listens to the database stream

// and maps the raw data directly into the Success UI state.
    val uiState: StateFlow<HomeUiState> = repository.getHomeDataStream()
        .map<HomeData, HomeUiState> { data ->
            HomeUiState.Success(
                currentBalance = data.balance,
                currentLevel = data.level,
                nextTierXp = data.nextTierXp,
                recentPosts = data.posts
            )
        }

        .catch { emit(HomeUiState.Error(it.message ?: "Database Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading // Shows spinner only until first DB read finishes
        )


    fun refreshPosts() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.syncDataFromNetwork()
            _isRefreshing.value = false
        }
    }
}
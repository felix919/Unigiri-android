package com.panmatsu.unigiri.scenes.deck

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panmatsu.unigiri.data.local.DeckDatabase
import com.panmatsu.unigiri.data.repository.DeckRepositoryImpl
import com.panmatsu.unigiri.domain.DeckInteractor
import com.panmatsu.unigiri.domain.DeleteDeckUseCase
import com.panmatsu.unigiri.domain.GetDecksUseCase
import com.panmatsu.unigiri.domain.SaveDeckUseCase
import com.panmatsu.unigiri.model.DeckModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DeckListViewModel(
    private val interactor: DeckInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeckListUiState())
    val uiState: StateFlow<DeckListUiState> = _uiState

    init {
        viewModelScope.launch {
            // Room の Flow なので保存/削除後も自動更新される
            interactor.getDecks()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { decks ->
                    _uiState.update { it.copy(decks = decks, error = null) }
                }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch {
            try {
                interactor.delete(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}

data class DeckListUiState(
    val decks: List<DeckModel> = emptyList(),
    val error: String? = null,
)

class DeckListViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dao = DeckDatabase.getInstance(context).deckDao()
        val repository = DeckRepositoryImpl(dao)
        val interactor = DeckInteractor(
            getDecksUseCase = GetDecksUseCase(repository),
            saveDeckUseCase = SaveDeckUseCase(repository),
            deleteDeckUseCase = DeleteDeckUseCase(repository),
        )

        return DeckListViewModel(interactor) as T
    }
}

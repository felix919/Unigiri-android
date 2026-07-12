package com.panmatsu.unigiri.scenes.deck

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panmatsu.unigiri.data.local.DeckDatabase
import com.panmatsu.unigiri.data.repository.DeckRepositoryImpl
import com.panmatsu.unigiri.domain.DeckInteractor
import com.panmatsu.unigiri.domain.DeckValidationState
import com.panmatsu.unigiri.domain.DeckValidator
import com.panmatsu.unigiri.domain.DeleteDeckUseCase
import com.panmatsu.unigiri.domain.GetDecksUseCase
import com.panmatsu.unigiri.domain.SaveDeckUseCase
import com.panmatsu.unigiri.model.DeckCardModel
import com.panmatsu.unigiri.model.DeckModel
import com.panmatsu.unigiri.model.ZutocaModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class DeckEditViewModel(
    private val deckInteractor: DeckInteractor,
    private val deckId: String?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeckEditUiState())
    val uiState: StateFlow<DeckEditUiState> = _uiState

    private var existingDeck: DeckModel? = null

    init {
        if (deckId != null) {
            // 保存済みスナップショットからの復元 (ネットワーク不要)
            viewModelScope.launch {
                deckInteractor.getDeck(deckId)?.let { deck ->
                    existingDeck = deck
                    _uiState.update {
                        it.copy(
                            deckName = deck.name,
                            selectedEntries = deck.cards.sortedBy { card -> card.sortOrder },
                            validation = DeckValidator.makeState(deck.cards),
                        )
                    }
                }
            }
        }
    }

    fun updateDeckName(name: String) {
        _uiState.update { it.copy(deckName = name) }
    }

    fun addCard(card: ZutocaModel) {
        val current = _uiState.value
        if (!current.validation.canAdd(card.id)) return

        val entries = current.selectedEntries.toMutableList()
        val index = entries.indexOfFirst { it.cardId == card.id }
        if (index >= 0) {
            entries[index] = entries[index].copy(count = entries[index].count + 1)
        } else {
            entries.add(DeckCardModel.from(card, count = 1, sortOrder = entries.size))
        }

        _uiState.update {
            it.copy(
                selectedEntries = entries,
                validation = DeckValidator.makeState(entries),
            )
        }
    }

    fun removeCard(cardId: String) {
        val entries = _uiState.value.selectedEntries.toMutableList()
        val index = entries.indexOfFirst { it.cardId == cardId }
        if (index < 0) return

        if (entries[index].count > 1) {
            entries[index] = entries[index].copy(count = entries[index].count - 1)
        } else {
            entries.removeAt(index)
        }

        _uiState.update {
            it.copy(
                selectedEntries = entries,
                validation = DeckValidator.makeState(entries),
            )
        }
    }

    fun save() {
        viewModelScope.launch {
            val current = _uiState.value
            val now = System.currentTimeMillis()

            val deck = DeckModel(
                id = existingDeck?.id ?: UUID.randomUUID().toString(),
                name = current.deckName.trim().ifEmpty { "新しいデッキ" },
                createdAt = existingDeck?.createdAt ?: now,
                updatedAt = now,
                cards = current.selectedEntries.mapIndexed { index, card ->
                    card.copy(sortOrder = index)
                },
            )

            try {
                deckInteractor.save(deck)
                _uiState.update { it.copy(isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(saveError = e.message) }
            }
        }
    }

    fun clearSaveError() {
        _uiState.update { it.copy(saveError = null) }
    }
}

data class DeckEditUiState(
    val deckName: String = "",
    val saveError: String? = null,
    val isSaved: Boolean = false,
    val selectedEntries: List<DeckCardModel> = emptyList(),
    val validation: DeckValidationState = DeckValidationState(),
)

class DeckEditViewModelFactory(
    private val context: Context,
    private val deckId: String?,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val dao = DeckDatabase.getInstance(context).deckDao()
        val deckRepository = DeckRepositoryImpl(dao)
        val deckInteractor = DeckInteractor(
            getDecksUseCase = GetDecksUseCase(deckRepository),
            saveDeckUseCase = SaveDeckUseCase(deckRepository),
            deleteDeckUseCase = DeleteDeckUseCase(deckRepository),
        )

        return DeckEditViewModel(deckInteractor, deckId) as T
    }
}

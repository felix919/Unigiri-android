package com.panmatsu.unigiri.scenes.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.panmatsu.unigiri.data.api.RetrofitClient
import com.panmatsu.unigiri.data.repository.SearchRepositoryImpl
import com.panmatsu.unigiri.domain.GetSearchResultUseCase
import com.panmatsu.unigiri.domain.SearchInteractor
import com.panmatsu.unigiri.model.SearchResultModel
import com.panmatsu.unigiri.model.ZutocaModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class SearchCondition(
    val keyword: String = "",
    val rare: Set<String> = mutableSetOf(),
    val cardType: Set<String> = mutableSetOf(),
    val type: Set<String> = mutableSetOf(),
    val power: Set<Int> = mutableSetOf(),
    val pack: String? = null,
    val song: String? = null,
    val costRange: IntRange = COST_RANGE_DEFAULT,
    val attackEnabled: Boolean = false,
    val attackType: Set<AttackType> = emptySet(),
    val attackRange: IntRange = ATTACK_RANGE_DEFAULT,
    val illustrator: String? = null,
) {
    // 攻撃力
    enum class AttackType {
        DAY, NIGHT
    }

    companion object {
        // POWER COSTのデフォルト範囲
        val COST_RANGE_DEFAULT = 0..10
        // 攻撃力のデフォルト範囲
        val ATTACK_RANGE_DEFAULT = 0..250
    }
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val interactor: SearchInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    // 検索条件
    private val _condition = MutableStateFlow(SearchCondition())
    val condition: StateFlow<SearchCondition> = _condition


    // APIから取得したデータ
    private val _rawResult = MutableStateFlow(SearchResultModel.createInstance())

    init {
        viewModelScope.launch {
            // 検索結果と条件をcombineで結合する
            combine(_rawResult, _condition) { raw, condition ->
                filterResult(raw, condition)
            }
                .debounce(300)
                .collect { filtered ->
                    _uiState.update {
                        it.copy(result = filtered)
                    }
                }
        }

        // 検索条件はデフォルトで検索する
        fetch()
    }

    fun updateKeyword(keyword: String) {
        _condition.update { it.copy(keyword = keyword) }
    }

    fun updateRare(rare: String) {
        _condition.update { current ->
            val newSet = current.rare.toMutableSet()
            newSet.add(rare)

            current.copy(rare = newSet)
        }
    }

    fun removeRare(rare: String) {
        _condition.update { current ->
            val newSet = current.rare.toMutableSet()
            newSet.remove(rare)

            current.copy(rare = newSet)
        }
    }

    fun updateCardType(cardType: String) {
        _condition.update { current ->
            val newSet = current.cardType.toMutableSet()
            newSet.add(cardType)

            current.copy(cardType = newSet)
        }
    }

    fun removeCardType(cardType: String) {
        _condition.update { current ->
            val newSet = current.cardType.toMutableSet()
            newSet.remove(cardType)

            current.copy(cardType = newSet)
        }
    }

    fun updateType(type: String) {
        _condition.update { current ->
            val newSet = current.type.toMutableSet()
            newSet.add(type)

            current.copy(type = newSet)
        }
    }

    fun removeType(type: String) {
        _condition.update { current ->
            val newSet = current.type.toMutableSet()
            newSet.remove(type)

            current.copy(type = newSet)
        }
    }

    fun updatePower(power: Int) {
        _condition.update { current ->
            val newSet = current.power.toMutableSet()
            newSet.add(power)

            current.copy(power = newSet)
        }
    }

    fun removePower(power: Int) {
        _condition.update { current ->
            val newSet = current.power.toMutableSet()
            newSet.remove(power)

            current.copy(power = newSet)
        }
    }


    fun updatePack(pack: String?) {
        _condition.update { current ->
            current.copy(pack = pack)
        }
    }

    fun updateSong(song: String?) {
        _condition.update { current ->
            current.copy(song = song)
        }
    }

    fun togglePowerEnabled() {
        _condition.update {
            if (it.attackEnabled) {
                // On -> Off
                it.copy(
                    attackEnabled = false,
                    attackType = emptySet(),
                )
            } else {
                // Off -> On
                it.copy(
                    attackEnabled = true
                )
            }
        }
    }

    fun updateAttackType(type: SearchCondition.AttackType) {
        _condition.update { current ->
            val newSet = current.attackType.toMutableSet()
            newSet.add(type)

            current.copy(attackType = newSet)
        }
    }

    fun removeAttackType(type: SearchCondition.AttackType) {
        _condition.update { current ->
            val newSet = current.attackType.toMutableSet()
            newSet.remove(type)

            current.copy(attackType = newSet)
        }
    }

    fun updateAttackRange(range: IntRange) {
        _condition.update {
            it.copy(attackRange = range)
        }
    }

    fun updateCostRange(cost: IntRange) {
        _condition.update {
            it.copy(costRange = cost)
        }
    }

    fun updateIllustrator(illustrator: String?) {
        _condition.update { current ->
            current.copy(illustrator = illustrator)
        }
    }

    private fun fetch() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // APIからカードを取得する
                val result = interactor.fetchSamples()

                // 結果を保存する
                _rawResult.value = result

                // UIに反映する
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        packList = result.hits.map { zutoca -> zutoca.pack.first() }.distinct(),
                        songList = result.hits.map { zutoca -> zutoca.songs }.distinct().filter { it.isNotEmpty() },
                        illustrator = result.hits.map { it.illustrator }.distinct(),
                        error = null,
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    // 検索条件に応じてフィルタリングする
    private fun filterResult(
        raw: SearchResultModel,
        condition: SearchCondition,
    ): List<ZutocaModel> {
        return raw.hits.filter { zutoca ->
            (condition.keyword.isBlank() || listOf(
                zutoca.title, zutoca.effect, zutoca.songs
            ).any { it.contains(condition.keyword, ignoreCase = true) }) &&
                    (condition.rare.isEmpty() || zutoca.rare in condition.rare) &&
                    (condition.type.isEmpty() || zutoca.type in condition.type) &&
                    (condition.cardType.isEmpty() || zutoca.cardType in condition.cardType) &&
                    (condition.power.isEmpty() || zutoca.power in condition.power) &&
                    (condition.attackEnabled.not() ||
                            run {
                                val targets = when {
                                    condition.attackType.isEmpty() -> listOf(zutoca.noonAttack, zutoca.nightAttack)
                                    else -> condition.attackType.map {
                                        when (it) {
                                            SearchCondition.AttackType.DAY -> zutoca.noonAttack
                                            SearchCondition.AttackType.NIGHT -> zutoca.nightAttack
                                        }
                                    }
                                }

                                targets.any { attack ->
                                    attack in condition.attackRange
                                }
                            }) &&
                    (condition.pack.isNullOrEmpty() || condition.pack in zutoca.pack) &&
                    (condition.song.isNullOrEmpty() || condition.song in zutoca.songs) &&
                    (zutoca.cost in condition.costRange) &&
                    (condition.illustrator.isNullOrEmpty() || condition.illustrator in zutoca.illustrator)

        }
    }
}

data class SearchUiState(
    val isLoading: Boolean = false,
    val result: List<ZutocaModel> = emptyList(),
    val packList: List<String> = emptyList(),
    val songList: List<String> = emptyList(),
    val illustrator: List<String> = emptyList(),
    val error: String? = null,
)

class SearchViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val api = RetrofitClient.api
        val repository = SearchRepositoryImpl(api)
        val useCase = GetSearchResultUseCase(repository)
        val interactor = SearchInteractor(useCase)

        return SearchViewModel(interactor) as T
    }
}

// UI → VM
fun ClosedFloatingPointRange<Float>.toIntRange(): IntRange {
    return start.roundToInt()..endInclusive.roundToInt()
}

// VM → UI
fun IntRange.toFloatRange(): ClosedFloatingPointRange<Float> {
    return start.toFloat()..endInclusive.toFloat()
}

# Unigiri-android

ずとまよカード (ZUTOMAYO CARD) のファンアプリ「うにぎり」の Android 版。
兄弟プロジェクト `../Unigiri-ios` があり、**機能は両OSで同等に保つ**方針。片方に機能を追加したら、もう片方にも同じ仕様で実装するのが基本。

## ビルド・実行

```bash
./gradlew :app:assembleFreeDebug        # ビルド (flavor: free / paid)
./gradlew :app:testFreeDebugUnitTest    # ユニットテスト
adb install -r app/build/outputs/apk/free/debug/app-free-debug.apk
```

- 単一 `:app` モジュール、パッケージ `com.panmatsu.unigiri`
- Kotlin 2.1.0 / AGP 8.9.1 / compileSdk 36 / minSdk 31 / jvmTarget 1.8 / Compose BOM 2025.02.00
- **version catalog は無い** — 依存はすべて `app/build.gradle.kts` にインライン記述。プラグインのバージョンも app モジュール側で宣言 (compose / ksp が前例)
- `ZUTOMAYO_BEARER`(APIキー) と署名情報は `local.properties` から BuildConfig へ注入
- 100% Jetpack Compose (XMLレイアウト無し)

## アーキテクチャ (クリーンアーキテクチャ)

依存方向は scenes → domain ← data。

| パッケージ | 役割 | 命名 |
|---|---|---|
| `model/` | ドメインエンティティ (data class) | `*Model` |
| `domain/` | Repository インターフェース・UseCase・Interactor・Validator | `*Repository`, `*UseCase`, `*Interactor` |
| `data/api/` | Retrofit (`RetrofitClient` object + `ApiService`) | `*Request` |
| `data/response/` | API DTO (Gson `@SerializedName`) | `*Response` |
| `data/local/` | Room (DB / DAO / Entity / TypeConverter) | `*Entity`, `*Dao`, `*Database` |
| `data/repository/` | Repository 実装 (DTO/Entity ⇄ Model マッピング内包) | `*RepositoryImpl` |
| `scenes/<機能>/` | Composable 画面 + ViewModel | `*Screen`, `*ViewModel` |

代表的な縦のスライス: `SearchScreen` → `SearchViewModel` → `SearchInteractor` → `GetSearchResultUseCase` → `SearchRepository` → `SearchRepositoryImpl` → `ApiService`

### 規約・パターン

- **DIフレームワーク無し** (Hilt/Koin不使用)。ViewModel と同ファイル末尾に `*ViewModelFactory : ViewModelProvider.Factory` を置き、依存チェーンを手動組み立て。`*UiState` data class も同ファイル末尾に置く (`SearchViewModel.kt` がテンプレート)
- 状態管理: private `MutableStateFlow` + 公開 `StateFlow`、更新は `_uiState.update { it.copy(...) }`、画面側は `collectAsState()`。ロード/エラーは `when { isLoading / error != null / else }` 分岐
- ナビゲーションは2層:
  - `scenes/AppNavHost.kt` — ルート NavHost。タブバーを覆うフルスクリーン遷移先はここ (`cardDetail`, `deckEdit?deckId={deckId}` 等)。画面へはコールバック lambda で navigate を渡す
  - `scenes/MainScreen.kt` — Scaffold + NavigationBar + 内側 NavHost。タブは末尾の `sealed class Screen` (Battle / CardList / Deck / About)。FAB は `when (currentRoute)` で出し分け
- Room: `DeckDatabase.getInstance(context)` の companion object シングルトン (`RetrofitClient` と同方式、Application クラスは無い)。一覧取得は `Flow` (保存/削除後に自動更新される)、単発取得は suspend
- Interactor は UseCase を束ねる薄いファサード

### ハマりどころ

- **Room の親テーブル更新は `@Upsert` を使う** — `@Insert(onConflict = REPLACE)` は親行を delete+insert するため FK CASCADE が子行 (deck_cards) を巻き添え削除する (`DeckDao.saveDeckWithCards` 参照)
- カード画像は `Referer: https://zutomayocard.net/` ヘッダ必須 (Coil の `ImageRequest.Builder().addHeader`)。画像表示は `CardView` / `SelectedCardCell` を再利用すること
- **触ってはいけない dead file**: `scenes/search/SearchResultModel.kt` (未使用の重複。正は `model/SearchResultModel.kt`)、`scenes/search/SearchUiState.kt` (空。正は `SearchViewModel.kt` 内)
- `scenes/search/FiterBottomSheet.kt` はファイル名が typo だが中身の `FilterBottomSheet` は現役
- `pack: List<String>` は Gson TypeConverter (`data/local/Converters.kt`) で永続化

## テーマ (公式サイト準拠・ダーク固定)

- ブランドカラーは `ui/theme/Color.kt` に集約 (iOS版 `Theme/AppTheme.swift` と同一値): 背景 `MainColor #422881` / primary `LightPurple #8B7FD6` / secondary `BrandGreen #36AE37` (公式グリーン) / surface階調 `#241546`〜`#4A2F8F`
- `Theme.kt` の `darkColorScheme` で **surface系を必ず明示する** — 未指定だと M3 デフォルトのほぼ黒になり TopAppBar/NavigationBar/シートが背景から浮く (過去に発生)
- コンポーネント対応: TopAppBar=`surface`、NavigationBar/DropdownMenu=`surfaceContainer`、ModalBottomSheet=`surfaceContainerLow`、AlertDialog=`surfaceContainerHigh`

## API

- Meilisearch: POST `https://search.zutomayocard.net/indexes/zutomayocard_cards/search` (Retrofit + `AuthInterceptor` が Bearer 付与)
- 起動時に全カード取得 (limit 999, `public != "非公開"`) → 絞り込みは **クライアントサイド** (`SearchViewModel.filterResult`、combine + debounce)
- 検索条件UIは `FilterBottomSheet` (SearchViewModel 結合)。他画面でフィルタが要る場合は `viewModel(factory = SearchViewModelFactory())` で専用インスタンスを作って再利用する (DeckEditScreen 方式)

## ドメイン知識: デッキ構築ルール (公式準拠)

`domain/DeckValidator.kt` に集約 (ユニットテスト: `DeckValidatorTest`)。iOS 側 `DeckValidator.swift` と完全同仕様。

- デッキは **20枚** (`DECK_SIZE`)。作りかけ (<20枚) でも保存は許可する仕様
- 同名カード (同一 `id` = 同一パック・同一ナンバー) は **最大2枚** (`MAX_COPIES`) — 追加時にブロック
- キャラクターカード (`cardType == "Character"`) 50%以上は**推奨** — 警告表示のみで保存はブロックしない
- `cardType` の値: `"Character"` / `"Enchant"` / `"Area Enchant"` (英語)

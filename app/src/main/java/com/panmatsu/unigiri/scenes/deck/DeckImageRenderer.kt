package com.panmatsu.unigiri.scenes.deck

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.panmatsu.unigiri.domain.DeckValidator
import com.panmatsu.unigiri.model.DeckModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * デッキ20枚を5列×4行のグリッドに合成した共有用画像を生成する
 */
class DeckImageRenderer(private val context: Context) {

    suspend fun renderToFile(deck: DeckModel): File = withContext(Dispatchers.IO) {
        val bitmap = render(deck)
        val dir = File(context.cacheDir, "shared_images").apply { mkdirs() }
        val file = File(dir, "deck_share.png")
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        file
    }

    suspend fun render(deck: DeckModel): Bitmap = withContext(Dispatchers.IO) {
        // sortOrder順にcount分のスロットへ展開 (同名カードは隣接)
        val slots = deck.cards
            .sortedBy { it.sortOrder }
            .flatMap { card -> List(card.count) { card } }
        if (slots.size != DeckValidator.DECK_SIZE) {
            throw IOException("デッキが${DeckValidator.DECK_SIZE}枚ではありません")
        }

        // cardIdでデデュープして並列取得 (一覧サムネイルとCoilキャッシュを共有)
        val bitmaps: Map<String, Bitmap> = coroutineScope {
            slots.distinctBy { it.cardId }.map { card ->
                async { card.cardId to loadBitmap(card.img) }
            }.awaitAll().toMap()
        }

        val canvasBitmap = Bitmap.createBitmap(CANVAS_WIDTH, CANVAS_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(canvasBitmap)
        canvas.drawColor(Color.WHITE)
        val paint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)

        slots.forEachIndexed { index, card ->
            val bitmap = bitmaps.getValue(card.cardId)
            val col = index % COLUMNS
            val row = index / COLUMNS
            val left = PADDING + col * (CARD_WIDTH + GAP)
            val top = PADDING + row * (CARD_HEIGHT + GAP)
            val dst = Rect(left, top, left + CARD_WIDTH, top + CARD_HEIGHT)
            canvas.drawBitmap(bitmap, centerCropRect(bitmap), dst, paint)
        }

        canvasBitmap
    }

    private suspend fun loadBitmap(url: String): Bitmap {
        val request = ImageRequest.Builder(context)
            .data(url)
            .addHeader("Referer", "https://zutomayocard.net/")
            // hardware bitmapはソフトウェアCanvasに描画できない
            .allowHardware(false)
            .build()
        val result = context.imageLoader.execute(request)
        return (result as? SuccessResult)?.drawable?.toBitmap()
            ?: throw IOException("画像の取得に失敗しました: $url")
    }

    // スロットのアスペクト比に合わせて元画像をcenter-cropするsrc Rect
    private fun centerCropRect(bitmap: Bitmap): Rect {
        val slotAspect = CARD_WIDTH.toFloat() / CARD_HEIGHT
        val srcAspect = bitmap.width.toFloat() / bitmap.height
        return if (srcAspect > slotAspect) {
            val cropWidth = (bitmap.height * slotAspect).toInt()
            val left = (bitmap.width - cropWidth) / 2
            Rect(left, 0, left + cropWidth, bitmap.height)
        } else {
            val cropHeight = (bitmap.width / slotAspect).toInt()
            val top = (bitmap.height - cropHeight) / 2
            Rect(0, top, bitmap.width, top + cropHeight)
        }
    }

    companion object {
        private const val COLUMNS = 5
        private const val ROWS = 4
        private const val CARD_WIDTH = 360
        private const val CARD_HEIGHT = 503
        private const val GAP = 8
        private const val PADDING = 16
        private const val CANVAS_WIDTH =
            PADDING * 2 + COLUMNS * CARD_WIDTH + (COLUMNS - 1) * GAP // 1864
        private const val CANVAS_HEIGHT =
            PADDING * 2 + ROWS * CARD_HEIGHT + (ROWS - 1) * GAP // 2068
    }
}

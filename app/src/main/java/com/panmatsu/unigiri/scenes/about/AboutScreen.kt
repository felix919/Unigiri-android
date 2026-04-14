package com.panmatsu.unigiri.scenes.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panmatsu.unigiri.R

@Composable
fun AboutScreen(
    onNavigateToWebView: (title: String, url: String) -> Unit
) {
    val scrollState = rememberScrollState()
    val baseUrl = stringResource(R.string.url_github_unigiri)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "うにぎり",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "バージョン 1.0",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // リンク
        LinkItem(
            title = "利用規約",
            onClick = { onNavigateToWebView("利用規約", "$baseUrl/terms-of-service.html") }
        )

        LinkItem(
            title = "プライバシーポリシー",
            onClick = { onNavigateToWebView("プライバシーポリシー", "$baseUrl/privacy-policy.html") }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // 免責事項
        SectionTitle("免責事項")
        Text(
            text = "本アプリは、ZUTOMAYO CARD のファンメイドアプリです。" +
                    "「ずっと真夜中でいいのに。」、株式会社ETB RIGHTS、および ZUTOMAYO CARD 公式とは一切関係がありません。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("著作権について")
        Text(
            text = "本アプリに表示されるカード画像、カード名、カードテキスト、イラスト等のすべてのコンテンツに関する著作権は、" +
                    "「ずっと真夜中でいいのに。」および株式会社ETB RIGHTS、その他の権利者に帰属します。" +
                    "本アプリはこれらの権利を侵害する意図を持つものではありません。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("データの出典")
        Text(
            text = "本アプリで表示されるカード情報は、ZUTOMAYO CARD 公式サイト（zutomayocard.net）から取得しています。" +
                    "情報の正確性については保証いたしかねますので、正確な情報は公式サイトをご確認ください。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("免責")
        Text(
            text = "本アプリは非公式のプレイヤーサポートツールです。" +
                    "本アプリの利用により生じたいかなる損害についても、開発者は一切の責任を負いません。\n\n" +
                    "権利者からの要請があった場合は、速やかに対応いたします。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun LinkItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SubSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
    )
}

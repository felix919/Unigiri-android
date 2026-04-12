package com.panmatsu.unigiri.scenes.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    val scrollState = rememberScrollState()

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

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // プライバシーポリシー
        SectionTitle("プライバシーポリシー")

        SubSectionTitle("1. 本アプリが収集する情報")
        Text(
            text = "本アプリは、ユーザーの個人情報を直接収集することはありません。" +
                    "本アプリはカード情報の検索および対戦サポート機能を提供するものであり、アカウント登録やログインは不要です。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("2. 広告配信について")
        Text(
            text = "本アプリでは、広告配信のために Google AdMob を使用しています。" +
                    "AdMob は以下の情報を自動的に収集する場合があります。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
        BulletList(
            items = listOf(
                "広告ID",
                "デバイス情報（機種名、OSバージョンなど）",
                "IPアドレス",
                "アプリの利用状況"
            )
        )
        Text(
            text = "これらの情報は、広告の表示および最適化のために使用されます。" +
                    "Google によるデータの取り扱いについては、Google プライバシーポリシー（https://policies.google.com/privacy）をご確認ください。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("3. パーソナライズ広告について")
        Text(
            text = "Google AdMob は、収集した情報をもとにパーソナライズされた広告を表示する場合があります。" +
                    "パーソナライズ広告を希望しない場合は、端末の設定から変更できます。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )
        BulletList(
            items = listOf(
                "設定 > Google > 広告 > 「広告のパーソナライズをオプトアウト」を有効にする",
                "広告IDのリセット: 同じ設定画面から「広告IDをリセット」を選択する"
            )
        )

        SubSectionTitle("4. 第三者への情報提供")
        Text(
            text = "本アプリは、前述の広告配信（Google AdMob）を除き、ユーザーの情報を第三者に提供することはありません。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("5. お子様のプライバシー")
        Text(
            text = "本アプリは、13歳未満のお子様から意図的に個人情報を収集することはありません。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("6. プライバシーポリシーの変更")
        Text(
            text = "本プライバシーポリシーは、必要に応じて変更されることがあります。変更があった場合は、本画面にて更新いたします。",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        SubSectionTitle("7. お問い合わせ")
        Text(
            text = "本プライバシーポリシーに関するお問い合わせは、以下のメールアドレスまでご連絡ください。\n\npanmatsu@gmail.com",
            fontSize = 14.sp,
            lineHeight = 22.sp
        )

        Text(
            text = "制定日: 2026年4月12日",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 24.dp, bottom = 32.dp)
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

@Composable
private fun BulletList(items: List<String>) {
    Column(modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp)) {
        items.forEach { item ->
            Text(
                text = "・$item",
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

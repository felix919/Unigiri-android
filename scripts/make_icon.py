"""うにぎりアプリアイコン生成: おにぎり + 海苔に「Z」(完全オリジナル・フラットデザイン)

Zは公式faviconのクラシカルセリフ体の様式 (細い横棒+太い斜線+セリフ) に寄せた
独自作図のポリゴンで、フォントは使用していない。

使い方: python3 make_icon.py <出力先ディレクトリ>
"""
import math
from PIL import Image, ImageDraw

SS = 4  # スーパーサンプリング倍率 (アンチエイリアス)

BG = (0x42, 0x28, 0x81, 255)      # 公式パープル
RICE = (0xFF, 0xFE, 0xF7, 255)    # 米 (わずかに温白)
NORI = (0x2B, 0x2B, 0x2B, 255)    # 海苔


def _v(a, b):
    return (b[0] - a[0], b[1] - a[1])


def _norm(v):
    l = math.hypot(*v)
    return (v[0] / l, v[1] / l)


def rounded_triangle(draw, pts, r, fill):
    """外接3頂点ptsと角丸半径rの丸角三角形 (内側三角形+円+太線のミンコフスキー和)"""
    inner = []
    n = len(pts)
    for i in range(n):
        p = pts[i]
        v1 = _norm(_v(p, pts[(i - 1) % n]))
        v2 = _norm(_v(p, pts[(i + 1) % n]))
        bis = _norm((v1[0] + v2[0], v1[1] + v2[1]))
        half = math.acos(max(-1.0, min(1.0, v1[0] * v2[0] + v1[1] * v2[1]))) / 2
        d = r / math.sin(half)
        inner.append((p[0] + bis[0] * d, p[1] + bis[1] * d))
    draw.polygon(inner, fill=fill)
    for ip in inner:
        draw.ellipse([ip[0] - r, ip[1] - r, ip[0] + r, ip[1] + r], fill=fill)
    for i in range(n):
        a, b = inner[i], inner[(i + 1) % n]
        draw.line([a, b], fill=fill, width=int(2 * r))


def draw_z(draw, to_px, x0, y0, w, h, fill):
    """セリフ体風のZをポリゴンで作図 (単位ボックス座標→to_pxで変換)"""
    hbar = 0.14   # 横棒の太さ (高さ比)
    dw = 0.30     # 斜線の水平幅

    def p(pts):
        draw.polygon([to_px(x0 + px * w, y0 + py * h) for px, py in pts], fill=fill)

    # 上横棒
    p([(0.02, 0.0), (0.98, 0.0), (0.98, hbar), (0.02, hbar)])
    # 左端の下向きセリフ
    p([(0.02, hbar), (0.15, hbar), (0.02, hbar + 0.13)])
    # 斜線 (右上→左下、太め)
    p([(0.98 - dw, 0.0), (0.98, 0.0), (0.02 + dw, 1.0), (0.02, 1.0)])
    # 下横棒
    p([(0.02, 1.0 - hbar), (0.98, 1.0 - hbar), (0.98, 1.0), (0.02, 1.0)])
    # 右端の上向きセリフ
    p([(0.98, 1.0 - hbar), (0.85, 1.0 - hbar), (0.98, 1.0 - hbar - 0.13)])


def draw_scene(img, scale=1.0):
    """おにぎり (海苔にZ) を描く (中心基準でscale倍)"""
    size = img.size[0]
    d = ImageDraw.Draw(img)

    def c(x, y):
        return (
            (0.5 + (x - 0.5) * scale) * size,
            (0.5 + (y - 0.5) * scale) * size,
        )

    def s(v):
        return v * scale * size

    # おにぎり本体
    rounded_triangle(
        d,
        [c(0.5, 0.17), c(0.14, 0.76), c(0.86, 0.76)],
        s(0.09),
        RICE,
    )

    # 海苔 (上角のみ丸)
    x0, y0 = c(0.5 - 0.16, 0.55)
    x1, y1 = c(0.5 + 0.16, 0.76)
    d.rounded_rectangle([x0, y0, x1, y1], radius=s(0.045), fill=NORI,
                        corners=(True, True, False, False))

    # 海苔の中央に「Z」
    draw_z(d, c, 0.5 - 0.065, 0.585, 0.13, 0.14, RICE)


def render(size, mode):
    """mode: full=背景あり正方形 / round=円形 / fg=透明背景(adaptive foreground)"""
    big = size * SS
    if mode == "fg":
        img = Image.new("RGBA", (big, big), (0, 0, 0, 0))
        draw_scene(img, scale=0.70)  # adaptive安全圏(66/108)内に収める
    else:
        img = Image.new("RGBA", (big, big), BG)
        draw_scene(img, scale=1.0)
        if mode == "round":
            mask = Image.new("L", (big, big), 0)
            ImageDraw.Draw(mask).ellipse([0, 0, big, big], fill=255)
            img.putalpha(mask)
    return img.resize((size, size), Image.LANCZOS)


if __name__ == "__main__":
    import sys
    out = sys.argv[1] if len(sys.argv) > 1 else "."

    # プレビュー
    render(512, "full").save(f"{out}/preview_full.png")
    render(512, "fg").save(f"{out}/preview_fg.png")

    # iOS (1024, 背景あり)
    render(1024, "full").convert("RGB").save(f"{out}/AppIcon.png")

    # Android
    densities = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}
    for name, mul in densities.items():
        legacy = int(48 * mul)
        fg = int(108 * mul)
        render(legacy, "full").save(f"{out}/ic_launcher_{name}.webp", lossless=True)
        render(legacy, "round").save(f"{out}/ic_launcher_round_{name}.webp", lossless=True)
        render(fg, "fg").save(f"{out}/ic_launcher_foreground_{name}.webp", lossless=True)
    print("done")

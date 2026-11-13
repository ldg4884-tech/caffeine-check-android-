package kr.co.caffeinecheck.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val Navy = Color(0xFF071923)
private val Teal = Color(0xFF43D6C2)
private val Pale = Color(0xFF071923)
private val Ink = Color(0xFFF1FAFA)

data class Drink(
    val name: String, val brand: String, val kind: String,
    val caffeine: Double?, val sugar: Double?, val kcal: Int?,
    val volumeMl: Int? = null, val source: String, val checkedDate: String,
    val sourceUrl: String = "https://www.kca.go.kr/home/sub.do?menukey=4004&mode=view&no=1004472111",
    val verified: Boolean = true
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CaffeineApp(this) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaffeineApp(context: Context) {
    val drinks = remember {
        // 한국소비자원 2026-05-28 프랜차이즈 카페 차음료 품질비교 시험 결과
        // 시험된 1잔 기준 카페인 함량만 등록. 시험되지 않은 영양값은 null로 유지.
        val source = "한국소비자원 프랜차이즈 카페 차음료 품질비교"
        val date = "2026-05-28"
        val kcaUrl = "https://www.kca.go.kr/home/sub.do?menukey=4004&mode=view&no=1004472111"
        val paikSource = "빽다방 공식 메뉴 영양정보"
        val paikDate = "2026-11-13"
        val paikUrl = "https://paikdabang.com/menu/menu_drink/"
        val starbucksSource = "스타벅스 코리아 공식 영양정보"
        val starbucksDate = "2026-11-13"
        val starbucksUrl = "https://www.starbucks.co.kr/menu/drink_list.do?CATE_CD=product_tea"
        listOf(
            Drink("녹차라떼", "메가MGC커피", "말차·녹차 라떼", 45.0, 46.0, null, 410, source, date),
            Drink("말차라떼", "빽다방", "말차·녹차 라떼", 93.0, null, null, 350, source, date),
            Drink("제주 말차 라떼", "스타벅스", "말차·녹차 라떼", 81.0, 26.0, null, 285, source, date),
            Drink("말차라떼", "이디야커피", "말차·녹차 라떼", 70.0, null, null, null, source, date),
            Drink("그린티라떼", "컴포즈커피", "말차·녹차 라떼", 68.0, 50.0, null, 349, source, date),
            Drink("그린티 라떼", "투썸플레이스", "말차·녹차 라떼", 65.0, null, null, null, source, date),
            Drink("로얄밀크티라떼", "메가MGC커피", "밀크티", 57.0, null, null, 397, source, date),
            Drink("밀크티", "빽다방", "밀크티", 113.0, 42.0, null, 359, source, date),
            Drink("클래식 밀크 티", "스타벅스", "밀크티", 172.0, null, null, 311, source, date),
            Drink("로열밀크티", "이디야커피", "밀크티", 73.0, null, null, null, source, date),
            Drink("밀크티", "컴포즈커피", "밀크티", 88.0, null, null, null, source, date),
            Drink("로얄 밀크티", "투썸플레이스", "밀크티", 148.0, 27.0, null, 276, source, date, kcaUrl),

            // 빽다방 공식 메뉴 페이지: 메뉴별 HOT/ICED와 컵 용량을 별도 음료로 등록
            Drink("블랙 벨벳 말차라떼 ICED", "빽다방", "말차·녹차 라떼 · ICED", 66.0, 51.4, 545, 660, paikSource, paikDate, paikUrl),
            Drink("호지차라떼 HOT", "빽다방", "차 라떼 · HOT", 87.5, 47.5, null, 510, paikSource, paikDate, paikUrl),
            Drink("호지차라떼 ICED", "빽다방", "차 라떼 · ICED", 68.2, 34.3, 305, 660, paikSource, paikDate, paikUrl),
            Drink("우바홍차 HOT", "빽다방", "홍차 · HOT", 59.3, 0.0, null, 510, paikSource, paikDate, paikUrl),
            Drink("우바홍차 ICED", "빽다방", "홍차 · ICED", 53.2, 0.0, null, 660, paikSource, paikDate, paikUrl),
            Drink("리치루이보스티 HOT", "빽다방", "허브티 · HOT", 0.0, 38.5, 180, 510, paikSource, paikDate, paikUrl),
            Drink("리치루이보스티 ICED", "빽다방", "허브티 · ICED", 0.0, 28.4, 140, 660, paikSource, paikDate, paikUrl),

            // 스타벅스 공식 영양정보: 해당 페이지의 기본 Tall 기준
            Drink("아이스 퓨어 말차", "스타벅스", "말차 · ICED · Tall", 60.0, 0.0, 9, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("퓨어 말차", "스타벅스", "말차 · HOT · Tall", 75.0, 0.0, 11, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("말차 글레이즈드 티 라떼", "스타벅스", "말차 라떼 · HOT · Tall", 65.0, 29.0, 310, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 말차 글레이즈드 티 라떼", "스타벅스", "말차 라떼 · ICED · Tall", 60.0, 23.0, 235, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("클래식 밀크티 블렌디드", "스타벅스", "밀크티 블렌디드 · Tall", 60.0, 37.0, 315, null, starbucksSource, starbucksDate, starbucksUrl),

            // 이디야 공식 음료 페이지: 페이지에 표기된 L/EX 제공량별 카페인
            Drink("연유 콜드브루 (일반) L", "이디야커피", "콜드브루 · L", 181.0, 41.0, 313, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html"),
            Drink("연유 콜드브루 (일반) EX", "이디야커피", "콜드브루 · EX", null, null, null, 670, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html"),
            Drink("연유 콜드브루 (디카페인) L", "이디야커피", "디카페인 콜드브루 · L", 12.0, 41.0, 313, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html"),
            Drink("연유 콜드브루 (디카페인) EX", "이디야커피", "디카페인 콜드브루 · EX", 18.0, 60.0, 449, 670, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html"),
            Drink("HOT 가나 시그니처 초콜릿 L", "이디야커피", "초콜릿 · HOT · L", 6.0, 41.0, 548, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html"),
            Drink("HOT 가나 시그니처 초콜릿 EX", "이디야커피", "초콜릿 · HOT · EX", 9.0, 61.0, 800, 650, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html"),
            Drink("ICED 가나 시그니처 초콜릿 L", "이디야커피", "초콜릿 · ICED · L", 6.0, 38.0, 437, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html"),
            Drink("흑당 콜드브루 L", "이디야커피", "콜드브루 · L", 181.0, 45.0, 306, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html?chked_val=12%2C&skeyword="),
            Drink("흑당 콜드브루 EX", "이디야커피", "콜드브루 · EX", 272.0, 63.0, 425, 670, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html?chked_val=12%2C&skeyword="),
            Drink("흑당 콜드브루 디카페인 L", "이디야커피", "디카페인 콜드브루 · L", 12.0, 45.0, 305, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html?chked_val=12%2C&skeyword="),
            Drink("흑당 콜드브루 디카페인 EX", "이디야커피", "디카페인 콜드브루 · EX", 18.0, 63.0, 424, 670, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://www.ediya.com/contents/drink.html?chked_val=12%2C&skeyword="),
            Drink("제로슈가 아샷추 복숭아 L", "이디야커피", "아이스티·에스프레소 · L", 129.0, 0.0, 43, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html?chked_val=14%2C"),
            Drink("제로슈가 아샷추 복숭아 EX", "이디야커피", "아이스티·에스프레소 · EX", 143.0, 0.0, 60, 670, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html?chked_val=14%2C"),
            // 투썸플레이스: 공식 메뉴 상세에서 확인한 카페인 수치
            Drink("디카페인 민트 밀샷추 R", "투썸플레이스", "디카페인 밀크티 · R", 6.0, null, null, null, "투썸플레이스 공식 메뉴 상세", "2026-11-13", "https://dd-mo.twosome.co.kr/mn/menuInfoDetail.do?menuCd=10193562"),
            Drink("디카페인 민트 밀샷추 L", "투썸플레이스", "디카페인 밀크티 · L", 10.0, null, null, null, "투썸플레이스 공식 메뉴 상세", "2026-11-13", "https://dd-mo.twosome.co.kr/mn/menuInfoDetail.do?menuCd=10193562"),

            // 텐퍼센트커피: 매장 메뉴판에서 판매 메뉴명을 확인. 공식 카페인 수치가 확인되지 않아 null로 유지
            Drink("아메리카노(다크)", "텐퍼센트커피", "커피 · 다크 원두", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("카페라떼", "텐퍼센트커피", "커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("텐라떼", "텐퍼센트커피", "시그니처 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("텐라떼 10oz · 참고값", "텐퍼센트커피", "시그니처 커피 · 10oz · 비공식 참고값", 31.06, null, null, 296, "다이닝코드 이용자 게시 영양정보 · 공식 미확인", "2026-11-13", "https://www.diningcode.com/profile.php?rid=WPiNoqZZ2BIh", false),
            Drink("디카페인 아메리카노", "텐퍼센트커피", "디카페인 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 플랫화이트", "텐퍼센트커피", "디카페인 커피 · ICED", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 카페라떼", "텐퍼센트커피", "디카페인 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 바닐라빈라떼", "텐퍼센트커피", "디카페인 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 돌체라떼", "텐퍼센트커피", "디카페인 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 밀크카라멜라떼", "텐퍼센트커피", "디카페인 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 카페모카", "텐퍼센트커피", "디카페인 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 시그니처라떼", "텐퍼센트커피", "디카페인 시그니처 커피 · ICED", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("디카페인 텐라떼", "텐퍼센트커피", "디카페인 시그니처 커피 · ICED", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("바닐라빈라떼", "텐퍼센트커피", "커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("돌체라떼", "텐퍼센트커피", "커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("밀크카라멜라떼", "텐퍼센트커피", "커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("카페모카", "텐퍼센트커피", "커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("콜드브루", "텐퍼센트커피", "콜드브루", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("콜드브루 라떼", "텐퍼센트커피", "콜드브루", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("돌체 콜드브루라떼", "텐퍼센트커피", "콜드브루", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("시그니처라떼", "텐퍼센트커피", "시그니처 커피 · ICED", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("솔티드 밀크 카라멜", "텐퍼센트커피", "시그니처 커피 · ICED", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("더티땅콩라떼", "텐퍼센트커피", "시그니처 커피 · ICED", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("아인슈페너 HOT", "텐퍼센트커피", "시그니처 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            Drink("아인슈페너 ICED", "텐퍼센트커피", "시그니처 커피", null, null, null, null, "카페인 수치 미확인 · 매장 메뉴판", "2026-11-13", "https://app.passorder.co.kr/normal/d14e1ee5-9fc8-4d3f-969e-0a0109de6e79/menus"),
            // 빽다방 공식 메뉴 영양정보에서 추가 확인한 음료. HOT/ICED·용량별 개별 등록.
            Drink("꿀밤라떼 HOT", "빽다방", "기타 라떼 · HOT", 0.0, null, 484.6, 510, paikSource, paikDate, paikUrl),
            Drink("자몽블랙티 HOT", "빽다방", "홍차 · HOT", 47.1, null, 193, 510, paikSource, paikDate, paikUrl),
            Drink("제로슈거 자몽티 HOT", "빽다방", "과일티 · HOT", 0.0, 0.6, 124, 510, paikSource, paikDate, paikUrl),
            Drink("제로슈거 자몽티 ICED", "빽다방", "과일티 · ICED", 0.0, 0.4, 113, 660, paikSource, paikDate, paikUrl),
            Drink("제로슈거 자몽에이드 ICED", "빽다방", "에이드 · ICED", 0.0, 0.4, 111, 660, paikSource, paikDate, paikUrl),
            Drink("딸기복숭아 루이보스티 ICED", "빽다방", "허브티 · ICED", 0.0, null, 190, 660, paikSource, paikDate, paikUrl),
            Drink("사과당근주스 ICED", "빽다방", "주스 · ICED", 0.0, 40.0, 200, 660, paikSource, paikDate, paikUrl),
            Drink("율무라떼 HOT", "빽다방", "곡물 라떼 · HOT", 0.0, 39.0, 425, 510, paikSource, paikDate, paikUrl),
            Drink("율무라떼 ICED", "빽다방", "곡물 라떼 · ICED", 0.0, 35.0, 402, 660, paikSource, paikDate, paikUrl),
            Drink("율무아몬드라떼 HOT", "빽다방", "곡물 라떼 · HOT", 0.0, 35.0, 324, 510, paikSource, paikDate, paikUrl),
            Drink("애사비 ICED", "빽다방", "기타 음료 · ICED", 0.0, 0.5, 119, 660, paikSource, paikDate, paikUrl),
            Drink("블루레몬에이드 ICED", "빽다방", "에이드 · ICED", 0.0, 69.0, 306, 660, paikSource, paikDate, paikUrl),
            Drink("복숭아 아이스티 ICED", "빽다방", "아이스티 · ICED", 20.2, 59.8, 256, 660, paikSource, paikDate, paikUrl),
            Drink("아망추 ICED", "빽다방", "아이스티 · ICED", 15.0, 60.0, 295, 660, paikSource, paikDate, paikUrl),
            Drink("망고스무디 ICED", "빽다방", "스무디 · ICED", 0.0, null, 283, 660, paikSource, paikDate, paikUrl),
            Drink("대추차 ICED", "빽다방", "전통차 · ICED", 0.0, null, 211, 660, paikSource, paikDate, paikUrl),
            Drink("쌍화차 HOT", "빽다방", "전통차 · HOT", 0.0, 45.0, 201, 510, paikSource, paikDate, paikUrl),
            Drink("쌍화차 ICED", "빽다방", "전통차 · ICED", 0.0, 57.0, 262, 660, paikSource, paikDate, paikUrl),
            Drink("대추라떼 HOT", "빽다방", "전통 라떼 · HOT", 0.0, 70.7, 524, 510, paikSource, paikDate, paikUrl),
            Drink("아임파인민트티 HOT", "빽다방", "허브티 · HOT", 4.0, 0.0, 59, 510, paikSource, paikDate, paikUrl),
            Drink("아임파인민트티 ICED", "빽다방", "허브티 · ICED", 4.0, 0.0, 52, 660, paikSource, paikDate, paikUrl),
            Drink("쿨라임스무디 ICED", "빽다방", "스무디 · ICED", null, 89.0, 376, 660, paikSource, paikDate, paikUrl),
            Drink("쿨라임에이드 ICED", "빽다방", "에이드 · ICED", null, 47.0, 189, 660, paikSource, paikDate, paikUrl),
            Drink("수정과 HOT", "빽다방", "전통차 · HOT", 0.0, 67.3, 289, 510, paikSource, paikDate, paikUrl),
            Drink("수정과 ICED", "빽다방", "전통차 · ICED", 0.0, 64.5, 294, 660, paikSource, paikDate, paikUrl),
            Drink("식혜 ICED", "빽다방", "전통음료 · ICED", 0.0, null, 318, 660, paikSource, paikDate, paikUrl),
            Drink("말차라떼 HOT", "빽다방", "말차 라떼 · HOT", 130.5, 53.8, 481, 510, paikSource, paikDate, paikUrl),
            Drink("말차라떼 ICED", "빽다방", "말차 라떼 · ICED", 104.4, 37.5, 354, 660, paikSource, paikDate, paikUrl),
            Drink("민트초코라떼 HOT", "빽다방", "초콜릿 라떼 · HOT", 45.0, 91.0, 655, 510, paikSource, paikDate, paikUrl),
            Drink("우롱티 HOT", "빽다방", "우롱티 · HOT", 92.0, 0.0, 7, 510, paikSource, paikDate, paikUrl),
            Drink("우롱티 ICED", "빽다방", "우롱티 · ICED", 67.14, 0.0, 3, 660, paikSource, paikDate, paikUrl),
            Drink("레모네이드 ICED", "빽다방", "에이드 · ICED", 0.0, 60.0, 246, 660, paikSource, paikDate, paikUrl),
            Drink("청포도에이드 ICED", "빽다방", "에이드 · ICED", 0.0, 54.0, 233, 660, paikSource, paikDate, paikUrl),
            Drink("깔라만시에이드 ICED", "빽다방", "에이드 · ICED", 0.0, 48.84, 215, 660, paikSource, paikDate, paikUrl),
            Drink("딸기에이드 ICED", "빽다방", "에이드 · ICED", 0.0, 39.1, 185, 660, paikSource, paikDate, paikUrl),
            Drink("파인애플주스 ICED", "빽다방", "주스 · ICED", 0.0, 32.0, 192, 660, paikSource, paikDate, paikUrl),
            Drink("토마토주스 ICED", "빽다방", "주스 · ICED", 0.0, 41.3, 182, 660, paikSource, paikDate, paikUrl),
            Drink("딸기주스 ICED", "빽다방", "주스 · ICED", 0.0, 42.31, 223, 660, paikSource, paikDate, paikUrl),
            Drink("망고주스 ICED", "빽다방", "주스 · ICED", 0.0, 37.0, 241, 660, paikSource, paikDate, paikUrl),

            // 제3자 카페인 데이터베이스의 참고 수치. 공식 브랜드 공표값이 아니므로 일일 합산에서 제외.
            Drink("아메리카노 다크 HOT · 참고값", "텐퍼센트커피", "커피 · 다크 · HOT · 비공식 참고값", 160.52, null, null, null, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%95%84%EB%A9%94%EB%A6%AC%EC%B9%B4%EB%85%B8", false),
            Drink("아메리카노 미디엄 HOT · 참고값", "텐퍼센트커피", "커피 · 미디엄 · HOT · 비공식 참고값", 169.62, null, null, null, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%95%84%EB%A9%94%EB%A6%AC%EC%B9%B4%EB%85%B8", false),
            Drink("아메리카노 다크 ICED 16oz · 참고값", "텐퍼센트커피", "커피 · 다크 · ICED · 비공식 참고값", 157.14, null, null, 473, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%95%84%EB%A9%94%EB%A6%AC%EC%B9%B4%EB%85%B8", false),
            Drink("아메리카노 미디엄 ICED 16oz · 참고값", "텐퍼센트커피", "커피 · 미디엄 · ICED · 비공식 참고값", 135.71, null, null, 473, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%95%84%EB%A9%94%EB%A6%AC%EC%B9%B4%EB%85%B8", false),
            Drink("디카페인 아메리카노 HOT · 참고값", "텐퍼센트커피", "디카페인 커피 · HOT · 비공식 참고값", 6.44, null, null, null, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%95%84%EB%A9%94%EB%A6%AC%EC%B9%B4%EB%85%B8", false),
            Drink("디카페인 아메리카노 ICED 16oz · 참고값", "텐퍼센트커피", "디카페인 커피 · ICED · 비공식 참고값", 5.76, null, null, 473, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%95%84%EB%A9%94%EB%A6%AC%EC%B9%B4%EB%85%B8", false),
            Drink("콜드브루라떼 HOT · 참고값", "텐퍼센트커피", "콜드브루 · HOT · 비공식 참고값", 174.9, null, null, null, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false),
            Drink("콜드브루라떼 ICED 16oz · 참고값", "텐퍼센트커피", "콜드브루 · ICED · 비공식 참고값", 180.9, null, null, 473, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false),
            Drink("디카페인 콜드브루라떼 HOT · 참고값", "텐퍼센트커피", "디카페인 콜드브루 · HOT · 비공식 참고값", 9.33, null, null, null, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false),
            Drink("디카페인 콜드브루라떼 ICED 16oz · 참고값", "텐퍼센트커피", "디카페인 콜드브루 · ICED · 비공식 참고값", 5.59, null, null, 473, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false),
            Drink("돌체 콜드브루라떼 HOT · 참고값", "텐퍼센트커피", "콜드브루 · HOT · 비공식 참고값", 138.58, null, null, null, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false),
            Drink("돌체 콜드브루라떼 ICED 16oz · 참고값", "텐퍼센트커피", "콜드브루 · ICED · 비공식 참고값", 143.96, null, null, 473, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false),
            Drink("디카페인 돌체 콜드브루라떼 HOT · 참고값", "텐퍼센트커피", "디카페인 콜드브루 · HOT · 비공식 참고값", 7.45, null, null, null, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false),
            Drink("디카페인 돌체 콜드브루라떼 ICED 16oz · 참고값", "텐퍼센트커피", "디카페인 콜드브루 · ICED · 비공식 참고값", 4.42, null, null, 473, "카페인노트 제3자 데이터베이스 · 공식 미확인", "2026-11-13", "https://caffeinenote.com/drinks?q=%EC%BD%9C%EB%93%9C%EB%B8%8C%EB%A3%A8", false)
        )
    }
    val prefs = remember { context.getSharedPreferences("caffeine_check", Context.MODE_PRIVATE) }
    var query by remember { mutableStateOf("") }
    var caffeineFilter by remember { mutableStateOf("전체") }
    var selectedBrand by remember { mutableStateOf("전체") }
    var favorites by remember {
        mutableStateOf(
            prefs.getStringSet("favorites", emptySet())?.toSet()?.let { saved ->
                val migrated = drinks.filter { it.name in saved }.map { drinkKey(it) }.toSet()
                val normalized = saved.filter { "|" in it }.toSet() + migrated
                if (normalized != saved) prefs.edit().putStringSet("favorites", normalized).apply()
                normalized
            } ?: emptySet()
        )
    }
    var selectedTab by remember { mutableIntStateOf(0) }
    var consumed by remember { mutableStateOf(loadRecords(prefs)) }
    val brands = listOf("전체") + drinks.map { it.brand }.distinct()
    val filtered = drinks.filter {
        (it.name.contains(query, true) || it.brand.contains(query, true)) &&
            (selectedBrand == "전체" || it.brand == selectedBrand) &&
            when (caffeineFilter) {
                "카페인 0mg" -> it.caffeine == 0.0
                "100mg 이하" -> it.caffeine != null && it.caffeine <= 100
                "저카페인" -> it.caffeine != null && it.caffeine > 0.0 && it.caffeine <= 100.0
                else -> true
            }
    }
    val todayRecords = consumed.filter { dateKey(it.optLong("time", 0L)) == dateKey(System.currentTimeMillis()) }
    val todayTotal = todayRecords.filter { it.optBoolean("known", false) && it.optBoolean("verified", false) }.sumOf { it.optDouble("caffeine", 0.0) }
    val unknownToday = todayRecords.count { !it.optBoolean("known", false) || !it.optBoolean("verified", false) }

    MaterialTheme(colorScheme = darkColorScheme(primary = Teal, background = Pale, surface = Color(0xFF102B38), onSurface = Color(0xFFF1FAFA))) {
        Scaffold(
            containerColor = Pale,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("카페인 안심체크", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("오늘도 가볍게, 한 잔씩 확인해요", fontSize = 12.sp, color = Color(0xFFBBD0D5))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Navy)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF102B38)) {
                    NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = {}, label = { Text("음료 찾기") })
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = {}, label = { Text("섭취 기록") })
                    NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = {}, label = { Text("즐겨찾기") })
                }
            }
        ) { padding ->
            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Navy)) {
                            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                                Text("오늘 섭취한 카페인", color = Color(0xFFBBD0D5), fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text("${formatMg(todayTotal)}", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
                                    Text(" mg", color = Color.White, modifier = Modifier.padding(bottom = 6.dp))
                                    Spacer(Modifier.weight(1f))
                                    Text("참고선 200mg", color = Color(0xFF43D6C2), fontSize = 12.sp)
                                }
                                Text("임신 중 카페인 참고 기준이며 개인별 의료진 안내를 우선하세요. 확인 필요 ${unknownToday}건", color = Color(0xFFA5BEC5), fontSize = 11.sp)
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = query, onValueChange = { query = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("음료 또는 브랜드 검색") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                    item {
                        Text("카페인 필터", color = Color(0xFFBBD0D5), fontSize = 12.sp)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("전체", "카페인 0mg", "100mg 이하", "저카페인").forEach { option ->
                                FilterChip(selected = caffeineFilter == option, onClick = { caffeineFilter = option }, label = { Text(option) })
                            }
                        }
                    }
                    item {
                        Text("브랜드", color = Color(0xFFBBD0D5), fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            brands.forEach { brand ->
                                FilterChip(selected = selectedBrand == brand, onClick = { selectedBrand = brand }, label = { Text(brand) })
                            }
                        }
                    }
                    items(filtered) { drink ->
                        DrinkCard(drink, drinkKey(drink) in favorites, {
                            val record = JSONObject().apply {
                                put("name", drink.name)
                                put("brand", drink.brand)
                                put("caffeine", drink.caffeine ?: 0)
                                put("known", drink.caffeine != null)
                                put("verified", drink.caffeine != null && drink.verified)
                                put("source", drink.source)
                                put("checkedDate", drink.checkedDate)
                                put("volumeMl", drink.volumeMl ?: JSONObject.NULL)
                                put("sugar", drink.sugar ?: JSONObject.NULL)
                                put("kcal", drink.kcal ?: JSONObject.NULL)
                                put("time", System.currentTimeMillis())
                            }
                            consumed = consumed + record
                            saveRecords(prefs, consumed)
                        }, {
                            favorites = if (drinkKey(drink) in favorites) favorites - drinkKey(drink) else favorites + drinkKey(drink)
                            prefs.edit().putStringSet("favorites", favorites).apply()
                        })
                    }
                    if (filtered.isEmpty()) item { Text("검색 결과가 없어요.", color = Color.Gray) }
                    item { Text("※ 공식·공공기관 확인값과 제3자 참고값을 구분합니다. 제3자 참고값은 일일 합산에서 제외하며, 공식 미공개 값은 추정하지 않습니다.", color = Color(0xFF91AAB2), fontSize = 11.sp) }
                }
            } else if (selectedTab == 1) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("오늘 섭취 기록", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text("확인된 카페인 수치만 합산합니다.", color = Color.Gray, fontSize = 13.sp)
                        Text("합계 ${formatMg(todayTotal)} mg", fontSize = 28.sp, color = Teal, fontWeight = FontWeight.Bold)
                    }
                    items(consumed.reversed()) { record ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF102B38))) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(record.optString("brand") + " · " + record.optString("name"), fontWeight = FontWeight.SemiBold)
                                    Text(if (!record.optBoolean("known")) "카페인 정보 확인 필요" else if (record.optBoolean("verified")) "${formatMg(record.optDouble("caffeine"))} mg" else "${formatMg(record.optDouble("caffeine"))} mg · 참고값(합산 제외)", color = Color.Gray, fontSize = 12.sp)
                                }
                                TextButton(onClick = {
                                    consumed = consumed.filterNot { it == record }
                                    saveRecords(prefs, consumed)
                                }) { Text("삭제") }
                            }
                        }
                    }
                    if (consumed.isEmpty()) item { Text("아직 기록이 없어요. 음료를 선택해 기록해 보세요.", color = Color.Gray) }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Text("즐겨찾기", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink) }
                    items(drinks.filter { drinkKey(it) in favorites }) { drink ->
                        DrinkCard(drink, true, {
                            val record = JSONObject().apply {
                                put("name", drink.name); put("brand", drink.brand)
                                put("caffeine", drink.caffeine ?: 0); put("known", drink.caffeine != null)
                                put("verified", drink.caffeine != null && drink.verified); put("source", drink.source); put("checkedDate", drink.checkedDate); put("volumeMl", drink.volumeMl ?: JSONObject.NULL); put("sugar", drink.sugar ?: JSONObject.NULL); put("kcal", drink.kcal ?: JSONObject.NULL); put("time", System.currentTimeMillis())
                            }
                            consumed = consumed + record
                            saveRecords(prefs, consumed)
                        }, {
                            favorites = favorites - drinkKey(drink)
                            prefs.edit().putStringSet("favorites", favorites).apply()
                        })
                    }
                    if (favorites.isEmpty()) item { Text("음료 카드의 ☆ 저장을 눌러 추가하세요.", color = Color.Gray) }
                }
            }
        }
    }
}

@Composable
private fun DrinkCard(drink: Drink, isFavorite: Boolean, onAdd: () -> Unit, onFavorite: () -> Unit) {
    val context = LocalContext.current
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF102B38)), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(drink.brand, fontSize = 12.sp, color = Teal, fontWeight = FontWeight.Bold)
                    Text(drink.name, fontSize = 17.sp, color = Ink, fontWeight = FontWeight.Bold)
                    Text(drink.kind + " · " + (drink.volumeMl?.let { "${it}mL" } ?: "용량 미확인"), fontSize = 11.sp, color = Color.Gray)
                    Text("출처: ${drink.source} · 확인일 ${drink.checkedDate}", fontSize = 10.sp, color = Color(0xFF91AAB2))
                    if (!drink.verified) Text("참고 수치 · 일일 섭취량 합산 제외", fontSize = 10.sp, color = Color(0xFFFFC56B))
                    TextButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(drink.sourceUrl))
                            context.startActivity(intent)
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("출처 원문 보기 ↗", color = Teal, fontSize = 11.sp) }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(if (drink.caffeine == null) "미확인" else "${formatMg(drink.caffeine)} mg",
                        color = if (drink.caffeine == 0.0) Teal else Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = onFavorite) { Text(if (isFavorite) "★ 저장됨" else "☆ 저장", color = Teal) }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("열량 ${drink.kcal?.let { "${it}kcal" } ?: "미확인"}  ·  당류 ${drink.sugar?.let { "${it}g" } ?: "미확인"}",
                    fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = Teal)) { Text("마셨어요") }
            }
        }
    }
}

private fun formatMg(value: Double): String = if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()

private fun drinkKey(drink: Drink): String = "${drink.brand}|${drink.name}"

private fun dateKey(time: Long): String = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.KOREA).format(java.util.Date(time))

private fun loadRecords(prefs: android.content.SharedPreferences): List<JSONObject> {
    val array = JSONArray(prefs.getString("records", "[]"))
    return (0 until array.length()).map { array.getJSONObject(it) }
}

private fun saveRecords(prefs: android.content.SharedPreferences, records: List<JSONObject>) {
    val array = JSONArray()
    records.forEach { array.put(it) }
    prefs.edit().putString("records", array.toString()).apply()
}

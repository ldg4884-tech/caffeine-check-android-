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
        val starbucksUrl = "https://www.starbucks.co.kr/menu/drink_list.do"
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
            Drink("꿀밤라떼 HOT", "빽다방", "기타 라떼 · HOT", 0.0, null, 485, 510, paikSource, paikDate, paikUrl),
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

            // 이디야 공식 음료 페이지의 최신 메뉴명 확인. 미공개 영양값은 null로 유지.
            Drink("흑임자 크림 라떼 HOT L", "이디야커피", "라떼 · HOT · L", 0.0, 55.0, 501, 520, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("흑임자 크림 라떼 HOT EX", "이디야커피", "라떼 · HOT · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("흑임자 크림 라떼 ICED L", "이디야커피", "라떼 · ICED · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("흑임자 크림 라떼 ICED EX", "이디야커피", "라떼 · ICED · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("밤 크림 라떼 HOT L", "이디야커피", "라떼 · HOT · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("밤 크림 라떼 HOT EX", "이디야커피", "라떼 · HOT · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("밤 크림 라떼 ICED L", "이디야커피", "라떼 · ICED · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("밤 크림 라떼 ICED EX", "이디야커피", "라떼 · ICED · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("히비스커스 청귤티 HOT L", "이디야커피", "블렌딩티 · HOT · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("히비스커스 청귤티 HOT EX", "이디야커피", "블렌딩티 · HOT · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("히비스커스 청귤티 ICED L", "이디야커피", "블렌딩티 · ICED · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("히비스커스 청귤티 ICED EX", "이디야커피", "블렌딩티 · ICED · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("말차폼 자스민티 ICED L", "이디야커피", "블렌딩티 · ICED · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("말차폼 자스민티 ICED EX", "이디야커피", "블렌딩티 · ICED · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("구망추 ICED L", "이디야커피", "아이스티 · ICED · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("구망추 ICED EX", "이디야커피", "아이스티 · ICED · EX", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),
            Drink("구아바 자스민 에이드 ICED L", "이디야커피", "에이드 · ICED · L", null, null, null, null, "이디야커피 공식 음료 영양정보", "2026-11-13", "https://ediya.com/contents/drink.html"),

            // 메가MGC커피 공식 메뉴 영양정보 · 2026 겨울시즌 및 상시 메뉴
            Drink("나 혼자만 드랍 UP! 은랑 큐브 레몬에이드 ICED", "메가MGC커피", "에이드 · ICED", 0.0, 44.9, 194, 710, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("음, 괜찮은 휴식이군 블레이드 초코크림커피 ICED", "메가MGC커피", "커피 · ICED", 242.7, 43.0, 306, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("하우스밀크 라떼 ICED", "메가MGC커피", "라떼 · ICED", 118.0, 15.8, 329, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("ARIH 듀얼바이오틱 소다 레드 루비 ICED", "메가MGC커피", "에이드 · ICED", 0.0, 2.0, 20, 355, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("딸기라떼 ICED", "메가MGC커피", "라떼 · ICED", 0.0, 58.0, 315, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("밀크쉐이크 ICED", "메가MGC커피", "쉐이크 · ICED", 0.0, 62.4, 401, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("유자생강차 HOT", "메가MGC커피", "과일차 · HOT", 0.0, 88.7, 389, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("초코젤라또 말차라떼 ICED", "메가MGC커피", "말차 라떼 · ICED", 213.5, 52.3, 332, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("딥 초코 체리 프라페 ICED", "메가MGC커피", "프라페 · ICED", null, null, null, null, "메가MGC커피 공식 메뉴명 · 영양정보 미확인", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("쫀득 모찌 밀크티 ICED", "메가MGC커피", "밀크티 · ICED", null, null, null, null, "메가MGC커피 공식 메뉴명 · 영양정보 미확인", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),
            Drink("매실 모과차 HOT", "메가MGC커피", "과일차 · HOT", null, null, null, null, "메가MGC커피 공식 메뉴명 · 영양정보 미확인", "2026-11-13", "https://www.mega-mgccoffee.com/menu/"),

            // 메가MGC커피 공식 메뉴 추가 조사 · 공식 메뉴 페이지의 제공량/카페인 표기 기준
            // 카페인 미표기·판독 불명 값은 null로 두고 임의 추정하지 않음
            Drink("연유라떼 HOT", "메가MGC커피", "커피 라떼 · HOT", 194.2, 32.8, 322, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=2"),
            Drink("카라멜마끼아또 HOT", "메가MGC커피", "커피 라떼 · HOT", 203.4, 26.1, 243, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=2"),
            Drink("카페모카 ICED", "메가MGC커피", "커피 라떼 · ICED", 232.5, 33.9, 294, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=2"),
            Drink("카푸치노 HOT", "메가MGC커피", "커피 라떼 · HOT", 192.3, 1.1, 132, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=2"),
            Drink("골드망고스무디 ICED", "메가MGC커피", "스무디 · ICED", 0.0, 49.5, 218, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("할메가커피 ICED", "메가MGC커피", "커피 · ICED", 176.5, 28.7, 259, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("왕할메가커피 ICED", "메가MGC커피", "커피 · ICED", 279.5, 45.4, 410, 946, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("코코넛 커피 스무디 ICED", "메가MGC커피", "커피 스무디 · ICED", 236.0, 75.0, 700, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("딸기주스 ICED", "메가MGC커피", "주스 · ICED", 0.0, 47.3, 242, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("딸기바나나주스 ICED", "메가MGC커피", "주스 · ICED", 0.0, 46.9, 297, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("디카페인 에스프레소 HOT", "메가MGC커피", "디카페인 커피 · HOT", 3.9, 0.0, 7, 59, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("디카페인 아메리카노 HOT", "메가MGC커피", "디카페인 커피 · HOT", 7.8, 0.0, 15, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?page=3"),
            Drink("왕메가카페라떼 ICED", "메가MGC커피", "커피 라떼 · ICED", 354.0, 20.0, 284, 946, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?list_checkbox_all=true&menu_category1=1&menu_category2=1&page=2"),
            Drink("디카페인 왕메가카페라떼 ICED", "메가MGC커피", "디카페인 커피 라떼 · ICED", 0.0, null, 282, 946, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?list_checkbox_all=true&menu_category1=1&menu_category2=1&page=2"),
            Drink("할메가미숫커피 ICED", "메가MGC커피", "곡물 커피 · ICED", 176.5, 47.4, 419, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?list_checkbox_all=true&menu_category1=1&menu_category2=1&page=2"),
            Drink("라이트 바닐라 아몬드라떼 ICED", "메가MGC커피", "라떼 · ICED", 236.0, 5.4, 83, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?list_checkbox_all=true&menu_category1=1&menu_category2=1&page=2"),
            Drink("디카페인 라이트 바닐라 아몬드라떼 ICED", "메가MGC커피", "디카페인 라떼 · ICED", 7.8, 5.4, 82, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?list_checkbox_all=true&menu_category1=1&menu_category2=1&page=2"),
            Drink("왕메가사과유자 ICED", "메가MGC커피", "과일티 · ICED", 0.0, 76.7, 313, 946, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?list_checkbox_all=true&menu_category1=1&menu_category2=1&page=2"),
            Drink("자몽차 HOT", "메가MGC커피", "과일차 · HOT", 0.0, 58.1, 294, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?category=2&menu_category1=1&menu_category2=1&page=2"),
            Drink("허니자몽블랙티 HOT", "메가MGC커피", "홍차 · HOT", 61.5, 53.6, 302, 591, "메가MGC커피 공식 메뉴 영양정보", "2026-11-13", "https://www.mega-mgccoffee.com/menu/menu.php?category=2&menu_category1=1&menu_category2=1&page=2"),
            // 공식 메뉴명은 확인됐으나 현재 확인 가능한 공식 카페인 수치가 없어 미확인으로 유지

            // 스타벅스 코리아 공식 음료 영양정보 · Tall 기준(Trenta 표기 메뉴는 Trenta 기준)
            // 2026-11-13 확인 · 메뉴별 공식 카페인 수치
            Drink("나이트로 바닐라 크림", "스타벅스", "콜드브루 · Tall", 232.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("나이트로 콜드 브루", "스타벅스", "콜드브루 · Tall", 245.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("돌체 콜드 브루", "스타벅스", "콜드브루 · Tall", 130.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("리저브 나이트로", "스타벅스", "콜드브루 · Tall", 190.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("리저브 콜드 브루", "스타벅스", "콜드브루 · Tall", 190.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("막걸리향 크림 콜드 브루", "스타벅스", "콜드브루 · Tall", 53.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("민트 콜드 브루", "스타벅스", "콜드브루 · Tall", 415.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("바닐라 아인슈페너 콜드 브루", "스타벅스", "콜드브루 · Tall", 134.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("바닐라 크림 콜드 브루", "스타벅스", "콜드브루 · Tall", 135.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("베르가못 콜드 브루", "스타벅스", "콜드브루 · Tall", 130.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("블랙&화이트 콜드 브루", "스타벅스", "콜드브루 · Tall", 190.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("시그니처 더 블랙 콜드 브루", "스타벅스", "콜드브루 · Tall", 680.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("오트 콜드 브루", "스타벅스", "콜드브루 · Tall", 65.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("콜드 브루(Trenta)", "스타벅스", "콜드브루 · Trenta", 360.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("콜드 브루", "스타벅스", "콜드브루 · Tall", 155.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("콜드 브루 몰트", "스타벅스", "콜드브루 · Tall", 190.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("콜드 브루 플로트", "스타벅스", "콜드브루 · Tall", 190.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),
            Drink("프렌치 바닐라 콜드 브루", "스타벅스", "콜드브루 · Tall", 65.0, null, null, null, "스타벅스 코리아 공식 음료 영양정보", "2026-11-13", "https://www.starbucks.co.kr/menu/drink_list.do"),

            // 스타벅스 코리아 공식 영양정보 · 2026-11-13 확인 · Tall 기준
            Drink("브루드 커피", "스타벅스", "커피 · HOT · Tall", 260.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("스위트 밀크 커피", "스타벅스", "커피 · HOT · Tall", 60.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 브루드 커피", "스타벅스", "커피 · ICED · Tall", 140.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("스타벅스 에어로카노", "스타벅스", "에스프레소 · Tall", 150.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("블랙 글레이즈드 라떼", "스타벅스", "라떼 · HOT · Tall", 75.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 블랙 글레이즈드 라떼", "스타벅스", "라떼 · ICED · Tall", 75.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 윈터 스카치 바닐라 라떼", "스타벅스", "라떼 · ICED · Tall", 100.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 토피 넛 라떼", "스타벅스", "라떼 · ICED · Tall", 75.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("윈터 스카치 바닐라 라떼", "스타벅스", "라떼 · HOT · Tall", 100.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("더블 에스프레소 칩 프라푸치노", "스타벅스", "프라푸치노 · Tall", 130.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("스타벅스 꿀 호떡 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("에스프레소 프라푸치노", "스타벅스", "프라푸치노 · Tall", 120.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("자바 칩 프라푸치노", "스타벅스", "프라푸치노 · Tall", 100.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("카라멜 프라푸치노", "스타벅스", "프라푸치노 · Tall", 85.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("딸기 글레이즈드 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 5.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("베르가못 딸기 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("베르가못 제주 말차 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 30.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("제주 까망 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("제주 말차 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 80.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("제주 쑥떡 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("제주 오름 초당옥수수 프라푸치노", "스타벅스", "프라푸치노 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("초콜릿 크림 칩 프라푸치노", "스타벅스", "프라푸치노 · Tall", 10.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("피넛 쿠키 크림 프라푸치노", "스타벅스", "프라푸치노 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("화이트 타이거 프라푸치노", "스타벅스", "프라푸치노 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("망고 패션 프루트 블렌디드", "스타벅스", "블렌디드 · Tall", 10.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("북한산 레몬 얼 그레이 블렌디드", "스타벅스", "블렌디드 · Tall", 8.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("여수 바다 유자 블렌디드", "스타벅스", "블렌디드 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("제주 탄제린 블렌디드", "스타벅스", "블렌디드 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("딸기 딜라이트 요거트 블렌디드", "스타벅스", "블렌디드 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("말차 폭포 유자 블렌디드", "스타벅스", "블렌디드 · Tall", 65.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("망고 바나나 블렌디드", "스타벅스", "블렌디드 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("블루베리 딜라이트 요거트 블렌디드", "스타벅스", "블렌디드 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("자몽 허니 레몬 블렌디드", "스타벅스", "블렌디드 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("피치 망고 블렌디드", "스타벅스", "블렌디드 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("딸기 아사이 레모네이드 스타벅스 리프레셔", "스타벅스", "리프레셔 · Tall", 30.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("핑크 피치 리프레셔", "스타벅스", "리프레셔 · Tall", 30.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("골든 만다린 홉 피지오", "스타벅스", "피지오 · Tall", 16.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("라이트 핑크 자몽 피지오", "스타벅스", "피지오 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("리버 피치 피지오", "스타벅스", "피지오 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("서울 석양 오미자 피지오", "스타벅스", "피지오 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("여수 바다 자몽 피지오", "스타벅스", "피지오 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("쿨 라임 피지오", "스타벅스", "피지오 · Tall", 100.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("피치 딸기 피지오", "스타벅스", "피지오 · Tall", 30.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 클래식 뱅쇼 히비스커스 티", "스타벅스", "티 · ICED · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("클래식 뱅쇼 히비스커스 티", "스타벅스", "티 · HOT · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 퓨어 호지차", "스타벅스", "티 · ICED · Tall", 20.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("퓨어 호지차", "스타벅스", "티 · HOT · Tall", 25.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("살구 조이풀 메들리 티 라떼", "스타벅스", "티 라떼 · HOT · Tall", 80.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 살구 조이풀 메들리 티 라떼", "스타벅스", "티 라떼 · ICED · Tall", 50.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 차이 티 라떼", "스타벅스", "티 라떼 · ICED · Tall", 60.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 호지 글레이즈드 티 라떼", "스타벅스", "티 라떼 · ICED · Tall", 20.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("딸기 콜드폼 초콜릿", "스타벅스", "초콜릿 · Tall", 5.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("시그니처 핫 초콜릿", "스타벅스", "초콜릿 · HOT · Tall", 40.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 시그니처 초콜릿", "스타벅스", "초콜릿 · ICED · Tall", 20.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("플러피 판다 아이스 초콜릿", "스타벅스", "초콜릿 · ICED · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("플러피 판다 핫 초콜릿", "스타벅스", "초콜릿 · HOT · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("스타벅스 슬래머", "스타벅스", "기타 음료 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("스팀 우유", "스타벅스", "우유 · HOT · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("우유", "스타벅스", "우유 · Tall", 0.0, null, null, null, starbucksSource, starbucksDate, starbucksUrl),

// 할리스 공식 메뉴 영양정보 · 제공량 기준은 메뉴 상세 페이지 표기 기준
            Drink("아이스티", "할리스", "아이스티 · ICED", 11.0, null, null, null, "할리스 공식 메뉴 영양정보", "2026-11-13", "https://www.hollys.co.kr/menu/signature.do"),
            Drink("민트초코 HOT", "할리스", "라떼 · HOT", 3.0, null, null, null, "할리스 공식 메뉴 영양정보", "2026-11-13", "https://www.hollys.co.kr/menu/signature.do"),
            Drink("민트초코 ICED", "할리스", "라떼 · ICED", 3.0, null, null, null, "할리스 공식 메뉴 영양정보", "2026-11-13", "https://www.hollys.co.kr/menu/signature.do"),
            Drink("핫초코", "할리스", "초콜릿 · HOT", 8.0, null, null, null, "할리스 공식 메뉴 영양정보", "2026-11-13", "https://www.hollys.co.kr/menu/signature.do"),
            Drink("아이스초코", "할리스", "초콜릿 · ICED", 8.0, null, null, null, "할리스 공식 메뉴 영양정보", "2026-11-13", "https://www.hollys.co.kr/menu/signature.do"),
            Drink("화이트초코 HOT", "할리스", "초콜릿 · HOT", 23.0, null, null, null, "할리스 공식 메뉴 영양정보", "2026-11-13", "https://www.hollys.co.kr/menu/signature.do"),
            Drink("화이트초코 ICED", "할리스", "초콜릿 · ICED", 23.0, null, null, null, "할리스 공식 메뉴 영양정보", "2026-11-13", "https://www.hollys.co.kr/menu/signature.do"),

// 매머드커피 공식 홈페이지 메뉴명 · 2026-11-13 확인 · 영양정보 미확인 값은 null
            Drink("딸기 주스", "매머드커피", "주스 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("딸기 피치 우롱 밀크티", "매머드커피", "밀크티 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("딸기 리치 젤리티", "매머드커피", "과일티 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("딸기 리치티", "매머드커피", "과일티 · HOT", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("말차 클래식 라떼", "매머드커피", "말차 라떼", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("시그니처 라떼", "매머드커피", "라떼", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("골든 코코넛 라떼", "매머드커피", "라떼", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("로얄 밀크티", "매머드커피", "밀크티", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("매머드 커피", "매머드커피", "커피 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("스노우 매머드 커피", "매머드커피", "커피 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("허니베리 홍초 에이드", "매머드커피", "에이드", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("패션 오렌지 아이스티", "매머드커피", "아이스티", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("말차 클래식 프라페", "매머드커피", "프라페", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("말차 딸기 라떼", "매머드커피", "말차 라떼 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("말차 제주 레몬 크러쉬", "매머드커피", "크러쉬 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("말차 클라우드블루 코코넛워터", "매머드커피", "말차 음료 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),
            Drink("말차 클라우드블루 코코넛라떼", "매머드커피", "말차 라떼 · ICED", null, null, null, null, "매머드커피 공식 메뉴 · 영양정보 미확인", "2026-11-13", "https://mmthcoffee.com/"),

// 메가MGC 공식 메뉴 및 컴포즈커피 매장 주문 메뉴명 · 카페인 미확인 값은 null 유지
            Drink("무카페인 오르조라떼", "메가MGC커피", "라떼 · ICED", null, null, null, null, "메가MGC커피 공식 메뉴 · 카페인 수치 미공개", "2026-11-13", "https://www.mega-mgccoffee.com/"),
            Drink("저당 골든애플 블랙티", "메가MGC커피", "블랙티 · ICED", null, null, null, null, "메가MGC커피 공식 메뉴 · 카페인 수치 미공개", "2026-11-13", "https://www.mega-mgccoffee.com/"),
            Drink("망고폼 자스민 티플레저", "메가MGC커피", "티플레저 · ICED", null, null, null, null, "메가MGC커피 공식 메뉴명 · 영양정보 미확인", "2026-11-13", "https://www.mega-mgccoffee.com/"),
            Drink("M망고 G구아바 C캐럿 주스", "메가MGC커피", "주스 · ICED", null, null, null, null, "메가MGC커피 공식 메뉴명 · 영양정보 미확인", "2026-11-13", "https://www.mega-mgccoffee.com/"),
            Drink("제로 체리콜라", "메가MGC커피", "에이드 · ICED", null, null, null, null, "메가MGC커피 공식 메뉴명 · 영양정보 미확인", "2026-11-13", "https://www.mega-mgccoffee.com/"),
            Drink("플레인요거트 스무디", "컴포즈커피", "스무디", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("자몽에이드", "컴포즈커피", "에이드", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("레몬에이드", "컴포즈커피", "에이드", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("망고에이드", "컴포즈커피", "에이드", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("유자에이드", "컴포즈커피", "에이드", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("블루레몬에이드", "컴포즈커피", "에이드", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("청포도에이드", "컴포즈커피", "에이드", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("키위주스", "컴포즈커피", "주스", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("오렌지당근주스", "컴포즈커피", "주스", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("국내산 딸기주스", "컴포즈커피", "주스", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/120b0e36-2035-4a20-a6de-5e30562ea3cb/menus"),
            Drink("망고스무디", "컴포즈커피", "스무디", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/290172d6-2c48-4c0c-bd12-ac838bffdbd8/menus"),
            Drink("블루베리스무디", "컴포즈커피", "스무디", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/290172d6-2c48-4c0c-bd12-ac838bffdbd8/menus"),
            Drink("딸기요거트스무디", "컴포즈커피", "스무디", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/290172d6-2c48-4c0c-bd12-ac838bffdbd8/menus"),
            Drink("망고요거트스무디", "컴포즈커피", "스무디", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/290172d6-2c48-4c0c-bd12-ac838bffdbd8/menus"),
            Drink("블루베리요거트스무디", "컴포즈커피", "스무디", null, null, null, null, "컴포즈커피 매장 주문 메뉴 · 카페인 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/290172d6-2c48-4c0c-bd12-ac838bffdbd8/menus"),

            // 더벤티·파스쿠찌 매장 주문 메뉴명 · 제3자 주문 페이지 확인, 카페인 함량 미확인
            Drink("(핫)하동매실티", "더벤티", "티 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/a089dc7b-6d33-472e-bf66-d25e13144aa5/menus"),
            Drink("(아이스)하동매실티", "더벤티", "티 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/a089dc7b-6d33-472e-bf66-d25e13144aa5/menus"),
            Drink("(핫)보성호지차라떼", "더벤티", "차 라떼 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/a089dc7b-6d33-472e-bf66-d25e13144aa5/menus"),
            Drink("알찬 포도 에이드", "더벤티", "에이드", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/a089dc7b-6d33-472e-bf66-d25e13144aa5/menus"),
            Drink("알찬 포도 요거트 스무디", "더벤티", "스무디", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/a089dc7b-6d33-472e-bf66-d25e13144aa5/menus"),
            Drink("(핫)알찬 옥수수팝 라떼", "더벤티", "라떼 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/a089dc7b-6d33-472e-bf66-d25e13144aa5/menus"),
            Drink("(아이스)알찬 옥수수팝 라떼", "더벤티", "라떼 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/a089dc7b-6d33-472e-bf66-d25e13144aa5/menus"),
            Drink("쿠키앤크림프라페", "더벤티", "프라페", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("자바칩프라페", "더벤티", "프라페", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("코코넛커피프라페", "더벤티", "커피 프라페", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("솔티드카라멜프라페", "더벤티", "프라페", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("제로 복숭아 아이스티", "더벤티", "아이스티", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("제로 매실 아이스티", "더벤티", "아이스티", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("제로 오렌지 멜론 머틀티", "더벤티", "블렌딩티", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("복숭아아이스티", "더벤티", "아이스티", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/9a51c10b-44f1-4094-9db2-658f20411e40/menus"),
            Drink("(핫)멜팅초코", "더벤티", "초콜릿 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/ecd63ea2-9fea-4d56-ae6d-e9fb91a98ce5/menus"),
            Drink("(아이스)멜팅초코", "더벤티", "초콜릿 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/39e09565-b7e4-4812-9203-4f3cf872859f/menus"),
            Drink("딸기라떼", "더벤티", "라떼 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/ecd63ea2-9fea-4d56-ae6d-e9fb91a98ce5/menus"),
            Drink("딸기연유라떼", "더벤티", "라떼 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/ecd63ea2-9fea-4d56-ae6d-e9fb91a98ce5/menus"),
            Drink("(아이스)미숫가루라떼", "더벤티", "곡물 라떼 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/ecd63ea2-9fea-4d56-ae6d-e9fb91a98ce5/menus"),
            Drink("(핫)초코라떼", "더벤티", "초콜릿 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/39e09565-b7e4-4812-9203-4f3cf872859f/menus"),
            Drink("(아이스)초코라떼", "더벤티", "초콜릿 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/39e09565-b7e4-4812-9203-4f3cf872859f/menus"),
            Drink("HOT)얼그레이", "파스쿠찌", "홍차 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("HOT)오렌지 루이보스", "파스쿠찌", "루이보스 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("HOT)플로럴 캐모마일", "파스쿠찌", "허브티 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("HOT)민트크루", "파스쿠찌", "허브티 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("HOT)프루티 하동 녹차", "파스쿠찌", "녹차 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("HOT)오렌지홍차", "파스쿠찌", "홍차 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("ICED)얼그레이", "파스쿠찌", "홍차 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("ICED)오렌지 루이보스", "파스쿠찌", "루이보스 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("ICED)플로럴 캐모마일", "파스쿠찌", "허브티 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("ICED)민트크루", "파스쿠찌", "허브티 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("ICED)프루티 하동 녹차", "파스쿠찌", "녹차 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("ICED)오렌지홍차", "파스쿠찌", "홍차 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("HOT)딸기 히비스커스", "파스쿠찌", "허브티 · HOT", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),
            Drink("ICED)딸기 히비스커스", "파스쿠찌", "허브티 · ICED", null, null, null, null, "매장 주문 메뉴명 확인 · 카페인 수치 미확인", "2026-11-13", "https://app.passorder.co.kr/normal/b2be113d-ed34-412e-9624-439a91d0d684/menus"),

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
    var dataFilter by remember { mutableStateOf("전체 데이터") }
    var selectedBrand by remember { mutableStateOf("전체") }
    var selectedCategory by remember { mutableStateOf("전체") }
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
    val categories = listOf("전체") + drinks.map { it.kind.substringBefore(" · ") }.distinct().sorted()
    val officialKnownCount = drinks.count { it.caffeine != null && it.verified }
    val unknownCount = drinks.count { it.caffeine == null }
    val referenceCount = drinks.count { it.caffeine != null && !it.verified }
    val filtered = drinks.filter {
        (it.name.contains(query, true) || it.brand.contains(query, true)) &&
            (selectedBrand == "전체" || it.brand == selectedBrand) &&
            (selectedCategory == "전체" || it.kind.substringBefore(" · ") == selectedCategory) &&
            when (dataFilter) {
                "공식·공공기관 확인" -> it.caffeine != null && it.verified
                "카페인 미확인" -> it.caffeine == null
                "참고값" -> it.caffeine != null && !it.verified
                else -> true
            } &&
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
                        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF102B38))) {
                            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("국내 카페 메뉴 데이터 현황", color = Ink, fontWeight = FontWeight.Bold)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column { Text("${drinks.size}", color = Teal, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("전체 메뉴", color = Color(0xFFBBD0D5), fontSize = 11.sp) }
                                    Column { Text("$officialKnownCount", color = Teal, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("공식·공공기관 확인", color = Color(0xFFBBD0D5), fontSize = 11.sp) }
                                    Column { Text("$unknownCount", color = Color(0xFFFFC56B), fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("카페인 미확인", color = Color(0xFFBBD0D5), fontSize = 11.sp) }
                                    Column { Text("$referenceCount", color = Color(0xFFFFC56B), fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("참고값", color = Color(0xFFBBD0D5), fontSize = 11.sp) }
                                }
                                Text("공식 확인율 ${if (drinks.isEmpty()) 0 else officialKnownCount * 100 / drinks.size}% · 미확인 메뉴도 검색할 수 있어요.", color = Color(0xFFBBD0D5), fontSize = 11.sp)
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
                        Text("데이터 상태", color = Color(0xFFBBD0D5), fontSize = 12.sp)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("전체 데이터", "공식·공공기관 확인", "카페인 미확인", "참고값").forEach { option ->
                                FilterChip(selected = dataFilter == option, onClick = { dataFilter = option }, label = { Text(option) })
                            }
                        }
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
                        Text("음료 종류", color = Color(0xFFBBD0D5), fontSize = 12.sp)
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                FilterChip(selected = selectedCategory == category, onClick = { selectedCategory = category }, label = { Text(category) })
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

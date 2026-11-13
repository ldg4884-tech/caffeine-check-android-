package kr.co.caffeinecheck.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val sourceUrl: String = "https://www.kca.go.kr/home/sub.do?menukey=4004&mode=view&no=1004472111"
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
            Drink("녹차라떼", "메가MGC커피", "말차·녹차 라떼", 45, 46.0, null, 410, source, date),
            Drink("말차라떼", "빽다방", "말차·녹차 라떼", 93, null, null, 350, source, date),
            Drink("제주 말차 라떼", "스타벅스", "말차·녹차 라떼", 81, 26.0, null, 285, source, date),
            Drink("말차라떼", "이디야커피", "말차·녹차 라떼", 70, null, null, null, source, date),
            Drink("그린티라떼", "컴포즈커피", "말차·녹차 라떼", 68, 50.0, null, 349, source, date),
            Drink("그린티 라떼", "투썸플레이스", "말차·녹차 라떼", 65, null, null, null, source, date),
            Drink("로얄밀크티라떼", "메가MGC커피", "밀크티", 57, null, null, 397, source, date),
            Drink("밀크티", "빽다방", "밀크티", 113, 42.0, null, 359, source, date),
            Drink("클래식 밀크 티", "스타벅스", "밀크티", 172, null, null, 311, source, date),
            Drink("로열밀크티", "이디야커피", "밀크티", 73, null, null, null, source, date),
            Drink("밀크티", "컴포즈커피", "밀크티", 88, null, null, null, source, date),
            Drink("로얄 밀크티", "투썸플레이스", "밀크티", 148, 27.0, null, 276, source, date, kcaUrl),

            // 빽다방 공식 메뉴 페이지: 메뉴별 HOT/ICED와 컵 용량을 별도 음료로 등록
            Drink("블랙 벨벳 말차라떼 ICED", "빽다방", "말차·녹차 라떼 · ICED", 66, 51.4, 545, 660, paikSource, paikDate, paikUrl),
            Drink("호지차라떼 HOT", "빽다방", "차 라떼 · HOT", 87.5, 47.5, null, 510, paikSource, paikDate, paikUrl),
            Drink("호지차라떼 ICED", "빽다방", "차 라떼 · ICED", 68.2, 34.3, 305, 660, paikSource, paikDate, paikUrl),
            Drink("우바홍차 HOT", "빽다방", "홍차 · HOT", 59.3, 0.0, null, 510, paikSource, paikDate, paikUrl),
            Drink("우바홍차 ICED", "빽다방", "홍차 · ICED", 53.2, 0.0, null, 660, paikSource, paikDate, paikUrl),
            Drink("리치루이보스티 HOT", "빽다방", "허브티 · HOT", 0, 38.5, 180, 510, paikSource, paikDate, paikUrl),
            Drink("리치루이보스티 ICED", "빽다방", "허브티 · ICED", 0, 28.4, 140, 660, paikSource, paikDate, paikUrl),

            // 스타벅스 공식 영양정보: 해당 페이지의 기본 Tall 기준
            Drink("아이스 퓨어 말차", "스타벅스", "말차 · ICED · Tall", 60, 0.0, 9, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("퓨어 말차", "스타벅스", "말차 · HOT · Tall", 75, 0.0, 11, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("말차 글레이즈드 티 라떼", "스타벅스", "말차 라떼 · HOT · Tall", 65, 29.0, 310, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("아이스 말차 글레이즈드 티 라떼", "스타벅스", "말차 라떼 · ICED · Tall", 60, 23.0, 235, null, starbucksSource, starbucksDate, starbucksUrl),
            Drink("클래식 밀크티 블렌디드", "스타벅스", "밀크티 블렌디드 · Tall", 60, 37.0, 315, null, starbucksSource, starbucksDate, starbucksUrl)
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
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("전체", "100mg 이하").forEach { option ->
                                FilterChip(selected = caffeineFilter == option, onClick = { caffeineFilter = option }, label = { Text(option) })
                            }
                        }
                    }
                    item {
                        Text("브랜드", color = Color(0xFFBBD0D5), fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                put("verified", drink.caffeine != null)
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
                    item { Text("※ 카페인 수치는 한국소비자원 2026년 시험 대상 제품의 1잔 기준입니다. 사이즈·제조 옵션에 따라 달라질 수 있습니다.", color = Color(0xFF91AAB2), fontSize = 11.sp) }
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
                                    Text(if (record.optBoolean("known")) "${formatMg(record.optDouble("caffeine"))} mg" else "카페인 정보 확인 필요", color = Color.Gray, fontSize = 12.sp)
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
                                put("verified", drink.caffeine != null); put("source", drink.source); put("checkedDate", drink.checkedDate); put("volumeMl", drink.volumeMl ?: JSONObject.NULL); put("sugar", drink.sugar ?: JSONObject.NULL); put("kcal", drink.kcal ?: JSONObject.NULL); put("time", System.currentTimeMillis())
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

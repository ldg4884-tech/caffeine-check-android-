package kr.co.caffeinecheck.app

import android.content.Context
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject

private val Navy = Color(0xFF071923)
private val Teal = Color(0xFF43D6C2)
private val Pale = Color(0xFF071923)
private val Ink = Color(0xFFF1FAFA)

data class Drink(val name: String, val brand: String, val kind: String, val caffeine: Int?, val sugar: Double?, val kcal: Int?)

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
        listOf(
            Drink("아메리카노", "스타벅스", "HOT", 150, 0.0, 10),
            Drink("아이스 아메리카노", "스타벅스", "ICE", 150, 0.0, 10),
            Drink("카페 라떼", "스타벅스", "HOT", 75, 13.0, 180),
            Drink("아이스 카페 라떼", "스타벅스", "ICE", 75, 8.0, 110),
            Drink("플러피 판다 핫 초콜릿", "스타벅스", "HOT", 0, 48.0, 470),
            Drink("하우스밀크 라떼", "메가MGC", "HOT", 118, 15.8, 329),
            Drink("오르조라떼", "메가MGC", "HOT", 0, 21.7, 239),
            Drink("리치루이보스티", "빽다방", "HOT", 0, null, null)
        )
    }
    val prefs = remember { context.getSharedPreferences("caffeine_check", Context.MODE_PRIVATE) }
    var query by remember { mutableStateOf("") }
    var zeroOnly by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var consumed by remember { mutableStateOf(loadRecords(prefs)) }
    val filtered = drinks.filter {
        (it.name.contains(query, true) || it.brand.contains(query, true)) &&
            (!zeroOnly || it.caffeine == 0)
    }
    val todayRecords = consumed.filter { dateKey(it.optLong("time", 0L)) == dateKey(System.currentTimeMillis()) }
    val todayTotal = todayRecords.filter { it.optBoolean("known", false) && it.optBoolean("verified", false) }.sumOf { it.optInt("caffeine", 0) }
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
                    NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = {}, label = { Text("오늘 기록") })
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
                                    Text("$todayTotal", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Bold)
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
                        FilterChip(
                            selected = zeroOnly, onClick = { zeroOnly = !zeroOnly },
                            label = { Text("카페인 0mg만 보기") }
                        )
                    }
                    items(filtered) { drink ->
                        DrinkCard(drink) {
                            val record = JSONObject().apply {
                                put("name", drink.name)
                                put("brand", drink.brand)
                                put("caffeine", drink.caffeine ?: 0)
                                put("known", drink.caffeine != null)
                                put("verified", false)
                                put("time", System.currentTimeMillis())
                            }
                            consumed = consumed + record
                            saveRecords(prefs, consumed)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("오늘 섭취 기록", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text("확인된 카페인 수치만 합산합니다.", color = Color.Gray, fontSize = 13.sp)
                        Text("합계 $todayTotal mg", fontSize = 28.sp, color = Teal, fontWeight = FontWeight.Bold)
                    }
                    items(consumed.reversed()) { record ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF102B38))) {
                            Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(record.optString("brand") + " · " + record.optString("name"), fontWeight = FontWeight.SemiBold)
                                    Text(if (record.optBoolean("known")) "${record.optInt("caffeine")} mg" else "카페인 정보 확인 필요", color = Color.Gray, fontSize = 12.sp)
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
            }
        }
    }
}

@Composable
private fun DrinkCard(drink: Drink, onAdd: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF102B38)), shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(drink.brand, fontSize = 12.sp, color = Teal, fontWeight = FontWeight.Bold)
                    Text(drink.name, fontSize = 17.sp, color = Ink, fontWeight = FontWeight.Bold)
                    Text(drink.kind, fontSize = 11.sp, color = Color.Gray)
                }
                Text(if (drink.caffeine == null) "미확인" else "${drink.caffeine} mg",
                    color = if (drink.caffeine == 0) Teal else Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("열량 ${drink.kcal?.let { "${it}kcal" } ?: "미확인"}  ·  당류 ${drink.sugar?.let { "${it}g" } ?: "미확인"}",
                    fontSize = 12.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                Button(onClick = onAdd, colors = ButtonDefaults.buttonColors(containerColor = Teal)) { Text("마셨어요") }
            }
        }
    }
}

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

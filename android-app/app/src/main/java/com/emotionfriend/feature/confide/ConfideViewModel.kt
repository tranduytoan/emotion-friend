package com.emotionfriend.feature.confide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.remote.ApiResult
import com.emotionfriend.data.remote.OpenAIApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer
import javax.inject.Inject

enum class MessageRole { USER, ASSISTANT }

/** Navigation suggestion from cô Vy: which feature to open next. */
enum class ConfideSuggestion(val label: String, val emoji: String) {
    STORY("Nghe câu chuyện", "📖"),
    RELAX("Thư giãn", "🌈"),
    BREATHING("Hít thở cùng cô", "🌬️"),
}

data class ConfideMessage(
    val role       : MessageRole,
    val text       : String,
    val isError    : Boolean = false,
    val suggestion : ConfideSuggestion? = null,
)

data class ConfideUiState(
    val messages   : List<ConfideMessage> = emptyList(),
    val isLoading  : Boolean = false,
    val inputText  : String  = "",
)

@HiltViewModel
class ConfideViewModel @Inject constructor(
    private val openAI: OpenAIApiClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfideUiState())
    val uiState: StateFlow<ConfideUiState> = _uiState.asStateFlow()

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    /** Called from both keyboard send and STT mic. */
    fun sendMessage(textOverride: String? = null) {
        val text = (textOverride ?: _uiState.value.inputText).trim()
        if (text.isBlank()) return

        val userMsg = ConfideMessage(role = MessageRole.USER, text = text)
        _uiState.update { state ->
            state.copy(
                messages  = state.messages + userMsg,
                inputText = "",
                isLoading = true,
            )
        }

        viewModelScope.launch {
            when (val result = openAI.chat(text)) {
                is ApiResult.Success -> {
                    val raw        = result.data
                    val suggestion = parseSuggestion(raw)
                    val displayText = raw
                        .replace("[SUGGEST:STORY]",     "")
                        .replace("[SUGGEST:RELAX]",     "")
                        .replace("[SUGGEST:BREATHING]", "")
                        .trim()
                    val botMsg = ConfideMessage(
                        role       = MessageRole.ASSISTANT,
                        text       = displayText,
                        suggestion = suggestion,
                    )
                    _uiState.update { it.copy(messages = it.messages + botMsg, isLoading = false) }
                }
                is ApiResult.Error -> {
                    // API failed — use keyword fallback so conversation still works
                    val fallback = buildFallbackResponse(text)
                    _uiState.update {
                        it.copy(
                            messages  = it.messages + fallback,
                            isLoading = false,
                        )
                    }
                }
            }
        }
    }

    // ─── Suggestion parsing ──────────────────────────────────────────────────

    private fun parseSuggestion(text: String): ConfideSuggestion? = when {
        "[SUGGEST:STORY]"     in text -> ConfideSuggestion.STORY
        "[SUGGEST:RELAX]"     in text -> ConfideSuggestion.RELAX
        "[SUGGEST:BREATHING]" in text -> ConfideSuggestion.BREATHING
        else                          -> null
    }

    // ─── Keyword-based fallback (no API needed) ──────────────────────────────
    // This fallback is intentionally broad because it is used only when the API
    // is unavailable. It keeps cô Vy responsive by matching hundreds of childlike
    // phrases, common Vietnamese accent/no-accent variants, short emotion words,
    // school/family/friend situations, and direct feature requests.

    private data class FallbackRule(
        val keywords: List<String>,
        val text: String,
        val suggestion: ConfideSuggestion? = null,
    )

    private val fallbackRules: List<FallbackRule> = listOf(
        // ── Immediate safety and sensitive situations: always handled first ──
        FallbackRule(
            keywords = listOf(
                "muốn chết", "muon chet", "không muốn sống", "khong muon song", "con muốn biến mất", "con muon bien mat",
                "con ghét cuộc sống", "con ghet cuoc song", "con muốn tự làm đau", "con muon tu lam dau", "tự tử", "tu tu",
                "con sẽ làm đau mình", "con se lam dau minh", "con muốn ngủ mãi", "con muon ngu mai", "chết đi cho xong",
                "chet di cho xong", "con không cần ai nữa", "con khong can ai nua", "con muốn kết thúc", "con muon ket thuc"
            ),
            text = "Cô Vy rất lo cho con khi nghe điều này. Con hãy dừng lại một chút, tránh xa những thứ có thể làm con đau, và gọi ngay cho bố mẹ, thầy cô hoặc một người lớn ở gần con nhé. Con không cần ở một mình lúc này đâu 💛",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "bị đánh", "bi danh", "đánh con", "danh con", "bố đánh", "bo danh", "mẹ đánh", "me danh", "anh đánh",
                "chị đánh", "cô đánh", "thầy đánh", "bị tát", "bi tat", "bị đá", "bi da", "bị đẩy", "bi day",
                "bị làm đau", "bi lam dau", "ai đó làm đau", "ai do lam dau", "con bị đau vì người khác", "con bi dau vi nguoi khac"
            ),
            text = "Cô Vy nghe con nói và cô rất thương con. Nếu có ai làm con đau, con hãy đến chỗ an toàn và nói ngay với một người lớn đáng tin như bố mẹ, ông bà, thầy cô hoặc cô chú gần con nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "người lạ", "nguoi la", "người lạ rủ", "nguoi la ru", "người lạ cho kẹo", "nguoi la cho keo",
                "người lạ đi theo", "nguoi la di theo", "ai đó đi theo", "ai do di theo", "bị theo dõi", "bi theo doi",
                "con bị kéo đi", "con bi keo di", "rủ đi chơi một mình", "ru di choi mot minh", "đừng nói với ai", "dung noi voi ai",
                "giữ bí mật với bố mẹ", "giu bi mat voi bo me"
            ),
            text = "Nếu con thấy không an toàn, con hãy đi ngay tới chỗ đông người hoặc tới bên người lớn mà con tin tưởng. Con không cần giữ bí mật khi điều đó làm con sợ đâu nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "đụng vào người", "dung vao nguoi", "chạm vào người", "cham vao nguoi", "sờ vào", "so vao", "chạm chỗ riêng tư",
                "cham cho rieng tu", "bắt con giữ bí mật", "bat con giu bi mat", "con thấy không an toàn", "con thay khong an toan",
                "ai đó làm con sợ", "ai do lam con so", "con không thích người đó chạm vào", "con khong thich nguoi do cham vao"
            ),
            text = "Cơ thể của con là của con. Nếu ai đó chạm vào con làm con sợ hoặc khó chịu, con hãy nói 'không', rời khỏi đó nếu có thể, và kể ngay với bố mẹ, thầy cô hoặc một người lớn đáng tin nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Direct feature requests ──
        FallbackRule(
            keywords = listOf(
                "kể chuyện", "ke chuyen", "nghe chuyện", "nghe chuyen", "câu chuyện", "cau chuyen", "truyện", "truyen",
                "đọc truyện", "doc truyen", "chuyện cổ tích", "chuyen co tich", "truyện cổ tích", "truyen co tich",
                "kể chuyện vui", "ke chuyen vui", "kể chuyện trước khi ngủ", "ke chuyen truoc khi ngu", "con muốn nghe truyện",
                "con muon nghe truyen", "cô kể cho con nghe", "co ke cho con nghe", "story", "tell story"
            ),
            text = "Cô có nhiều câu chuyện dịu dàng lắm. Mình cùng nghe một câu chuyện để trái tim nhẹ hơn nhé 📖",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "thư giãn", "thu gian", "nghỉ ngơi", "nghi ngoi", "nghe nhạc", "nghe nhac", "âm nhạc", "am nhac",
                "nhạc nhẹ", "nhac nhe", "nhạc ru ngủ", "nhac ru ngu", "nhạc yên tĩnh", "nhac yen tinh", "nhạc êm",
                "nhac em", "con muốn thư giãn", "con muon thu gian", "mở nhạc", "mo nhac", "relax", "music", "calm music"
            ),
            text = "Được rồi, cô Vy sẽ đưa con tới một không gian thật nhẹ nhàng. Mình nghe nhạc và thả lỏng một chút nhé 🌈",
            suggestion = ConfideSuggestion.RELAX,
        ),
        FallbackRule(
            keywords = listOf(
                "hít thở", "hit tho", "thở", "tho", "thở sâu", "tho sau", "tập thở", "tap tho", "bình tĩnh",
                "binh tinh", "con muốn bình tĩnh", "con muon binh tinh", "khó thở", "kho tho", "tim đập nhanh", "tim dap nhanh",
                "run quá", "run qua", "giúp con bình tĩnh", "giup con binh tinh", "breathing", "breathe", "calm down"
            ),
            text = "Mình cùng hít vào thật chậm, rồi thở ra nhẹ nhàng nhé. Cô Vy sẽ ở đây và cùng con bình tĩnh lại 🌬️",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Greeting, identity, capability ──
        FallbackRule(
            keywords = listOf(
                "xin chào", "xin chao", "chào cô", "chao co", "chào cô vy", "chao co vy", "cô vy ơi", "co vy oi",
                "hello", "hi", "alo", "ê cô", "e co", "cô ơi", "co oi", "chào buổi sáng", "chao buoi sang",
                "chào buổi tối", "chao buoi toi", "con chào cô", "con chao co"
            ),
            text = "Cô Vy chào con nè 😊 Hôm nay trong lòng con đang thế nào? Con có thể kể cho cô nghe từng chút một nhé.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "cô là ai", "co la ai", "bạn là ai", "ban la ai", "cô vy là ai", "co vy la ai", "ai đang nói",
                "ai dang noi", "cô tên gì", "co ten gi", "tên cô là gì", "ten co la gi", "who are you", "what are you"
            ),
            text = "Cô là cô Vy, người bạn nhỏ luôn sẵn sàng lắng nghe con. Con có thể kể chuyện vui, chuyện buồn, chuyện ở lớp hoặc chuyện trong nhà cho cô nghe nhé.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "cô làm được gì", "co lam duoc gi", "cô giúp được gì", "co giup duoc gi", "ứng dụng này làm gì", "ung dung nay lam gi",
                "con nói gì được", "con noi gi duoc", "giúp con", "giup con", "help me", "help", "cần giúp", "can giup"
            ),
            text = "Cô Vy có thể lắng nghe con tâm sự, gợi ý hít thở khi con lo lắng, mở nhạc thư giãn khi con mệt, hoặc dẫn con tới một câu chuyện dịu dàng khi con buồn.",
            suggestion = null,
        ),

        // ── More nuanced emotion words and blended feelings ──
        FallbackRule(
            keywords = listOf(
                "vừa vui vừa buồn", "vua vui vua buon", "lẫn lộn", "lan lon", "rối", "roi", "rối quá", "roi qua",
                "khó hiểu", "kho hieu", "không hiểu cảm xúc", "khong hieu cam xuc", "mixed feelings", "confused"
            ),
            text = "Có lúc trong lòng mình có nhiều cảm xúc cùng một lúc. Con không cần gọi tên đúng ngay. Mình hít thở chậm rồi kể từng phần nhỏ cho cô Vy nghe nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Sadness, crying, loneliness, missing someone ──
        FallbackRule(
            keywords = listOf(
                "buồn", "buon", "rất buồn", "rat buon", "buồn quá", "buon qua", "không vui", "khong vui", "không hạnh phúc",
                "khong hanh phuc", "chán đời", "chan doi", "tủi thân", "tui than", "não nề", "nao ne", "thất vọng", "that vong",
                "sad", "unhappy", "con buồn", "con buon", "con thấy buồn", "con thay buon", "hôm nay buồn", "hom nay buon"
            ),
            text = "Cô Vy hiểu con đang buồn. Nỗi buồn không xấu đâu, nó chỉ đang nói rằng trái tim con cần được ôm ấp một chút. Để cô kể một câu chuyện nhẹ nhàng cho con nhé 💛",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "khóc", "khoc", "muốn khóc", "muon khoc", "con khóc", "con khoc", "khóc nhiều", "khoc nhieu", "nước mắt",
                "nuoc mat", "rơi nước mắt", "roi nuoc mat", "khóc một mình", "khoc mot minh", "cry", "crying", "tears"
            ),
            text = "Con có thể khóc nếu con cần. Khóc không làm con yếu đuối đâu, nó giúp lòng mình nhẹ hơn. Cô Vy ở đây lắng nghe con nhé.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "cô đơn", "co don", "một mình", "mot minh", "không ai chơi", "khong ai choi", "không ai hiểu", "khong ai hieu",
                "không có bạn", "khong co ban", "bị bỏ rơi", "bi bo roi", "không ai thương", "khong ai thuong", "lonely",
                "alone", "con chỉ có một mình", "con chi co mot minh", "con không có ai", "con khong co ai"
            ),
            text = "Cảm giác cô đơn làm mình buồn lắm. Nhưng ngay lúc này, cô Vy đang lắng nghe con. Con kể cho cô nghe chuyện gì làm con thấy một mình nhé.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "nhớ mẹ", "nho me", "nhớ bố", "nho bo", "nhớ ba", "nho ba", "nhớ ông", "nho ong", "nhớ bà", "nho bà",
                "nhớ nhà", "nho nha", "nhớ anh", "nho anh", "nhớ chị", "nho chi", "mẹ đi vắng", "me di vang",
                "bố đi vắng", "bo di vang", "xa mẹ", "xa me", "xa bố", "xa bo", "miss mom", "miss dad", "miss home"
            ),
            text = "Khi nhớ người mình thương, tim có thể thấy trống trải. Con có thể ôm gối, vẽ một bức tranh, hoặc kể cho cô nghe con nhớ điều gì nhất về người đó nhé.",
            suggestion = ConfideSuggestion.STORY,
        ),

        // ── Anger, frustration, jealousy, conflict energy ──
        FallbackRule(
            keywords = listOf(
                "tức", "tuc", "giận", "gian", "bực", "buc", "nổi giận", "noi gian", "cáu", "cau", "cáu gắt",
                "cau gat", "điên quá", "dien qua", "tức quá", "tuc qua", "bực mình", "buc minh", "khó chịu", "kho chiu",
                "angry", "mad", "annoyed", "con đang giận", "con dang gian", "con bực lắm", "con buc lam"
            ),
            text = "Cô Vy thấy con đang rất bực. Trước khi nói hay làm gì, mình thử hít vào thật sâu và thở ra chậm để cơn giận nhỏ lại nhé 🌬️",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "ghét", "ghet", "con ghét bạn", "con ghet ban", "con ghét em", "con ghet em", "không thích bạn ấy", "khong thich ban ay",
                "ghét tất cả", "ghet tat ca", "muốn hét", "muon het", "muốn đập", "muon dap", "muốn đánh", "muon danh",
                "muốn ném đồ", "muon nem do", "con muốn phá", "con muon pha"
            ),
            text = "Khi giận quá, cơ thể mình muốn hét hoặc đập đồ. Mình có thể dừng lại, nắm tay rồi thả ra, hít thở chậm, và nói: 'Con đang rất giận'. Cô Vy cùng con bình tĩnh nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "ghen tị", "ghen ti", "ghen tỵ", "gen ti", "ganh tị", "ganh ti", "bạn giỏi hơn", "ban gioi hon",
                "em được khen", "em duoc khen", "bạn được khen", "ban duoc khen", "con không bằng bạn", "con khong bang ban",
                "con thua bạn", "con thua ban", "jealous", "envy"
            ),
            text = "Ghen tị đôi khi xuất hiện khi con cũng muốn được yêu thương hoặc được công nhận. Con vẫn có điểm tốt của riêng mình, và cô Vy tin con có thể tiến bộ từng chút một.",
            suggestion = null,
        ),

        // ── Anxiety, fear, panic, uncertainty ──
        FallbackRule(
            keywords = listOf(
                "lo", "lo lắng", "lo lang", "lo quá", "lo qua", "bồn chồn", "bon chon", "hồi hộp", "hoi hop", "run",
                "run quá", "run qua", "sợ sai", "so sai", "không yên tâm", "khong yen tam", "worry", "worried", "anxious",
                "nervous", "con lo", "con lo quá", "con lo qua", "con hồi hộp", "con hoi hop"
            ),
            text = "Con đang lo lắng à? Mình không cần giải quyết mọi thứ ngay lập tức. Trước tiên, con đặt tay lên bụng, hít vào chậm và thở ra thật nhẹ cùng cô nhé 🌬️",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "sợ", "so", "sợ quá", "so qua", "rất sợ", "rat so", "hoảng", "hoang", "hoảng sợ", "hoang so",
                "hãi", "hai", "kinh hãi", "kinh hai", "afraid", "scared", "fear", "terrified", "con sợ", "con so"
            ),
            text = "Cô Vy ở đây với con. Nếu con đang ở nơi an toàn, mình cùng hít thở chậm. Nếu con thấy nguy hiểm, con hãy tìm ngay người lớn ở gần con nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "ác mộng", "ac mong", "mơ xấu", "mo xau", "gặp ác mộng", "gap ac mong", "ngủ mơ", "ngu mo",
                "con mơ thấy", "con mo thay", "sợ ngủ", "so ngu", "không dám ngủ", "khong dam ngu", "nightmare", "bad dream"
            ),
            text = "Ác mộng làm con sợ, nhưng nó chỉ là một giấc mơ thôi. Con thử ôm gối, nhìn quanh phòng để biết mình đang an toàn, rồi nghe một câu chuyện nhẹ nhàng nhé 📖",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "sợ bóng tối", "so bong toi", "sợ tối", "so toi", "tối quá", "toi qua", "phòng tối", "phong toi",
                "ma", "sợ ma", "so ma", "quái vật", "quai vat", "monster", "ghost", "dark", "darkness"
            ),
            text = "Bóng tối có thể làm trí tưởng tượng của mình chạy nhanh hơn. Con thử bật đèn nhỏ, ôm món đồ con thích, rồi hít thở chậm cùng cô Vy nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "sấm", "sam", "sét", "set", "mưa to", "mua to", "bão", "bao", "sợ sấm", "so sam", "sợ sét", "so set",
                "tiếng động lớn", "tieng dong lon", "ồn quá", "on qua", "loud", "thunder", "storm"
            ),
            text = "Âm thanh lớn có thể làm con giật mình. Con thử che tai nhẹ, ngồi gần người thân nếu có thể, rồi thở chậm cùng cô nhé 🌬️",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Tired, bored, overstimulated, sleep ──
        FallbackRule(
            keywords = listOf(
                "mệt", "met", "mệt mỏi", "met moi", "kiệt sức", "kiet suc", "đuối", "duoi", "hết sức", "het suc",
                "uể oải", "ue oai", "mệt quá", "met qua", "tired", "exhausted", "fatigue", "con mệt", "con met"
            ),
            text = "Con có vẻ đã dùng rất nhiều năng lượng rồi. Mình nghỉ một chút, nghe nhạc nhẹ và để cơ thể được thả lỏng nhé 🌈",
            suggestion = ConfideSuggestion.RELAX,
        ),
        FallbackRule(
            keywords = listOf(
                "buồn ngủ", "buon ngu", "ngủ", "ngu", "khó ngủ", "kho ngu", "không ngủ được", "khong ngu duoc",
                "trằn trọc", "tran troc", "muốn ngủ", "muon ngu", "sleepy", "sleep", "can't sleep", "insomnia"
            ),
            text = "Đến lúc cơ thể cần nghỉ rồi đó. Con thử nằm thoải mái, thả lỏng vai, hít thở chậm và nghe âm thanh dịu nhẹ nhé 🌈",
            suggestion = ConfideSuggestion.RELAX,
        ),
        FallbackRule(
            keywords = listOf(
                "chán", "chan", "chán quá", "chan qua", "không có gì làm", "khong co gi lam", "buồn chán", "buon chan",
                "bored", "boring", "nhàm chán", "nham chan", "con chán", "con chan", "con không biết chơi gì", "con khong biet choi gi"
            ),
            text = "Khi chán, mình có thể thử một điều nhỏ mới: nghe một câu chuyện, vẽ một hình, hoặc nghe nhạc nhẹ. Con muốn cô Vy dẫn con tới một câu chuyện không? 📖",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "ồn", "on", "ồn ào", "on ao", "đau tai", "dau tai", "nhiều tiếng quá", "nhieu tieng qua",
                "quá nhiều thứ", "qua nhieu thu", "không chịu nổi", "khong chiu noi", "overwhelmed", "too much", "stress"
            ),
            text = "Có vẻ xung quanh đang quá nhiều thứ với con. Mình tìm một góc yên hơn nếu có thể, rồi cùng cô thở chậm để cơ thể dịu lại nhé 🌬️",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Happiness, excitement, pride, gratitude ──
        FallbackRule(
            keywords = listOf(
                "vui", "hạnh phúc", "hanh phuc", "vui quá", "vui qua", "rất vui", "rat vui", "thích quá", "thich qua",
                "happy", "glad", "excited", "yay", "yeah", "con vui", "con vui quá", "con vui qua", "hôm nay vui", "hom nay vui"
            ),
            text = "Nghe con vui, cô Vy cũng vui lây nè 😊 Con muốn kể cho cô nghe điều gì đã làm hôm nay trở nên vui như vậy không?",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "con làm được rồi", "con lam duoc roi", "con thắng", "con thang", "con giỏi", "con gioi", "được khen", "duoc khen",
                "điểm cao", "diem cao", "được điểm tốt", "duoc diem tot", "hoàn thành", "hoan thanh", "tự hào", "tu hao",
                "proud", "i did it", "good job"
            ),
            text = "Tuyệt quá! Cô Vy rất tự hào vì con đã cố gắng. Con nhớ cảm giác này nhé, nó là phần thưởng cho sự nỗ lực của con đó 🌟",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "cảm ơn", "cam on", "cám ơn", "thank", "thanks", "thank you", "cô tốt quá", "co tot qua",
                "yêu cô", "yeu co", "con thích cô", "con thich co", "cô dễ thương", "co de thuong"
            ),
            text = "Cô Vy cũng rất vui khi được ở bên con 💛 Khi nào con muốn kể chuyện, cô luôn sẵn sàng lắng nghe nhé.",
            suggestion = null,
        ),

        // ── School: tests, homework, teachers, classmates, first day ──

        FallbackRule(
            keywords = listOf(
                "bài tập", "bai tap", "nhiều bài", "nhieu bai", "làm bài", "lam bai", "chưa làm bài", "chua lam bai",
                "khó quá", "kho qua", "không hiểu bài", "khong hieu bai", "homework", "assignment", "study", "học bài", "hoc bai"
            ),
            text = "Bài tập nhiều có thể làm con thấy rối. Mình chia nhỏ ra nhé: chọn một bài dễ nhất trước, làm từng chút một, rồi nghỉ ngắn. Con không cần làm tất cả trong một hơi đâu.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "kiểm tra", "kiem tra", "bài kiểm tra", "bai kiem tra", "thi", "exam", "test", "sắp thi", "sap thi",
                "sợ kiểm tra", "so kiem tra", "sợ thi", "so thi", "thi điểm kém", "thi diem kem", "quên bài", "quen bai"
            ),
            text = "Trước bài kiểm tra, lo lắng là bình thường. Mình hít thở chậm, ôn phần nhỏ nhất trước, rồi tự nhủ: 'Con sẽ làm từng câu một'. Cô Vy tin con cố gắng được 🌬️",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "điểm kém", "diem kem", "bị điểm thấp", "bi diem thap", "làm sai", "lam sai", "sai bài", "sai bai",
                "bị cô mắng", "bi co mang", "bị thầy mắng", "bi thay mang", "không đạt", "khong dat", "fail", "bad grade"
            ),
            text = "Điểm chưa tốt không có nghĩa là con kém. Nó chỉ cho mình biết phần nào cần luyện thêm. Con có thể nghỉ một chút, rồi hỏi thầy cô hoặc bố mẹ cách sửa nhé.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "cô mắng", "co mang", "thầy mắng", "thay mang", "bị phạt", "bi phat", "phạt đứng", "phat dung",
                "cô nghiêm", "co nghiem", "thầy nghiêm", "thay nghiem", "teacher scold", "punished"
            ),
            text = "Bị mắng làm con buồn và sợ. Khi con bình tĩnh hơn, mình thử nghĩ xem cô thầy muốn con sửa điều gì, rồi con có thể nói chuyện nhẹ nhàng với người lớn nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        FallbackRule(
            keywords = listOf(
                "đi học", "di hoc", "trường", "truong", "lớp", "lop", "ngày đầu đi học", "ngay dau di hoc", "lớp mới",
                "lop moi", "trường mới", "truong moi", "cô giáo mới", "co giao moi", "thầy giáo mới", "thay giao moi", "first day school"
            ),
            text = "Đi học hoặc vào lớp mới có thể làm con vừa hồi hộp vừa tò mò. Con cứ bắt đầu bằng một việc nhỏ thôi: chào cô giáo, ngồi ngay ngắn, và quan sát xung quanh nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Friends: friendship, rejection, conflict, bullying ──

        FallbackRule(
            keywords = listOf(
                "bạn không chơi", "ban khong choi", "không cho chơi", "khong cho choi", "bị bỏ ra ngoài", "bi bo ra ngoai",
                "bạn không thích con", "ban khong thich con", "bạn nghỉ chơi", "ban nghi choi", "không ai ngồi cùng", "khong ai ngoi cung",
                "bị lẻ loi", "bi le loi", "excluded", "left out"
            ),
            text = "Bị bạn không cho chơi làm con tủi thân lắm. Con vẫn đáng được yêu quý. Mình có thể thử tìm một bạn khác hiền hơn, hoặc nhờ cô giáo giúp con kết nối nhé.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "cãi nhau", "cai nhau", "giận bạn", "gian ban", "bạn giận", "ban gian", "xích mích", "xich mich",
                "bạn lấy đồ", "ban lay do", "bạn làm hỏng", "ban lam hong", "không nói chuyện", "khong noi chuyen", "fight friend"
            ),
            text = "Cãi nhau với bạn làm lòng mình khó chịu. Khi cả hai bình tĩnh hơn, con có thể nói: 'Mình buồn khi bạn làm vậy' thay vì hét lên. Cô Vy cùng con thở trước nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "bị trêu", "bi treu", "bị chọc", "bi choc", "bị bắt nạt", "bi bat nat", "bắt nạt", "bat nat",
                "bị cười", "bi cuoi", "bị chế giễu", "bi che gieu", "gọi tên xấu", "goi ten xau", "nói xấu", "noi xau",
                "bị đe dọa", "bi de doa", "bully", "bullying", "tease", "mock"
            ),
            text = "Bị trêu chọc hay bắt nạt không phải lỗi của con. Con hãy nói với cô giáo, bố mẹ hoặc một người lớn đáng tin. Con xứng đáng được an toàn và được tôn trọng.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "bạn không trả đồ", "ban khong tra do", "mất bút", "mat but", "mất đồ", "mat do", "bị lấy đồ", "bi lay do",
                "bạn giấu đồ", "ban giau do", "đồ chơi bị lấy", "do choi bi lay", "steal", "stolen"
            ),
            text = "Mất đồ hoặc bị lấy đồ làm con lo và bực. Con hãy nói rõ với cô giáo hoặc người lớn: con mất gì, mất ở đâu, lúc nào con thấy lần cuối nhé.",
            suggestion = null,
        ),

        FallbackRule(
            keywords = listOf(
                "bạn", "ban", "bạn bè", "ban be", "bạn thân", "ban than", "chơi với bạn", "choi voi ban",
                "muốn có bạn", "muon co ban", "kết bạn", "ket ban", "new friend", "friendship", "best friend"
            ),
            text = "Bạn bè là điều rất quan trọng với con. Con có thể bắt đầu bằng một câu nhỏ như: 'Bạn chơi cùng mình không?' hoặc 'Mình ngồi đây được không?'. Cô Vy tin con làm được.",
            suggestion = null,
        ),

        // ── Family: parents, siblings, home situations ──
        FallbackRule(
            keywords = listOf(
                "bố mẹ mắng", "bo me mang", "mẹ mắng", "me mang", "bố mắng", "bo mang", "ba mắng", "ba mang",
                "bị la", "bi la", "bị quát", "bi quat", "mẹ quát", "me quat", "bố quát", "bo quat", "parents scold"
            ),
            text = "Bị người lớn mắng làm con buồn và sợ. Con thử thở chậm trước, rồi khi mọi người bình tĩnh hơn, con có thể nói: 'Con buồn và con muốn được nghe nhẹ nhàng hơn'.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "mẹ không yêu", "me khong yeu", "bố không yêu", "bo khong yeu", "ba không yêu", "ba khong yeu",
                "bố mẹ không thương", "bo me khong thuong", "mẹ thương em hơn", "me thuong em hon", "bố thương em hơn", "bo thuong em hon",
                "con bị bỏ quên", "con bi bo quen"
            ),
            text = "Khi con cảm thấy không được yêu thương, lòng con sẽ đau lắm. Cảm giác đó là thật. Con có thể nói với bố mẹ: 'Con muốn được ôm' hoặc 'Con muốn bố mẹ nghe con một chút'.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "anh chị em", "anh chi em", "em con", "anh con", "chị con", "chi con", "em phá", "em pha", "em giành đồ",
                "em gianh do", "anh giành", "anh gianh", "chị giành", "chi gianh", "cãi nhau với em", "cai nhau voi em", "sibling"
            ),
            text = "Sống cùng anh chị em đôi khi rất vui nhưng cũng dễ cãi nhau. Con thử nói rõ: 'Con đang dùng món này' hoặc nhờ người lớn giúp chia lượt công bằng nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "bố mẹ cãi nhau", "bo me cai nhau", "ba mẹ cãi nhau", "ba me cai nhau", "bố mẹ giận nhau", "bo me gian nhau",
                "nhà ồn", "nha on", "người lớn cãi nhau", "nguoi lon cai nhau", "con sợ ở nhà", "con so o nha"
            ),
            text = "Nghe người lớn cãi nhau có thể làm con sợ. Đó không phải lỗi của con. Con hãy tìm chỗ an toàn, ở gần người con tin, và nói với một người lớn rằng con đang sợ nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "bố mẹ bận", "bo me ban", "mẹ bận", "me ban", "bố bận", "bo ban", "không ai nghe con", "khong ai nghe con",
                "con muốn được nghe", "con muon duoc nghe", "bố mẹ không nói chuyện", "bo me khong noi chuyen"
            ),
            text = "Khi người lớn bận, con có thể thấy mình bị bỏ lại. Con thử chọn lúc yên hơn và nói: 'Con có chuyện muốn kể, bố mẹ nghe con một chút được không ạ?'.",
            suggestion = null,
        ),

        // ── Mistakes, guilt, shame, apology, lying ──
        FallbackRule(
            keywords = listOf(
                "con sai", "con làm sai", "con lam sai", "con có lỗi", "con co loi", "lỗi của con", "loi cua con",
                "con hối hận", "con hoi han", "con lỡ", "con lo", "guilt", "guilty", "mistake"
            ),
            text = "Ai cũng có lúc làm sai. Điều quan trọng là con dám nhận ra và sửa lại. Con có thể xin lỗi, nói thật, rồi làm một việc nhỏ để sửa lỗi nhé.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "xin lỗi", "xin loi", "không dám xin lỗi", "khong dam xin loi", "nên xin lỗi", "nen xin loi",
                "con muốn xin lỗi", "con muon xin loi", "apologize", "sorry"
            ),
            text = "Một lời xin lỗi chân thành có thể rất mạnh mẽ. Con có thể nói: 'Mình xin lỗi vì đã làm bạn buồn. Mình sẽ cố gắng không làm vậy nữa'.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "con nói dối", "con noi doi", "lỡ nói dối", "lo noi doi", "giấu bố mẹ", "giau bo me", "không dám nói thật",
                "khong dam noi that", "lie", "lied", "secret"
            ),
            text = "Nói thật đôi khi làm mình sợ bị mắng, nhưng sự thật giúp mọi người hiểu và giúp con sửa lỗi. Con có thể nói chậm rãi: 'Con muốn nói thật, nhưng con hơi sợ'.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "xấu hổ", "xau ho", "ngại", "ngai", "mắc cỡ", "mac co", "quê", "que", "bị cười quê", "bi cuoi que",
                "embarrassed", "shy", "ashamed", "con ngại", "con ngai"
            ),
            text = "Xấu hổ là cảm giác ai cũng từng có. Nó sẽ nhỏ lại theo thời gian. Con thử thở chậm và nhớ rằng một khoảnh khắc ngượng không nói lên con là ai nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Self-esteem, ability, identity-neutral encouragement ──
        FallbackRule(
            keywords = listOf(
                "con dở", "con do", "con ngu", "con kém", "con kem", "con tệ", "con te", "con vô dụng", "con vo dung",
                "con không giỏi", "con khong gioi", "con chẳng làm được", "con chang lam duoc", "stupid", "useless", "bad at"
            ),
            text = "Cô Vy không nghĩ con vô dụng đâu. Có thể con đang gặp việc khó, nhưng khó không có nghĩa là con kém. Mình chia nhỏ việc ra và thử lại từng bước nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "con xấu", "con xau", "con không đẹp", "con khong dep", "con béo", "con beo", "con gầy", "con gay",
                "bị chê xấu", "bi che xau", "không thích mình", "khong thich minh", "ugly", "not pretty", "fat", "skinny"
            ),
            text = "Con không cần phải giống ai để đáng yêu. Cơ thể và khuôn mặt của con là của riêng con. Điều cô Vy quý nhất là con biết yêu thương và cố gắng mỗi ngày.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "con không làm được", "con khong lam duoc", "khó quá con bỏ", "kho qua con bo", "bỏ cuộc", "bo cuoc",
                "không thể", "khong the", "i can't", "cant", "cannot", "give up"
            ),
            text = "Khi việc gì đó khó, mình không cần bỏ cuộc ngay. Con chỉ cần làm bước nhỏ nhất trước. Làm được một chút cũng là tiến bộ rồi đó 🌟",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Body needs, health discomfort, basic care ──
        FallbackRule(
            keywords = listOf(
                "đói", "doi", "khát", "khat", "buồn đi vệ sinh", "buon di ve sinh", "đau bụng đói", "dau bung doi",
                "hungry", "thirsty", "con đói", "con doi", "con khát", "con khat"
            ),
            text = "Cơ thể con đang nhắc mình cần được chăm sóc. Con hãy nói với người lớn rằng con đói, khát hoặc cần đi vệ sinh nhé. Chăm sóc cơ thể cũng rất quan trọng đó.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "đau", "dau", "đau bụng", "dau bung", "đau đầu", "dau dau", "đau răng", "dau rang", "sốt", "sot",
                "ốm", "om", "mệt bệnh", "met benh", "chóng mặt", "chong mat", "buồn nôn", "buon non", "sick", "hurt", "pain"
            ),
            text = "Nếu cơ thể con đang đau hoặc khó chịu, con hãy nói ngay với bố mẹ, thầy cô hoặc người lớn gần con để được chăm sóc nhé. Cô Vy có thể cùng con thở chậm trong lúc chờ người lớn giúp.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "ngã", "nga", "bị ngã", "bi nga", "chảy máu", "chay mau", "bong gân", "bong gan", "trầy xước",
                "tray xuoc", "đứt tay", "dut tay", "bỏng", "bong", "injured", "bleeding", "fall down"
            ),
            text = "Con bị đau rồi. Con hãy gọi người lớn ngay để được kiểm tra và giúp đỡ. Trong lúc đó, con cố ngồi yên ở chỗ an toàn và thở chậm nhé.",
            suggestion = ConfideSuggestion.BREATHING,
        ),

        // ── Common child requests: jokes, games, drawing, animals, wishes ──
        FallbackRule(
            keywords = listOf(
                "kể chuyện cười", "ke chuyen cuoi", "nói đùa", "noi dua", "cười", "cuoi", "trò vui", "tro vui",
                "joke", "funny", "make me laugh", "làm con cười", "lam con cuoi"
            ),
            text = "Cô Vy thích tiếng cười của con lắm 😊 Nhưng lúc này cô có thể dẫn con tới một câu chuyện vui và nhẹ nhàng trước nhé.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "chơi", "choi", "trò chơi", "tro choi", "muốn chơi", "muon choi", "game", "play", "play game",
                "chơi cùng con", "choi cung con"
            ),
            text = "Chơi giúp tâm trạng nhẹ hơn đó. Con có thể chọn một trò nhỏ, hoặc mình nghe một câu chuyện để trí tưởng tượng bay xa nhé 📖",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "vẽ", "ve", "tô màu", "to mau", "màu sắc", "mau sac", "bút màu", "but mau", "draw", "drawing", "paint",
                "con muốn vẽ", "con muon ve"
            ),
            text = "Vẽ là cách rất hay để nói ra cảm xúc. Con thử vẽ hôm nay trái tim con có màu gì, rồi kể cho cô Vy nghe nhé.",
            suggestion = ConfideSuggestion.RELAX,
        ),
        FallbackRule(
            keywords = listOf(
                "chó", "cho", "mèo", "meo", "thỏ", "tho", "gấu", "gau", "khủng long", "khung long", "động vật", "dong vat",
                "pet", "dog", "cat", "rabbit", "animal"
            ),
            text = "Những người bạn động vật đáng yêu thật đó. Con thích con vật nào nhất? Cô Vy có thể cùng con nghe một câu chuyện dễ thương về các bạn ấy 📖",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "ước", "uoc", "điều ước", "dieu uoc", "con ước", "con uoc", "mong muốn", "mong muon", "wish", "dream"
            ),
            text = "Điều ước của con chắc rất đặc biệt. Con kể cho cô Vy nghe đi, vì khi mình nói ra điều ước, trái tim thường sáng hơn một chút đó ✨",
            suggestion = null,
        ),

        // ── Social manners and small talk ──
        FallbackRule(
            keywords = listOf(
                "tạm biệt", "tam biet", "bye", "goodbye", "ngủ ngon", "ngu ngon", "chúc ngủ ngon", "chuc ngu ngon",
                "hẹn gặp lại", "hen gap lai", "mai gặp", "mai gap"
            ),
            text = "Cô Vy chào con nhé 💛 Chúc con có một khoảng thời gian thật yên bình. Khi nào muốn tâm sự, con lại quay lại với cô nha.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "không biết", "khong biet", "con không biết nói gì", "con khong biet noi gi", "không biết kể gì", "khong biet ke gi",
                "hmm", "ừm", "um", "ờ", "o", "bí quá", "bi qua"
            ),
            text = "Không sao đâu, mình không cần nói thật hay ngay từ đầu. Con có thể bắt đầu bằng một từ thôi: vui, buồn, giận, sợ, mệt hoặc bình thường.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "bình thường", "binh thuong", "không sao", "khong sao", "ổn", "on", "cũng được", "cung duoc", "ok", "okay",
                "fine", "normal", "không có gì", "khong co gi"
            ),
            text = "Bình thường cũng là một cảm xúc đáng được lắng nghe. Nếu lát nữa con muốn kể thêm điều gì, cô Vy vẫn ở đây nhé 😊",
            suggestion = null,
        ),

        // ── Stress, pressure, and burnout ──

        FallbackRule(
            keywords = listOf(
                "áp lực", "ap luc", "căng thẳng", "cang thang", "stress", "stressed", "nặng đầu", "nang dau", "mệt đầu",
                "met dau", "nhiều việc", "nhieu viec", "quá tải", "qua tai"
            ),
            text = "Áp lực giống như chiếc balo quá nặng. Mình đặt balo xuống một chút nhé: thở chậm, nghỉ ngắn, rồi chọn một việc nhỏ nhất để làm trước.",
            suggestion = ConfideSuggestion.BREATHING,
        ),
        FallbackRule(
            keywords = listOf(
                "thất bại", "that bai", "thua", "lose", "lost", "không thắng", "khong thang", "hỏng rồi", "hong roi",
                "làm hỏng", "lam hong", "con làm hỏng", "con lam hong"
            ),
            text = "Thất bại không phải dấu chấm hết. Nó chỉ là một lần thử chưa thành công. Cô Vy tin con có thể học được điều gì đó rồi thử lại nhẹ nhàng hơn.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "mong được khen", "mong duoc khen", "muốn được khen", "muon duoc khen", "không ai khen", "khong ai khen",
                "con cố gắng mà", "con co gang ma", "không ai thấy", "khong ai thay", "need praise", "notice me"
            ),
            text = "Con đã cố gắng và cô Vy nhìn thấy điều đó. Dù chưa ai khen ngay, nỗ lực của con vẫn rất đáng quý. Con kể cho cô nghe con đã cố gắng thế nào nhé.",
            suggestion = null,
        ),

        // ── Food, sharing, toys, daily routines ──
        FallbackRule(
            keywords = listOf(
                "không muốn ăn", "khong muon an", "ăn cơm", "an com", "bị ép ăn", "bi ep an", "khó ăn", "kho an",
                "món con ghét", "mon con ghet", "không ngon", "khong ngon", "meal", "eat"
            ),
            text = "Chuyện ăn uống đôi khi làm con khó chịu. Con có thể nói với người lớn: 'Con no rồi' hoặc 'Con muốn ăn chậm hơn'. Cơ thể con cần được lắng nghe nhẹ nhàng.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "đồ chơi", "do choi", "gấu bông", "gau bong", "búp bê", "bup be", "xe đồ chơi", "xe do choi", "lego",
                "toy", "favorite toy", "mất đồ chơi", "mat do choi", "hỏng đồ chơi", "hong do choi"
            ),
            text = "Đồ chơi mình thích thường chứa rất nhiều kỷ niệm. Nếu nó mất hoặc hỏng, con buồn là đúng rồi. Con có thể nhờ người lớn cùng tìm hoặc sửa nhé.",
            suggestion = ConfideSuggestion.STORY,
        ),
        FallbackRule(
            keywords = listOf(
                "không muốn tắm", "khong muon tam", "đánh răng", "danh rang", "đi ngủ sớm", "di ngu som", "dọn đồ", "don do",
                "việc nhà", "viec nha", "routine", "chores"
            ),
            text = "Có những việc hằng ngày mình không thích nhưng vẫn giúp cơ thể và căn phòng dễ chịu hơn. Con thử làm trong thời gian rất ngắn trước, rồi tự khen mình một câu nhé.",
            suggestion = null,
        ),

        // ── Technology and screen time ──
        FallbackRule(
            keywords = listOf(
                "điện thoại", "dien thoai", "ipad", "máy tính", "may tinh", "xem youtube", "xem điện thoại", "xem dien thoai",
                "chơi game quá lâu", "choi game qua lau", "bị cấm điện thoại", "bi cam dien thoai", "screen time"
            ),
            text = "Khi phải dừng điện thoại hay trò chơi, con có thể thấy khó chịu. Mình thử nghỉ mắt một chút, uống nước, rồi chọn một hoạt động khác nhẹ nhàng hơn nhé.",
            suggestion = ConfideSuggestion.RELAX,
        ),

        // ── Questions about emotions ──
        FallbackRule(
            keywords = listOf(
                "cảm xúc là gì", "cam xuc la gi", "buồn là gì", "buon la gi", "giận là gì", "gian la gi", "sợ là gì", "so la gi",
                "vui là gì", "vui la gi", "emotion", "feeling", "feelings"
            ),
            text = "Cảm xúc là tín hiệu trong lòng và cơ thể. Vui làm mình sáng lên, buồn làm mình chậm lại, giận làm người nóng lên, còn sợ giúp mình chú ý đến an toàn.",
            suggestion = null,
        ),
        FallbackRule(
            keywords = listOf(
                "làm sao hết buồn", "lam sao het buon", "làm sao bình tĩnh", "lam sao binh tinh", "làm sao hết sợ", "lam sao het so",
                "làm sao hết giận", "lam sao het gian", "what should i do", "how to calm", "how to feel better"
            ),
            text = "Mình bắt đầu bằng ba bước nhỏ nhé: gọi tên cảm xúc, hít thở chậm, rồi kể cho người mình tin. Cô Vy có thể cùng con tập thở ngay bây giờ 🌬️",
            suggestion = ConfideSuggestion.BREATHING,
        ),
    )

    private fun buildFallbackResponse(input: String): ConfideMessage {
        val raw = input.lowercase().trim()
        val folded = normalizeText(raw)
        val matchedRule = fallbackRules.firstOrNull { rule ->
            containsAny(raw, folded, rule.keywords)
        }

        val (text, suggestion) = if (matchedRule != null) {
            matchedRule.text to matchedRule.suggestion
        } else {
            "Cô Vy lắng nghe con rồi. Con có muốn kể thêm cho cô nghe không? Con có thể nói con đang vui, buồn, giận, sợ, mệt hay lo lắng nhé." to null
        }

        return ConfideMessage(
            role       = MessageRole.ASSISTANT,
            text       = text,
            suggestion = suggestion,
        )
    }

    private fun containsAny(raw: String, folded: String, keywords: List<String>): Boolean =
        keywords.any { keyword -> textContainsKeyword(raw, folded, keyword) }

    private fun textContainsKeyword(raw: String, folded: String, keyword: String): Boolean {
        val rawKeyword = keyword.lowercase().trim()
        if (rawKeyword.isBlank()) return false

        val foldedKeyword = normalizeText(rawKeyword)
        if (rawKeyword in raw) return true

        // Short words such as "lo", "sợ", "ổn", "ngủ" can accidentally match
        // inside longer words after accent folding. Use a soft word boundary for them.
        return if (foldedKeyword.length <= 3) {
            val boundary = Regex("(^|[^\\p{L}\\p{N}])${Regex.escape(foldedKeyword)}([^\\p{L}\\p{N}]|$)")
            boundary.containsMatchIn(folded)
        } else {
            foldedKeyword in folded
        }
    }

    private fun normalizeText(text: String): String {
        val withoutMarks = Normalizer
            .normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace("đ", "d")
        return withoutMarks
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}

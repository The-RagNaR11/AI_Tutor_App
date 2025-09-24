import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ragnar.ai_tutor.ui.theme.WaveformActive
import com.ragnar.ai_tutor.ui.theme.WaveformInactive
import kotlin.random.Random


@Composable
fun AudioWaveform(progress: Float) {
    val amplitudes = remember { List(40) { Random.nextInt(10, 60) } }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.height(60.dp)
    ) {
        amplitudes.forEachIndexed { index, amplitude ->
            val color = if (index.toFloat() / amplitudes.size < progress) {
                WaveformActive
            } else {
                WaveformInactive
            }
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(amplitude.dp.coerceAtLeast(10.dp))
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
    }
}
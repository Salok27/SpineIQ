package noshtek.back_pain_prototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import noshtek.back_pain_prototype.navigation.SpineIQNavGraph
import noshtek.back_pain_prototype.ui.gamification.CelebrationHost
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpineIQTheme {
                Box(Modifier.fillMaxSize()) {
                    SpineIQNavGraph()
                    // Global reward toasts + level-up/achievement/streak overlays.
                    // Last child = top z-order; renders nothing while idle.
                    CelebrationHost()
                }
            }
        }
    }
}

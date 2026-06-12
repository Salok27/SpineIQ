package noshtek.back_pain_prototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import noshtek.back_pain_prototype.navigation.SpineIQNavGraph
import noshtek.back_pain_prototype.ui.gamification.CelebrationHost
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Fade + scale the splash icon out into the aurora UI instead of the
        // default hard cut.
        splash.setOnExitAnimationListener { provider ->
            provider.iconView.animate()
                .alpha(0f)
                .scaleX(1.2f).scaleY(1.2f)
                .setDuration(260L)
                .withEndAction { provider.remove() }
                .start()
        }
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

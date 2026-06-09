package noshtek.back_pain_prototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import noshtek.back_pain_prototype.navigation.SpineIQNavGraph
import noshtek.back_pain_prototype.ui.theme.SpineIQTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpineIQTheme {
                SpineIQNavGraph()
            }
        }
    }
}

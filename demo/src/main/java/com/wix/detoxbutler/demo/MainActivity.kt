package com.wix.detoxbutler.demo

import android.app.ActivityManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.lifecycleScope
import com.wix.detoxbutler.DetoxButler
import com.wix.detoxbutler.demo.ui.theme.DetoxButlerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DIVISION_BY_ZERO")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSystemService(ActivityManager::class.java)

        setContent {
            val isServiceEnabledState = rememberSaveable {
                mutableStateOf(false)
            }


            DetoxButlerTheme {
                MainScreen(
                    callback = object : MainScreenCallback {
                        override fun setup() {
                            lifecycleScope.launch(Dispatchers.IO) {
                                DetoxButler.setup(this@MainActivity)
                            }
                        }

                        override fun tearDown() {
                            lifecycleScope.launch(Dispatchers.IO) {
                                DetoxButler.teardown(this@MainActivity)
                            }
                        }

                        override fun onGenerateAnrClicked() {
                            Thread.sleep(10000)
                        }

                        override fun refreshServiceStatus() {
                            isServiceEnabledState.value = DetoxButler.isDetoxButlerServiceEnabled()
                        }

                        override fun onCrashClicked() {
                            1/0
                        }
                    },
                    serviceEnabled = isServiceEnabledState.value,
                )
            }
        }
    }
}


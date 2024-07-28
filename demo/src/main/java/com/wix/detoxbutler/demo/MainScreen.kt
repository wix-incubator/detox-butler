package com.wix.detoxbutler.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wix.detoxbutler.demo.ui.theme.DetoxButlerTheme


interface MainScreenCallback {

    fun setup()

    fun tearDown()
    fun onGenerateAnrClicked()

    fun refreshServiceStatus()

    fun onCrashClicked()
}

@Composable
fun MainScreen(callback: MainScreenCallback, serviceEnabled: Boolean) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        MainScreenContent(callback, serviceEnabled)
    }
}

@Composable
private fun MainScreenContent(callback: MainScreenCallback, serviceEnabled: Boolean) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column {
            Button(onClick = callback::setup) {
                Text(text = "Setup")
            }
            Button(onClick = callback::tearDown) {
                Text(text = "Tear Down")
            }
            Button(onClick = callback::onGenerateAnrClicked) {
                Text(text = "Generate ANR")
            }
            Button(onClick = callback::refreshServiceStatus) {
                Text(text = "Refresh Enabled Status")
            }
            Button(onClick = callback::onCrashClicked) {
                Text(text = "Crash")
            }
        }

        Column {
            Text(text = "Service Status: $serviceEnabled")
        }
    }

}

private class MainScreenPreviewCallback : MainScreenCallback {
    override fun setup() {}

    override fun tearDown() {}

    override fun onGenerateAnrClicked() {}
    override fun refreshServiceStatus() {}
    override fun onCrashClicked() {}
}

@Composable
@Preview(showBackground = true)
private fun MainScreenPreview() {
    DetoxButlerTheme {
        MainScreen(MainScreenPreviewCallback(), false)
    }
}
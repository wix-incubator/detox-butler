package com.wix.detoxbutler.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wix.detoxbutler.demo.ui.theme.DetoxButlerTheme


interface MainScreenCallback {

    fun setup()

    fun tearDown()
    fun onGenerateAnrClicked()
}

@Composable
fun MainScreen(callback: MainScreenCallback) {
    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        MainScreenContent(callback)
    }
}

@Composable
private fun MainScreenContent(callback: MainScreenCallback) {
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
    }
}

private class MainScreenPreviewCallback : MainScreenCallback {
    override fun setup() {}

    override fun tearDown() {}

    override fun onGenerateAnrClicked() {}
}

@Composable
@Preview(showBackground = true)
private fun MainScreenPreview() {
    DetoxButlerTheme {
        MainScreen(MainScreenPreviewCallback())
    }
}
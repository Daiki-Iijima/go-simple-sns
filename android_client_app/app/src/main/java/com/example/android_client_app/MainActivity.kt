package com.example.android_client_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.android_client_app.ui.theme.AndroidclientappTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidclientappTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val coroutineScope = rememberCoroutineScope()

    var responseStr by remember{ mutableStateOf("none") }

    Column (
        modifier = modifier
    ){
        Text(text = responseStr)
        Button(onClick = {
            coroutineScope.launch(Dispatchers.IO) {
                val client = HttpClient(CIO)
                val response : HttpResponse = client.get("https://daiki-iijima.github.io/archives/");
                responseStr = response.status.toString()
            }
        }) {
            Text("取得開始")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    AndroidclientappTheme {
        Greeting("Android")
    }
}
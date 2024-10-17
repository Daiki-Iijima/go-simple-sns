package com.example.android_client_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun UserAuthenticationView(onLogined: (Int)->Unit,modifier: Modifier = Modifier){
    var isLogin by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()) {

        InputView(
            isLogin,
            {userName,password ->
                coroutineScope.launch(Dispatchers.IO) {
                    val client = HttpClient(CIO)
                    val response : HttpResponse = client.post("http://192.168.0.80:8080/signup"){
                        contentType(ContentType.Application.Json)
                        setBody("{\"username\":\"$userName\",\"password\":\"$password\"}")
                    }
                    val responseStr = response.bodyAsText()

                    //  TODO : Jsonパース方式にしたい
                    // "user_id" が含まれている場合のみ処理を進める
                    if (responseStr.contains("\"user_id\"")) {
                        // 正規表現で user_id の値を抽出
                        val regex = "\"user_id\":(\\d+)".toRegex()
                        val matchResult = regex.find(responseStr)

                        // マッチした部分から数値を取り出す
                        val id = matchResult?.groups?.get(1)?.value?.toInt()
                        if(id != null){
                            onLogined(id)
                        }
                    }

                    client.close()
                }
        },modifier)

        Spacer(modifier = Modifier.height(100.dp))

        Button(onClick = {isLogin = !isLogin}) {
            Text(text= if(isLogin) "アカウント登録する" else "ログインする")
        }
    }
}

@Composable
fun InputView(isLogin: Boolean,onClickBtn: (String,String)-> Unit,modifier: Modifier = Modifier){

    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = userName,
            placeholder = {
                Text(text = "ユーザー名")
            },
            onValueChange = {
                userName = it
            }
            )
        OutlinedTextField(
            value = password,
            placeholder = {
                Text(text = "パスワード")
            },
            onValueChange = {
                password = it
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Button(onClick = {
            onClickBtn(userName,password)
        } ) {
            Text(text = if(isLogin)  "ログイン" else "アカウント作成")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun Preview(){
   UserAuthenticationView({})
}
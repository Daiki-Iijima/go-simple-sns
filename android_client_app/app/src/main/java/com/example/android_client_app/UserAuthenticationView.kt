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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserAuthenticationView(modifier: Modifier = Modifier){
    var isLogin by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()) {

        InputView(isLogin,{},modifier)

        Spacer(modifier = Modifier.height(100.dp))

        Button(onClick = {isLogin = !isLogin}) {
            Text(text= if(isLogin) "アカウント登録する" else "ログインする")
        }
    }
}

@Composable
fun InputView(isLogin: Boolean,onClickBtn: ()-> Unit,modifier: Modifier = Modifier){

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "",
            placeholder = {
                Text(text = "ユーザー名")
            },
            onValueChange = {
            }
            )
        OutlinedTextField(
            value = "",
            placeholder = {
                Text(text = "パスワード")
            },
            onValueChange = {
            },
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Button(onClick = onClickBtn) {Text(text = if(isLogin)  "ログイン" else "アカウント作成") }
    }
}

@Preview(showSystemUi = true)
@Composable
fun Preview(){
   UserAuthenticationView()
}
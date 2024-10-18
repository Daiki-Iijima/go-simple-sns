package com.example.android_client_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PostListView(modifier: Modifier = Modifier){
    var newPostStr by remember { mutableStateOf("") }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ){
        val list = List(100){ "データデータデータデータデータデータデータデータデータデータデータデータ:$it" }

        LazyColumn (
            modifier = Modifier.fillMaxSize().weight(9f)
        ){
            items(list){
                Column (
                    modifier = Modifier.fillMaxWidth().height(100.dp).padding(10.dp)
                ){
                    Text(text = "投稿者 : aa",modifier = Modifier.padding(8.dp))
                    Text(text = "$it",modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp).weight(1f)
        ){
            OutlinedTextField(
                value = newPostStr,
                onValueChange = { newValue->
                    newPostStr = newValue
                },
                placeholder = {
                    Text("新しい投稿")
                }
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {}) {
                Text("投稿")
            }

        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PostListPreview(){
    Scaffold { innerPadding ->
        PostListView(modifier = Modifier.padding(innerPadding))
    }
}

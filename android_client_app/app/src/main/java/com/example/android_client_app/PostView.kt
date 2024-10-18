package com.example.android_client_app

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.Send
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Post(
    val id: Int,
    val content: String,
    val user_id: Int,
    val created_at: String,
    val updated_at: String,
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PostResponse(
    val posts: List<Post>
)

private suspend fun GetPosts(reverse: Boolean = true): PostResponse? {

    val client = HttpClient(CIO)
    try{
        val response : HttpResponse = client.get("$BASE_URL/post")
        val responseStr = response.bodyAsText()
        Log.d("取得結果",responseStr)
        // JSON文字列をオブジェクトに変換
        val postResponse = Json.decodeFromString<PostResponse>(responseStr)
        return PostResponse(postResponse.posts.reversed())
    } catch (e: Exception) {
        Log.e("エラー", "パースに失敗しました: ${e.message}")
    } finally {
        client.close()
    }
    return null
}

private suspend fun SendPost(userId: String,newPostStr: String) {
    val client = HttpClient(CIO)
    val response : HttpResponse = client.post("$BASE_URL/post"){
        contentType(ContentType.Application.Json)
        setBody("{\"user_id\":\"$userId\",\"content\":\"$newPostStr\"}")
    }
    val responseStr = response.bodyAsText()

    client.close()
}

private fun ParseTime(timeString: String): String{
    // 文字列をZonedDateTimeにパースし、タイムゾーンを日本時間に変更
    val dateTime = ZonedDateTime.parse(timeString).withZoneSameInstant(java.time.ZoneId.of("Asia/Tokyo"))

    // フォーマットパターンを定義
    val formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分", Locale.JAPANESE)

    // フォーマットした文字列を取得
    val formattedTime = dateTime.format(formatter)

    return formattedTime
}

@Composable
fun PostView(
    userId:Int,
    content: String,
    createdAt: String,
    modifier: Modifier = Modifier
){
    Column (
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(10.dp)
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(text = "投稿者 : $userId",modifier = Modifier.padding(8.dp))
            Spacer(modifier = Modifier.weight(1f))
            Text(text = ParseTime(createdAt) ,modifier = Modifier.padding(8.dp))
        }

        Text(
            text = content,
            fontSize = 20.sp ,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostListView(userId: String,modifier: Modifier = Modifier){
    var newPostStr by remember { mutableStateOf("") }

    val coroutine = rememberCoroutineScope()

    var postResponse: PostResponse by remember { mutableStateOf(PostResponse(posts = listOf())) }

    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()

    //  初回コンポーズのみ実行される
    LaunchedEffect(Unit) {

        //  最初に1回更新
        postResponse = GetPosts() ?: postResponse

        // リストの最後のアイテムにスクロール
        listState.animateScrollToItem(postResponse.posts.size - 1)

        //  2秒おきに実行する
        while(true){
            postResponse = GetPosts() ?: postResponse

            // リストの最後のアイテムにスクロール
            listState.animateScrollToItem(postResponse.posts.size - 1)

            delay(2000L)
        }
    }


    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ){
        LazyColumn (
            state = listState,
            modifier = Modifier.fillMaxSize().weight(9f)
        ){
            items(postResponse.posts){ post->
                PostView(
                    post.user_id,
                    post.content,
                    post.created_at,
                )
                HorizontalDivider(thickness = 2.dp)
            }
        }

        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(4.dp).weight(1.2f)
        ){
            OutlinedTextField(
                value = newPostStr,
                onValueChange = { newValue->
                    newPostStr = newValue
                },
                placeholder = {
                    Text("新しい投稿(10文字まで)")
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        coroutine.launch {
                            //  投稿
                            SendPost(userId, newPostStr)
                            //  キーボードを閉じる
                            focusManager.clearFocus()
                            //  テキストをクリア
                            newPostStr = ""

                            //  最新投稿を取得
                            postResponse = GetPosts() ?: postResponse

                            // リストの最後のアイテムにスクロール
                            listState.animateScrollToItem(postResponse.posts.size - 1)
                        }
                    }
                )
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PostListPreview(){
    Scaffold { innerPadding ->
        PostListView("1",modifier = Modifier.padding(innerPadding))
    }
}

@Preview(showBackground = true)
@Composable
fun PostPreview(){
        PostView(1,"これは新しいポスト","2024-10-18T05:50:00.131255Z")
}

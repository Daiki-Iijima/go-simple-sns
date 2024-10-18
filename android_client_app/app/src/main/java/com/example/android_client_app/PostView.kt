package com.example.android_client_app

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
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

private suspend fun GetPosts(): PostResponse? {

    val client = HttpClient(CIO)
    try{
        val response : HttpResponse = client.get("$BASE_URL/post")
        val responseStr = response.bodyAsText()
        Log.d("取得結果",responseStr)
        // JSON文字列をオブジェクトに変換
        return Json.decodeFromString<PostResponse>(responseStr)
    } catch (e: Exception) {
        Log.e("エラー", "パースに失敗しました: ${e.message}")
    } finally {
        client.close()
    }
    return null
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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun PostListView(userId: String,modifier: Modifier = Modifier){
    var newPostStr by remember { mutableStateOf("") }

    val coroutine = rememberCoroutineScope()

    var postResponse: PostResponse by remember { mutableStateOf(PostResponse(posts = listOf())) }

    Log.d("テスト","$BASE_URL/post")

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ){

        Row(modifier = Modifier.fillMaxWidth().weight(0.5f).padding(horizontal = 10.dp)){
            Button(onClick = {
                //  最新の投稿を取得
                coroutine.launch{
                    postResponse = GetPosts() ?: postResponse
                }
            }) {
                Text(text = "最新投稿を取得")
            }
        }

        LazyColumn (
            modifier = Modifier.fillMaxSize().weight(9f)
        ){
            items(postResponse.posts){ post->
                Column (
                    modifier = Modifier.fillMaxWidth().height(100.dp).padding(10.dp)
                ){
                    Row{
                        Text(text = "投稿者 : ${post.user_id}",modifier = Modifier.padding(8.dp))
                        Text(text = ParseTime(post.created_at) ,modifier = Modifier.padding(8.dp))
                    }
                    Text(text = post.content,modifier = Modifier.padding(horizontal = 8.dp))
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
                onClick = {
                    val client = HttpClient(CIO)
                    coroutine.launch{
                        val response : HttpResponse = client.post("$BASE_URL/post"){
                            contentType(ContentType.Application.Json)
                            setBody("{\"user_id\":\"$userId\",\"content\":\"$newPostStr\"}")
                        }
                        val responseStr = response.bodyAsText()
                        Log.d("投稿結果",responseStr)

                        client.close()

                        //  最新の投稿を取得
                        coroutine.launch{
                            postResponse = GetPosts() ?: postResponse
                        }
                    }
                }) {
                Text("投稿")
            }
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

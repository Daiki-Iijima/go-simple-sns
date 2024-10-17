package com.example.android_client_app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.android_client_app.ui.theme.AndroidclientappTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.userInfoDataStore: DataStore<Preferences> by preferencesDataStore("user_info")
val USER_ID = stringPreferencesKey("user_id")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidclientappTheme {
                EntryPointView()
            }
        }
    }
}

@Composable
fun EntryPointView(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    val userInfoFlow = context.userInfoDataStore.data.map {preferences ->
        preferences[USER_ID] ?: ""
    }

    val userId by userInfoFlow.collectAsState(initial = "")

    val coroutine = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column (
            modifier =
            modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            Text(text = "ユーザーID:$userId")

            if(userId == "") {
                UserAuthenticationView({ id->
                    coroutine.launch{
                        context.userInfoDataStore.edit { preferences ->
                            preferences[USER_ID] = id.toString()
                        }
                    }
                },modifier = modifier)
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun GreetingPreview() {
    AndroidclientappTheme {
        EntryPointView()
    }
}
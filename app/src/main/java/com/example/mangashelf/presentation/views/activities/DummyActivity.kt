package com.example.mangashelf.presentation.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class DummyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DummyScreen()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DummyScreen() {
    var isToggled by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        var moved by remember { mutableStateOf(false) }

        val offset by animateDpAsState(if (moved) 200.dp else 0.dp, label = "")

        Button(
            onClick = { moved = !moved },
            modifier = Modifier.offset(x = offset)
        ) {
            Text("Slide Me")
        }
    }
}
package com.example.sleeptrackingapp

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.sleeptrackingapp.ui.theme.SleepTrackingAppTheme
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class MainActivity : ComponentActivity() {

    private lateinit var databaseHelper: TimeLogDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = TimeLogDatabaseHelper(this)
        databaseHelper.getAllData()
        setContent {
            SleepTrackingAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyScreen(this,databaseHelper)
                }
            }
        }
    }
}
@Composable
fun MyScreen(context: Context, databaseHelper: TimeLogDatabaseHelper) {
    var startTime by remember { mutableStateOf(0L) }
    var endTime by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
//    var firstAttempt by remember { mutableStateOf(true) }
    var currentTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val imageModifier = Modifier
    Image(
        painterResource(id = R.drawable.sleeptracking),
        contentScale = ContentScale.FillHeight,
        contentDescription = "",
        modifier = imageModifier
            .alpha(0.3F),
    )




    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {



        Text(
            fontSize = 50.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Cursive,
            color = Color.White,
            text = "Sleep Tracking"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isRunning) {

            Button(onClick = {
                endTime = System.currentTimeMillis()
                isRunning = false
            }) {
                Text("Stop")
                //databaseHelper.addTimeLog(startTime)
            }
        } else {

            Button(onClick = {
                startTime = System.currentTimeMillis()
                isRunning = true
            }) {
                Text("Start")
                databaseHelper.addTimeLog(startTime, endTime)
            }
        }
        Spacer(modifier = Modifier.height(200.dp))
        if(isRunning)
        {
            Timer().scheduleAtFixedRate(0, 1000) {
                currentTime = returnCurrentTime()
            }
            Text(text = "Sleep Time: ${formatTime(currentTime - startTime)}")
        }
        else
        {
            Text(text = "Time Not Started")
        }



        Spacer(modifier = Modifier.height(156.dp))
        Button(onClick = {
//            context.startActivity(
//                Intent(
//                    context,
//                    TrackActivity::class.java
//                )
//            )
            startTrackActivity(context)
        }) {
            Text(text = "Track Sleep")
        }
        Spacer(modifier = Modifier.height((16.dp)))
        
        Button(onClick = {
            databaseHelper.deleteAllData()
        }){
            Text(text = "Clear Tracking History")
        }

    }

}

private fun startTrackActivity(context: Context) {
    val intent = Intent(context, TrackActivity::class.java)
    ContextCompat.startActivity(context, intent, null)
}
fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    val currentTime = System.currentTimeMillis()
    return dateFormat.format(Date(currentTime))
}

fun formatTime(timeInMillis: Long): String {
    val hours = (timeInMillis / (1000 * 60 * 60)) % 24
    val minutes = (timeInMillis / (1000 * 60)) % 60
    val seconds = (timeInMillis / 1000) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun returnCurrentTime() : Long {
    return System.currentTimeMillis()
}

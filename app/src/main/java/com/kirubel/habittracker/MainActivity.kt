package com.kirubel.habittracker

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.kirubel.habittracker.ui.theme.HabitTrackerTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HabitTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HabitTrackerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class Habit(
    val title: String,
    var isCompleted: Boolean = false
)

fun saveHabits(context: Context, habits: List<Habit>) {
    val sharedPref = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
    val editor = sharedPref.edit()

    val json = Gson().toJson(habits)

    editor.putString("habits", json)
    editor.apply()
}

fun loadHabits(context: Context): MutableList<Habit> {
    val sharedPref = context.getSharedPreferences("habit_prefs", Context.MODE_PRIVATE)
    val json = sharedPref.getString("habits", null)

    return if (json != null) {
        val type = object : TypeToken<MutableList<Habit>>() {}.type
        Gson().fromJson(json, type)
    } else {
        mutableListOf()
    }
}

@Composable
fun HabitTrackerScreen(modifier: Modifier = Modifier) {

    var habitText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val habits = remember {
        mutableStateListOf<Habit>().apply {
            addAll(loadHabits(context))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Habit Tracker",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Build better habits daily 🚀",
            style = MaterialTheme.typography.bodyMedium
        )

        // 📝 Input Field
        OutlinedTextField(
            value = habitText,
            onValueChange = { habitText = it },
            label = { Text("Enter habit") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // ➕ Add Button
        Button(
            onClick = {
                if (habitText.isNotBlank()) {
                    habits.add(Habit(habitText))
                    habitText = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Habit")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your Habits:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (habits.isEmpty()) {
            Text(
                text = "No habits yet. Start by adding one!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // 📋 Habit List
        Column {
            habits.forEachIndexed { index, habit ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Checkbox(
                                checked = habit.isCompleted,
                                onCheckedChange = {
                                    habit.isCompleted = it
                                    saveHabits(context, habits)
                                }
                            )

                            Text(
                                text = habit.title,
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // 🗑️ Delete Button
                        TextButton(onClick = {
                            habits.removeAt(index)
                            saveHabits(context, habits)
                        }) {
                            Text("Delete")
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHabitTracker() {
    HabitTrackerTheme {
        HabitTrackerScreen()
    }
}
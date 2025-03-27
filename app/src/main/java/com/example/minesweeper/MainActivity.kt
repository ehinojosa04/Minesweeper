package com.example.minesweeper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minesweeper.ui.theme.MinesweeperTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MinesweeperTheme {
                GameScreen()
            }
        }
    }
}

@Composable
fun GameScreen() {
    val columns = 6
    val rows = 15
    val totalCells = rows * columns

    val buttonStates = remember { List(totalCells) { mutableStateOf(true) } }
    val mines = remember { List(totalCells) { mutableStateOf(Random.nextInt(10) > 8) } }
    var revealedCells by remember { mutableStateOf(0) }
    var gameState by remember { mutableStateOf<GameState>(GameState.Playing) }

    when (gameState) {
        GameState.Defeat -> GameDialog("Defeat", "You lost") { resetGame(buttonStates, mines) { gameState = GameState.Playing; revealedCells = 0 } }
        GameState.Victory -> GameDialog("Victory", "You won") { resetGame(buttonStates, mines) { gameState = GameState.Playing; revealedCells = 0 } }
        else -> {}
    }

    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        Bar("Minesweeper")
        Board(rows, columns, buttonStates, mines, {
            revealedCells++
            if (revealedCells >= 10) gameState = GameState.Victory
        }, {
            gameState = GameState.Defeat
        })
    }
}

@Composable
fun Board(
    rows: Int, columns: Int,
    buttonStates: List<MutableState<Boolean>>,
    mines: List<MutableState<Boolean>>,
    onSafeClick: () -> Unit,
    onMineClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        for (i in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.Center) {
                for (j in 0 until columns) {
                    val index = i * columns + j
                    Button(
                        onClick = {
                            buttonStates[index].value = false
                            if (mines[index].value) onMineClick() else onSafeClick()
                        },
                        modifier = Modifier.weight(1f).padding(0.5.dp).size(40.dp),
                        shape = RectangleShape,
                        enabled = buttonStates[index].value
                    ) {
                        Text(if (buttonStates[index].value) "*" else if (mines[index].value) "X" else "O", color = Color.Black, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun Bar(title: String) {
    Column(
        modifier = Modifier.background(Color.Black).fillMaxWidth().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title, color = Color.White)
    }
}

@Composable
fun GameDialog(title: String, message: String, onRestart: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onRestart) { Text("Restart") }
        }
    )
}

fun resetGame(buttonStates: List<MutableState<Boolean>>, mines: List<MutableState<Boolean>>, onReset: () -> Unit) {
    buttonStates.forEach { it.value = true }
    mines.forEach { it.value = Random.nextInt(10) > 8 }
    onReset()
}

enum class GameState { Playing, Victory, Defeat }

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    MinesweeperTheme {
        GameScreen()
    }
}
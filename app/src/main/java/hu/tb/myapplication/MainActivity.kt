package hu.tb.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.tb.myapplication.ui.theme.MyApplicationTheme

data class Book(
    val id: Int,
    val title: String,
    val moreAbout: String,
    var isExpanded: Boolean
)

val testList = mutableStateListOf(
    Book(1, "aaa", "asd", false),
    Book(2, "bbb", "asd", false)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Log.d("MYTAG", "asd")

                val dataList = remember {
                    mutableStateListOf(
                        MyDataClass("Item 1"),
                        MyDataClass("Item 2"),
                        MyDataClass("Item 3")
                    )
                }

                Column {
                    Column {
                        Greeting(testList)
                        Button(onClick = { testList[0].isExpanded = true }) {

                        }
                    }

                    MyLazyColumn(dataList = dataList)
                    TaskScreen()
                }

            }
        }
    }
}

data class Task(val id: Int, var title: String, var completed: Boolean)

// Define a ViewModel to manage the list of tasks
class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf(
        Task(1, "Task 1", false),
        Task(2, "Task 2", true),
        Task(3, "Task 3", false)
    )

    val tasks: List<Task> get() = _tasks

    fun updateTask(updatedTask: Task) {
        val index = _tasks.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            _tasks[index] = updatedTask
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onTaskUpdated: (Task) -> Unit
) {
    var isChecked by remember { mutableStateOf(task.completed) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { newCheckedState ->
                isChecked = newCheckedState
                // Update the task's completed property and call the callback
                task.completed = isChecked
                onTaskUpdated(task)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = task.title, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskUpdated: (Task) -> Unit
) {
    LazyColumn {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onTaskUpdated = onTaskUpdated
            )
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks = viewModel.tasks

    TaskList(
        tasks = tasks,
        onTaskUpdated = { updatedTask ->
            // Handle the updated task in your ViewModel
            viewModel.updateTask(updatedTask)
        }
    )
}





data class MyDataClass(var value: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyItem(data: MyDataClass, onUpdateValue: (String) -> Unit) {
    var editedValue by remember { mutableStateOf(data.value) }

    Column {
        TextField(
            value = editedValue,
            onValueChange = { newValue ->
                editedValue = newValue
            },
            label = { Text("Edit Value") }
        )

        Button(
            onClick = {
                onUpdateValue(editedValue)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(editedValue)
        }
    }
}

@Composable
fun MyLazyColumn(dataList: List<MyDataClass>) {
    LazyColumn {
        items(dataList) { dataItem ->
            MyItem(
                data = dataItem,
                onUpdateValue = { newValue ->
                    // Update the value of the data class object in the list
                    dataItem.value = newValue
                    Log.d("MYTAG", dataList[0].toString())
                }
            )
            Log.d("MYTAG", dataList.toString())
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Greeting(books: List<Book>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(
            items = books,
            key = { _, item -> item.id }
        ) { index, book ->
            var item by remember {
                mutableStateOf(book.isExpanded)
            }

            AnimatedContent(
                targetState = item,
                label = ""
            ) { isExpanded ->
                when (isExpanded) {
                    true -> OpenBookItem(modifier = Modifier, book,
                        onClick = {
                            item = it
                        }
                    )

                    false -> CloseBookItem(book,
                        onClick = {
                            item = it
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OpenBookItem(modifier: Modifier, book: Book, onClick: (value: Boolean) -> Unit) {
    ElevatedCard {
        Row {
            Text(
                text = book.title
            )
            Icon(
                modifier = modifier
                    .clickable {
                        onClick(false)
                    },
                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_up_24),
                contentDescription = ""
            )
        }
    }
}

@Composable
fun CloseBookItem(book: Book, onClick: (value: Boolean) -> Unit) {
    ElevatedCard {
        Row {
            Text(
                text = book.title
            )
            Icon(
                modifier = Modifier
                    .clickable {
                        onClick(true)
                    },
                painter = painterResource(id = R.drawable.baseline_keyboard_arrow_down_24),
                contentDescription = ""
            )
        }
    }
}
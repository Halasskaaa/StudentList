package com.example.studentlist

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studentlist.ui.theme.StudentListTheme
import java.nio.file.WatchEvent

data class Student(
    val id: Int,
    val name: String,
    val className: String
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme() {
                StudentListScreen()
            }
        }
    }
}

@Composable
fun StudentListScreen() {

    val context = LocalContext.current

    var name: String by remember {
        mutableStateOf("")
    }
    var className: String by remember {
        mutableStateOf("")
    }
    var nextId: Int by remember {
        mutableStateOf(1)
    }
    var students = remember {
        mutableStateListOf<Student>()
    }
    var nameError: Boolean by remember {
        mutableStateOf(false)
    }
    var classNameError: Boolean by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text (
            "Diáklista",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = { Text("Név")},
            supportingText = {
                if (nameError)
                    Text(
                        "A név megadása kötelező",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = className,
            onValueChange = {
                className = it
                classNameError = false
            },
            label = { Text("Osztály")},
            supportingText = {
                if (classNameError)
                    Text(
                        "A név megadása kötelező",
                        color = MaterialTheme.colorScheme.error
                    )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button (
            onClick = {
                nameError = name.isBlank()
                classNameError = name.isBlank()

                if(nameError || classNameError) {
                    Toast.makeText(
                        context, "Minen mezőt ki kelltölteni",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val newStudent = Student(
                        id = nextId,
                        name = name.trim(),
                        className = className.trim()
                    )
                    students.add(newStudent)
                    nextId++
                    Toast.makeText(
                        context,
                        "Sikeresen hozzáadta ${newStudent.name} tanulót",
                        Toast.LENGTH_SHORT
                    ).show()
                    name = ""
                    className = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hozzáadás")
        }
        HorizontalDivider()

        // TODO: szűrő, névre és osztály

        if (students.isEmpty())
            Text(
                "Nincsenek tanulók a rendszerben",
                style = MaterialTheme.typography.bodyLarge
            )
        else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = students,
                    key = {student -> student.id}
                ) {
                    student ->
                    StudentCard(
                        student = student,
                        onDelete = {
                            students.remove((student))
                        },
                        onEdit = {editedStudent ->
                            students[students.indexOf(student)] = editedStudent
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Student, onDelete:() -> Unit, onEdit: (Student) -> Unit) {
    var showDeleteDialog: Boolean by remember {
        mutableStateOf(false)
    }
    var showEditDialog: Boolean by remember {
        mutableStateOf(false)
    }

    var editName: String by remember {
        mutableStateOf(student.name)
    }
    var editClassName: String by remember {
        mutableStateOf(student.className)
    }
    var editNameError: Boolean by remember {
        mutableStateOf(false)
    }
    var editClassNameError: Boolean by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudentAvatar(student.name)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = student.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = student.className,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            IconButton(
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Icon(painter = painterResource(
                    id = R.drawable.delete_icon
                ), contentDescription = "Diák törlése")
            }
            IconButton(
                onClick = {
                    showEditDialog = true
                    editName = student.name
                    editClassName = student.className
                    editNameError = false
                    editClassNameError = false
                }
            ) {
                Icon(painter = painterResource(
                    id = R.drawable.edit_icon
                ), contentDescription = "Diák szerkeztése")
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Törlés megerősítése") },
            text = { Text("Biztosan törölni szeretnéd ${student.name} nevű diákot?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("Törlés")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Mégsem")
                }
            }
        )
    }
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Törlés szerkeztése") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            editName = it
                            editNameError = false
                        },
                        label = { Text("Név") },
                        supportingText = {
                            if (editNameError) {
                                Text(
                                    "A név megadása kötelező!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editClassName,
                        onValueChange = {
                            editClassName = it
                            editClassNameError = false
                        },
                        label = { Text("Osztály") },
                        supportingText = {
                            if (editClassNameError) {
                                Text(
                                    "Az osztály megadása kötelező!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        onEdit(Student(student.id, editName, editClassName))
                    }
                ) {
                    Text("Módosítás")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("Mégsem")
                }
            }
        )
    }
}

@Composable
fun StudentAvatar(name: String) {
    //monogram type shi
    val initials = name.split(" ")
        .filter { it.isNotBlank() }.take(2)
        .mapNotNull { it.firstOrNull() }
        .joinToString ( "" )
        .uppercase()
    Box (
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) { Text(
        initials,
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold
        )
    }
}

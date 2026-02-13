package au.edu.jcu.fittrackplus.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileContent(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(ui.name, viewModel::setName, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(ui.gender, viewModel::setGender, label = { Text("Gender") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            ui.age, viewModel::setAge,
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            ui.heightCm, viewModel::setHeightCm,
            label = { Text("Height(cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            ui.weightKg, viewModel::setWeightKg,
            label = { Text("Weight(kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = viewModel::saveProfile) { Text("Save Profile") }

        ui.message?.let { Text(it) }
    }
}
package au.edu.jcu.fittrackplus.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings

@Composable
fun ProfileContent(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = ui.name,
            onValueChange = viewModel::setName,
            label = { Text(s.name) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ui.gender,
            onValueChange = viewModel::setGender,
            label = { Text(s.gender) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ui.age,
            onValueChange = viewModel::setAge,
            label = { Text(s.age) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ui.heightCm,
            onValueChange = viewModel::setHeightCm,
            label = { Text(s.heightCm) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ui.weightKg,
            onValueChange = viewModel::setWeightKg,
            label = { Text(s.weightKg) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = viewModel::saveProfile) { Text(s.save) }

        ui.message?.let { Text(it) }
    }
}
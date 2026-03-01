package au.edu.jcu.fittrackplus.ui.schedule

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import au.edu.jcu.fittrackplus.ui.i18n.LocalStrings
import au.edu.jcu.fittrackplus.ui.i18n.localizedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    planId: Long,
    onBack: () -> Unit,
    vm: ScheduleViewModel = hiltViewModel()
) {
    val s = LocalStrings.current
    val isZh = s.isZh

    val selected by vm.selectedPlan.collectAsState()
    val form by vm.form.collectAsState()

    var editing by rememberSaveable { mutableStateOf(false) }
    var expanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(planId) { vm.bindPlanById(planId) }
    LaunchedEffect(selected?.id) { selected?.let { vm.loadToForm(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(s.planDetailTitle) },
                navigationIcon = { TextButton(onClick = onBack) { Text(s.back) } },
                actions = {
                    TextButton(
                        onClick = {
                            editing = !editing
                            if (!editing) expanded = false
                        }
                    ) {
                        Text(if (editing) s.cancel else s.edit)
                    }
                }
            )
        }
    ) { inner ->
        if (selected == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ✅ 非编辑：灰框；编辑：黑框
            OutlinedTextField(
                value = form.name,
                onValueChange = vm::onNameChange,
                label = { Text(s.planName) },
                enabled = editing,
                modifier = Modifier.fillMaxWidth()
            )

            // ✅ Category：非编辑灰框；编辑黑框 + 可下拉
            Box {
                OutlinedTextField(
                    value = form.selectedType.localizedName(isZh),
                    onValueChange = {},
                    readOnly = true,
                    enabled = editing, // ✅ 核心：跟随 editing
                    label = { Text(s.category) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .let { m ->
                            // 只有编辑态才允许点击展开
                            if (editing) {
                                m.clickable { expanded = true }
                            } else {
                                m
                            }
                        }
                )

                DropdownMenu(
                    expanded = expanded && editing,
                    onDismissRequest = { expanded = false }
                ) {
                    form.categoryOptions.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t.localizedName(isZh)) },
                            onClick = {
                                vm.onTypeChange(t)
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = form.durationMinutes,
                onValueChange = vm::onDurationChange,
                label = { Text(s.durationMinutes) },
                enabled = editing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.estimatedCalories,
                onValueChange = vm::onCaloriesChange,
                label = { Text(s.estimatedCalories) },
                enabled = editing,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.note,
                onValueChange = vm::onNoteChange,
                label = { Text(s.planNote) },
                enabled = editing,
                modifier = Modifier.fillMaxWidth()
            )

            form.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            if (editing) {
                Button(
                    onClick = { vm.updatePlan { editing = false; expanded = false } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(s.saveChanges)
                }
            }
        }
    }
}
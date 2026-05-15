package ec.edu.uisek.githubclient.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uisek.githubclient.viewmodels.RepoFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoForm(
    owner: String? = null,
    repoName: String? = null,
    onSaveSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: RepoFormViewModel = viewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    var name by remember { mutableStateOf(repoName ?: "") }
    var description by remember { mutableStateOf("") }
    val isEditing = owner != null && repoName != null

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            onSaveSuccess()
            viewModel.resetSuccess()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Editar Repositorio" else "Nuevo Repositorio") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        // Contenedor principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del repositorio") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isEditing // En GitHub el nombre suele ser parte de la URL, pero para simplificar...
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                if (errorMsg != null) {
                    Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        if (owner != null && repoName != null) {
                            viewModel.updateRepository(owner, repoName, name, description)
                        } else {
                            viewModel.createRepository(name, description)
                        }
                    },
                    enabled = name.isNotBlank() && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        Text("Guardando...")
                    } else {
                        Text(if (isEditing) "Actualizar Repositorio" else "Guardar Repositorio")
                    }
                }
            }
        }
    }
}

// Preview corregido
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun RepoFormPreview() {
    // Reemplaza por el nombre real de tu tema (ej. GithubClientTheme)
    MaterialTheme {
        RepoForm()
    }
}
package ec.edu.uisek.githubclient.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uisek.githubclient.ui.components.RepoItem
import ec.edu.uisek.githubclient.viewmodels.RepoListViewModel

@Composable
fun RepoList (
    modifier: Modifier=Modifier,
    viewModel: RepoListViewModel= viewModel(),
    onNavigateToForm:(String?, String?)->Unit={ _, _ -> }
){
    val repos by viewModel.repos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.errorMsg.collectAsState()
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToForm(null, null) },
                shape= CircleShape,
                elevation= FloatingActionButtonDefaults.elevation(8.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ){
                Icon(
                    imageVector= Icons.Default.Add,
                    contentDescription="Agregar repositorio"
                )
            }
        }
    ){paddingValues ->
        Box(
            modifier=Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ){
            if(isLoading){
                CircularProgressIndicator(
                    modifier= Modifier.align (Alignment.Center)
                )
            }
            errorMsg?.let { message->
                Text(
                    text = message,
                    color= MaterialTheme.colorScheme.error,
                    modifier= Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            if(!isLoading&&errorMsg==null){
                LazyColumn(
                    modifier=Modifier.fillMaxSize()
                ) {
                    items(repos, key = { it.id }) { repository ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                when (dismissValue) {
                                    SwipeToDismissBoxValue.EndToStart -> {
                                        viewModel.deleteRepo(repository.owner.login, repository.name)
                                        true
                                    }
                                    SwipeToDismissBoxValue.StartToEnd -> {
                                        onNavigateToForm(repository.owner.login, repository.name)
                                        false
                                    }
                                    else -> false
                                }
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
                                    else -> Color.Transparent
                                }
                                val alignment = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                    else -> Alignment.Center
                                }
                                val icon = when (dismissState.dismissDirection) {
                                    SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Edit
                                    else -> null
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color)
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = alignment
                                ) {
                                    if (icon != null) {
                                        Icon(
                                            icon,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            },
                            content = {
                                RepoItem(repository)
                            }
                        )
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun RepoListPreview(){
    RepoList()
}
package ec.edu.uisek.githubclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ec.edu.uisek.githubclient.ui.screens.RepoForm
import ec.edu.uisek.githubclient.ui.screens.RepoList
import ec.edu.uisek.githubclient.ui.theme.GithubClientTheme
import ec.edu.uisek.githubclient.viewmodels.RepoListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val listViewModel: RepoListViewModel=viewModel()
            GithubClientTheme {
                var currentScreen by remember { mutableStateOf("repoList") }
                var selectedOwner by remember { mutableStateOf<String?>(null) }
                var selectedRepo by remember { mutableStateOf<String?>(null) }

                when(currentScreen){
                    "repoList" -> RepoList(
                        onNavigateToForm = { owner, repo ->
                            selectedOwner = owner
                            selectedRepo = repo
                            currentScreen = "repoForm"
                        }
                    )
                    "repoForm" -> RepoForm(
                        owner = selectedOwner,
                        repoName = selectedRepo,
                        onSaveSuccess = {
                            listViewModel.fetchRepos()
                            currentScreen="repoList"},
                        onBackClick = { currentScreen="repoList" }
                    )
                }
            }
        }
    }
}
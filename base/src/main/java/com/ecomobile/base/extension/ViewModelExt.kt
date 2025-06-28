
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun ViewModel.runOnBackgroundThread(callback: () -> Unit) = CoroutineScope(Dispatchers.IO).launch {
    callback()
}

fun ViewModel.runOnMainThread(callback: () -> Unit) = CoroutineScope(Dispatchers.Main).launch {
    callback()
}
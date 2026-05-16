package friends.mobile.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import friends.mobile.feature.events.presentation.CreateEventViewModel

class CreateEventViewModelFactory(
    private val selectedDate: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateEventViewModel(selectedDate = selectedDate) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

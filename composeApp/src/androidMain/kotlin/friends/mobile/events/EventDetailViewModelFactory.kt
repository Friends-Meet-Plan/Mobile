package friends.mobile.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import friends.mobile.feature.events.presentation.eventdetail.EventDetailViewModel

class EventDetailViewModelFactory(
    private val eventId: String,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EventDetailViewModel(eventId = eventId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

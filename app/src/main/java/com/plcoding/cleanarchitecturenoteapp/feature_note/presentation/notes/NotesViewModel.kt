package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.use_case.NotesUseCase
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteEvents
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NoteOrder
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.NotesState
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesUseCase: NotesUseCase
) : ViewModel() {
    private val _state = mutableStateOf(NotesState())
    val state: State<NotesState> = _state
    private var recentlyDeletedNote: Note? = null
    private var getNotesJob: Job? = null
    init {
        getNotes(NoteOrder.Date(OrderType.Descending))
    }
    fun onEvent(noteEvents: NoteEvents) {
        when(noteEvents) {
            is NoteEvents.Order -> {
                if(state.value.noteOrder::class == noteEvents.noteOrder::class &&
                    state.value.noteOrder.orderType == noteEvents.noteOrder.orderType) {
                    return
                }
                getNotes(noteEvents.noteOrder)
            }

            is NoteEvents.DeleteNote -> {
                viewModelScope.launch {
                    notesUseCase.deleteNote(noteEvents.note)
                    recentlyDeletedNote = noteEvents.note
                }
            }
            NoteEvents.RestoreNote -> {
                viewModelScope.launch {
                    notesUseCase.addNote(recentlyDeletedNote ?: return@launch)
                    recentlyDeletedNote = null
                }
            }
            NoteEvents.ToggleOrderSection -> {
                _state.value = _state.value.copy(
                    isOrderSelectionVisible = !state.value.isOrderSelectionVisible
                )
            }
        }
    }

    private fun getNotes(noteOrder: NoteOrder) {
        getNotesJob?.cancel()
        getNotesJob = notesUseCase.getNotes(noteOrder)
            .onEach {notes ->
                _state.value = state.value.copy(
                    notesList = notes,
                    noteOrder = noteOrder
                )
            }
            .launchIn(viewModelScope)
    }
}
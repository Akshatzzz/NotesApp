package com.plcoding.cleanarchitecturenoteapp.feature_note.domain.util

import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note

data class NotesState(
    val notesList: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSelectionVisible: Boolean = false
) {
}
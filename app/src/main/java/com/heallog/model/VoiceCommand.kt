package com.heallog.model

sealed class VoiceCommand {
    data class NavigateTo(val destination: String) : VoiceCommand()
    data class SetPainLevel(val level: Int) : VoiceCommand()
    data class AddNote(val text: String) : VoiceCommand()
    data object StartRecord : VoiceCommand()
    data object GoHome : VoiceCommand()
    data class TextInput(val text: String) : VoiceCommand()
}

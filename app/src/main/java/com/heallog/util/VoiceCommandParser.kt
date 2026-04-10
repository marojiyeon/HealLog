package com.heallog.util

import com.heallog.model.VoiceCommand

/**
 * Parses voice input text and converts it to VoiceCommand objects.
 *
 * Supports Korean voice commands for:
 * - Recording injuries: "기록", "기록해", "기록하다"
 * - Navigating home: "홈", "홈으로", "처음으로"
 * - Setting pain level: "통증 N", "레벨 N"
 * - Adding notes: "메모 <text>"
 * - Navigation: "바디맵", "상세", "설정"
 * - Plain text input (fallback)
 */
object VoiceCommandParser {

    private val koreanDigits = mapOf(
        "일" to 1, "이" to 2, "삼" to 3, "사" to 4, "오" to 5,
        "육" to 6, "칠" to 7, "팔" to 8, "구" to 9, "십" to 10
    )

    // Regex patterns for commands
    private val recordCommandPatterns = listOf("기록 추가", "기록해", "기록하다", "기록하기")
    private val homeCommandPatterns = listOf("홈으로", "홈 화면", "처음으로")
    private val painCommandKeywords = listOf("통증", "레벨", "수준")
    private val bodyMapNavigationPatterns = listOf("바디맵", "몸")
    private val detailNavigationPatterns = listOf("상세", "디테일")
    private val memoKeyword = "메모"
    private val settingsKeyword = "설정"

    fun parse(text: String): VoiceCommand {
        val normalized = text.trim()

        return when {
            matchesRecordCommand(normalized) -> VoiceCommand.StartRecord
            matchesHomeCommand(normalized) -> VoiceCommand.GoHome
            matchesPainLevelCommand(normalized) -> extractPainLevel(normalized) ?: VoiceCommand.TextInput(normalized)
            matchesMemoCommand(normalized) -> extractNote(normalized) ?: VoiceCommand.TextInput(normalized)
            matchesBodyMapNavigation(normalized) -> VoiceCommand.NavigateTo("bodymap")
            matchesDetailNavigation(normalized) -> VoiceCommand.NavigateTo("detail")
            matchesSettingsNavigation(normalized) -> VoiceCommand.NavigateTo("settings")
            else -> VoiceCommand.TextInput(normalized)
        }
    }

    private fun matchesRecordCommand(text: String): Boolean =
        recordCommandPatterns.any { text.contains(it) }

    private fun matchesHomeCommand(text: String): Boolean =
        text == "홈" || homeCommandPatterns.any { text.contains(it) }

    private fun matchesPainLevelCommand(text: String): Boolean =
        painCommandKeywords.any { text.contains(it) }

    private fun matchesMemoCommand(text: String): Boolean =
        text.contains(memoKeyword)

    private fun matchesBodyMapNavigation(text: String): Boolean =
        bodyMapNavigationPatterns.any { text.contains(it) }

    private fun matchesDetailNavigation(text: String): Boolean =
        detailNavigationPatterns.any { text.contains(it) }

    private fun matchesSettingsNavigation(text: String): Boolean =
        text.contains(settingsKeyword)

    /**
     * Extracts pain level (1-10) from text containing pain-related keywords.
     */
    private fun extractPainLevel(text: String): VoiceCommand.SetPainLevel? {
        val level = text.split("\\s+".toRegex())
            .firstNotNullOfOrNull { token -> parseNumber(token)?.takeIf { it in 1..10 } }
        return level?.let { VoiceCommand.SetPainLevel(it) }
    }

    /**
     * Extracts note text after the "메모" keyword.
     */
    private fun extractNote(text: String): VoiceCommand.AddNote? {
        val memoIndex = text.indexOf(memoKeyword)
        if (memoIndex == -1) return null

        val noteText = text.substring(memoIndex + memoKeyword.length).trimStart()
        return if (noteText.isNotBlank()) VoiceCommand.AddNote(noteText) else null
    }

    /**
     * Parses a token as either a numeric value or a Korean digit word.
     */
    private fun parseNumber(token: String): Int? =
        token.toIntOrNull() ?: koreanDigits[token]
}

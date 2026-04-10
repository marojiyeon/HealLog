package com.heallog.util

import com.heallog.model.VoiceCommand

object VoiceCommandParser {

    private val koreanDigits = mapOf(
        "일" to 1, "이" to 2, "삼" to 3, "사" to 4, "오" to 5,
        "육" to 6, "칠" to 7, "팔" to 8, "구" to 9, "십" to 10
    )

    // Matches Arabic or Korean numerals 1-10
    private val numberRegex = Regex("([1-9]|10|일|이|삼|사|오|육|칠|팔|구|십)")

    fun parse(text: String): VoiceCommand {
        val normalized = text.trim()

        // StartRecord: "기록 추가" or "기록해"
        if (normalized.contains("기록 추가") || normalized.contains("기록해") ||
            normalized.contains("기록하다") || normalized.contains("기록하기")) {
            return VoiceCommand.StartRecord
        }

        // GoHome: "홈으로" or "홈"
        if (normalized.contains("홈으로") || normalized == "홈" ||
            normalized.contains("홈 화면") || normalized.contains("처음으로")) {
            return VoiceCommand.GoHome
        }

        // SetPainLevel: "통증 레벨 N", "통증 N", "레벨 N", "통증 수준 N"
        val isPainCommand = normalized.contains("통증") || normalized.contains("레벨") ||
                normalized.contains("수준")
        if (isPainCommand) {
            val match = numberRegex.find(normalized)
            if (match != null) {
                val level = parseNumber(match.value)
                if (level != null && level in 1..10) {
                    return VoiceCommand.SetPainLevel(level)
                }
            }
        }

        // AddNote: "메모 <text>" – extract text after "메모"
        val memoIdx = normalized.indexOf("메모")
        if (memoIdx != -1) {
            val noteText = normalized.substring(memoIdx + 2).trimStart()
            if (noteText.isNotBlank()) {
                return VoiceCommand.AddNote(noteText)
            }
        }

        // NavigateTo: explicit navigation phrases
        when {
            normalized.contains("바디맵") || normalized.contains("몸") -> {
                return VoiceCommand.NavigateTo("bodymap")
            }
            normalized.contains("상세") || normalized.contains("디테일") -> {
                return VoiceCommand.NavigateTo("detail")
            }
            normalized.contains("설정") -> {
                return VoiceCommand.NavigateTo("settings")
            }
        }

        // Fallback: plain text input
        return VoiceCommand.TextInput(normalized)
    }

    private fun parseNumber(token: String): Int? {
        return token.toIntOrNull() ?: koreanDigits[token]
    }
}

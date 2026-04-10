package com.heallog.util

import com.heallog.model.VoiceCommand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VoiceCommandParserTest {

    // --- StartRecord ---

    @Test
    fun `기록 추가 returns StartRecord`() {
        assertEquals(VoiceCommand.StartRecord, VoiceCommandParser.parse("기록 추가"))
    }

    @Test
    fun `기록해 returns StartRecord`() {
        assertEquals(VoiceCommand.StartRecord, VoiceCommandParser.parse("기록해"))
    }

    @Test
    fun `기록하다 returns StartRecord`() {
        assertEquals(VoiceCommand.StartRecord, VoiceCommandParser.parse("기록하다"))
    }

    @Test
    fun `기록하기 returns StartRecord`() {
        assertEquals(VoiceCommand.StartRecord, VoiceCommandParser.parse("기록하기"))
    }

    // --- GoHome ---

    @Test
    fun `홈으로 returns GoHome`() {
        assertEquals(VoiceCommand.GoHome, VoiceCommandParser.parse("홈으로"))
    }

    @Test
    fun `홈 alone returns GoHome`() {
        assertEquals(VoiceCommand.GoHome, VoiceCommandParser.parse("홈"))
    }

    @Test
    fun `홈 화면 returns GoHome`() {
        assertEquals(VoiceCommand.GoHome, VoiceCommandParser.parse("홈 화면으로 가줘"))
    }

    @Test
    fun `처음으로 returns GoHome`() {
        assertEquals(VoiceCommand.GoHome, VoiceCommandParser.parse("처음으로"))
    }

    // --- SetPainLevel (Arabic numerals) ---

    @Test
    fun `통증 레벨 7 returns SetPainLevel 7`() {
        assertEquals(VoiceCommand.SetPainLevel(7), VoiceCommandParser.parse("통증 레벨 7"))
    }

    @Test
    fun `통증 1 returns SetPainLevel 1`() {
        assertEquals(VoiceCommand.SetPainLevel(1), VoiceCommandParser.parse("통증 1"))
    }

    @Test
    fun `레벨 10 returns SetPainLevel 10`() {
        assertEquals(VoiceCommand.SetPainLevel(10), VoiceCommandParser.parse("레벨 10"))
    }

    @Test
    fun `통증 수준 5 returns SetPainLevel 5`() {
        assertEquals(VoiceCommand.SetPainLevel(5), VoiceCommandParser.parse("통증 수준 5"))
    }

    // --- SetPainLevel (Korean numerals) ---

    @Test
    fun `통증 칠 returns SetPainLevel 7`() {
        assertEquals(VoiceCommand.SetPainLevel(7), VoiceCommandParser.parse("통증 칠"))
    }

    @Test
    fun `레벨 십 returns SetPainLevel 10`() {
        assertEquals(VoiceCommand.SetPainLevel(10), VoiceCommandParser.parse("레벨 십"))
    }

    @Test
    fun `통증 수준 삼 returns SetPainLevel 3`() {
        assertEquals(VoiceCommand.SetPainLevel(3), VoiceCommandParser.parse("통증 수준 삼"))
    }

    // --- AddNote ---

    @Test
    fun `메모 with text returns AddNote`() {
        assertEquals(VoiceCommand.AddNote("오늘 많이 아팠다"), VoiceCommandParser.parse("메모 오늘 많이 아팠다"))
    }

    @Test
    fun `메모 alone without text falls through to TextInput`() {
        val result = VoiceCommandParser.parse("메모")
        assertTrue(result is VoiceCommand.TextInput)
    }

    // --- NavigateTo ---

    @Test
    fun `바디맵 returns NavigateTo bodymap`() {
        assertEquals(VoiceCommand.NavigateTo("bodymap"), VoiceCommandParser.parse("바디맵으로 가줘"))
    }

    @Test
    fun `몸 returns NavigateTo bodymap`() {
        assertEquals(VoiceCommand.NavigateTo("bodymap"), VoiceCommandParser.parse("몸 상태 보기"))
    }

    @Test
    fun `설정 returns NavigateTo settings`() {
        assertEquals(VoiceCommand.NavigateTo("settings"), VoiceCommandParser.parse("설정 열기"))
    }

    // --- Fallback ---

    @Test
    fun `unrecognized text returns TextInput`() {
        val input = "오늘 날씨가 좋다"
        assertEquals(VoiceCommand.TextInput(input), VoiceCommandParser.parse(input))
    }

    @Test
    fun `empty string returns TextInput with empty string`() {
        assertEquals(VoiceCommand.TextInput(""), VoiceCommandParser.parse("  "))
    }

    // --- Priority ordering: StartRecord wins over pain keyword ---

    @Test
    fun `기록 추가 takes priority over 통증 keyword in same phrase`() {
        assertEquals(VoiceCommand.StartRecord, VoiceCommandParser.parse("통증 기록 추가해줘"))
    }

    // --- Edge cases ---

    @Test
    fun `pain level 0 is not a valid command and falls back to TextInput`() {
        // 0 is not in [1..10] range — the numberRegex doesn't match 0 either
        val result = VoiceCommandParser.parse("통증 레벨 0")
        assertTrue(result is VoiceCommand.TextInput)
    }

    @Test
    fun `pain level text without number returns TextInput`() {
        val result = VoiceCommandParser.parse("통증이 심해요")
        assertTrue(result is VoiceCommand.TextInput)
    }
}

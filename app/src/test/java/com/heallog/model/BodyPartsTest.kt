package com.heallog.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BodyPartsTest {

    @Test
    fun `findById returns correct part for known id`() {
        val part = BodyParts.findById("left_knee")
        assertNotNull(part)
        assertEquals("left_knee", part!!.id)
        assertEquals("왼쪽 무릎", part.nameKo)
    }

    @Test
    fun `findById returns null for unknown id`() {
        val part = BodyParts.findById("nonexistent_part")
        assertNull(part)
    }

    @Test
    fun `all contains 21 body parts`() {
        assertEquals(21, BodyParts.all.size)
    }

    @Test
    fun `all ids are unique`() {
        val ids = BodyParts.all.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `chest has front position but no back position`() {
        val def = BodyParts.definitions.first { it.part.id == "chest" }
        assertNotNull(def.frontPosition)
        assertNull(def.backPosition)
    }

    @Test
    fun `upper_back has back position but no front position`() {
        val def = BodyParts.definitions.first { it.part.id == "upper_back" }
        assertNull(def.frontPosition)
        assertNotNull(def.backPosition)
    }

    @Test
    fun `lower_back has back position but no front position`() {
        val def = BodyParts.definitions.first { it.part.id == "lower_back" }
        assertNull(def.frontPosition)
        assertNotNull(def.backPosition)
    }

    @Test
    fun `positions are within 0-1 range`() {
        BodyParts.definitions.forEach { def ->
            def.frontPosition?.let { pos ->
                assert(pos.x in 0f..1f) { "${def.part.id} front.x out of range: ${pos.x}" }
                assert(pos.y in 0f..1f) { "${def.part.id} front.y out of range: ${pos.y}" }
            }
            def.backPosition?.let { pos ->
                assert(pos.x in 0f..1f) { "${def.part.id} back.x out of range: ${pos.x}" }
                assert(pos.y in 0f..1f) { "${def.part.id} back.y out of range: ${pos.y}" }
            }
        }
    }

    @Test
    fun `symmetric parts have mirrored x positions`() {
        val leftKnee = BodyParts.definitions.first { it.part.id == "left_knee" }
        val rightKnee = BodyParts.definitions.first { it.part.id == "right_knee" }
        assertEquals(
            1f - leftKnee.frontPosition!!.x,
            rightKnee.frontPosition!!.x,
            0.01f
        )
        assertEquals(leftKnee.frontPosition.y, rightKnee.frontPosition.y, 0.001f)
    }
}

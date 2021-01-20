package co.ravn.kevin.peopleofstarwars

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class CustomCapitalizeUnitTest {
    @Test
    fun customCapitalizeNA() {
        assertEquals("N/A", "n/a".customCapitalize(Locale.getDefault()))
    }

    @Test
    fun customCapitalizeSingleWord() {
        assertEquals("White", "white".customCapitalize(Locale.getDefault()))
    }

    @Test
    fun customCapitalizeMultiWord() {
        assertEquals("White, Red", "white, red".customCapitalize(Locale.getDefault()))
    }
}
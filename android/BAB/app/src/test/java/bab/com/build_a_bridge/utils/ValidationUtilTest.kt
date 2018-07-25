package bab.com.build_a_bridge.utils

import org.junit.Assert.*
import org.junit.Test

class ValidationUtilTest {
    @Test
    fun shouldPassName() {
        val expected = null
        val actual = ValidationUtil.isNameValid("Name")
        assertEquals(expected, actual)
    }

    @Test
    fun shouldPassNameWithArgs(){
        val expected = null
        val actual = ValidationUtil.isNameValid("Apple Pie", ' ')
        assertEquals(expected, actual)
    }

    @Test
    fun shouldFailName(){
        val expected = ' '
        val actual = ValidationUtil.isNameValid("Blueberry Pie")
    }

    @Test
    fun shouldFailNameWithArgs(){
        val expected = '*'
        val actual = ValidationUtil.isNameValid("Banana* Cream Pie", '_')
        assertEquals(expected, actual)
    }

}
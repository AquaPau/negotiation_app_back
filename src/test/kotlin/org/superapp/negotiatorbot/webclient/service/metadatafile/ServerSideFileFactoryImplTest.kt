package org.superapp.negotiatorbot.webclient.service.metadatafile

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.superapp.negotiatorbot.webclient.entity.BusinessType
import org.superapp.negotiatorbot.webclient.entity.User

class DocumentMetadataFactoryTest {

    @Test
    fun `createFile should create file with valid extension`() {
        //given
        val user = mock(User::class.java)
        `when`(user.id).thenReturn(123L)
        val businessType = mock(BusinessType::class.java)
        val fileName = "test.csv"

        //when
        val file = DocumentHelper.createFile(user, businessType, fileName)

        assertEquals("csv", file.extension)
        assertEquals("test", file.name)
        assertTrue(file.path!!.contains( "users/${user.id}/"))
        assertTrue(file.path!!.contains("/test.csv"))
    }


    @Nested
    inner class InvalidNames {

        @Test
        fun `createFile should throw exception for invalid extension`() {
            //given
            val user = mock(User::class.java)
            `when`(user.id).thenReturn(123L)

            val businessType = mock(BusinessType::class.java)
            val fileName = "test.invalid"
            //then
            assertThrows<IllegalArgumentException> {
                DocumentHelper.createFile(user, businessType, fileName)
            }
        }

        @Test
        fun `createFile should throw exception for empty extension`() {
            val user = mock(User::class.java)
            `when`(user.id).thenReturn(123L)
            val businessType = mock(BusinessType::class.java)

            val fileName = "test."
            assertThrows<IllegalArgumentException> {
                DocumentHelper.createFile(user, businessType, fileName)
            }
        }

        @Test
        fun `createFile should throw exception for empty filename`() {
            val user = mock(User::class.java)
            `when`(user.id).thenReturn(123L)
            val businessType = mock(BusinessType::class.java)

            val fileName = ""
            assertThrows<IllegalArgumentException> {
                DocumentHelper.createFile(user, businessType, fileName)
            }
        }

        @Test
        fun `createFile should throw exception for blank filename`() {
            val user = mock(User::class.java)
            `when`(user.id).thenReturn(123L)
            val businessType = mock(BusinessType::class.java)

            val fileName = " "
            assertThrows<IllegalArgumentException> {
                DocumentHelper.createFile(user, businessType, fileName)
            }
        }

        @Test
        fun `createFile should throw exception for filename like dot txt`() {
            val user = mock(User::class.java)
            `when`(user.id).thenReturn(123L)
            val businessType = mock(BusinessType::class.java)

            val fileName = ".txt"

            assertThrows<IllegalArgumentException> {
                DocumentHelper.createFile(user, businessType, fileName)
            }
        }

        @Test
        fun `createFile should throw exception for single dot filename`() {
            val user = mock(User::class.java)
            `when`(user.id).thenReturn(123L)
            val businessType = mock(BusinessType::class.java)

            val fileName = "."

            assertThrows<IllegalArgumentException> {
                DocumentHelper.createFile(user, businessType, fileName)
            }
        }

        @Test
        fun `createFile should throw exception for filename with multiple dots but no valid name`() {
            val user = mock(User::class.java)
            `when`(user.id).thenReturn(123L)
            val businessType = mock(BusinessType::class.java)

            val fileName = "..."

            assertThrows<IllegalArgumentException> {
                DocumentHelper.createFile(user, businessType, fileName)
            }
        }
    }

}
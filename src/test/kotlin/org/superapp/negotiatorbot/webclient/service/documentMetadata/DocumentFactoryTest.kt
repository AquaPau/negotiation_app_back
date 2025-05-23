package org.superapp.negotiatorbot.webclient.service.documentMetadata

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.superapp.negotiatorbot.webclient.enums.BusinessType
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.exception.DocumentNotValidException

class DocumentFactoryTest {

    private fun createTestFile(fileNameWithExtension: String) =
        DocumentFactory.createFile(
            userId = 123L,
            businessType = BusinessType.USER,
            relatedId = 456L,
            documentType = DocumentType.DEFAULT,
            fileNameWithExtension = fileNameWithExtension
        )

    @Test
    fun `createFile should create file with valid extension`() {
        // given
        val fileName = "test.csv"

        // when
        val file = createTestFile(fileName)

        // then
        assertEquals("csv", file.extension)
        assertEquals("test", file.name)
        assertTrue(file.path!!.contains("users/123/"))
        assertTrue(file.path!!.contains("/test.csv"))
    }

    @Nested
    inner class InvalidNames {

        @Test
        fun `createFile should throw exception for invalid extension`() {
            val fileName = "test.invalid"
            assertThrows<DocumentNotValidException> { createTestFile(fileName) }
        }

        @Test
        fun `createFile should throw exception for empty extension`() {
            val fileName = "test."
            assertThrows<DocumentNotValidException> { createTestFile(fileName) }
        }

        @Test
        fun `createFile should throw exception for empty filename`() {
            val fileName = ""
            assertThrows<DocumentNotValidException> { createTestFile(fileName) }
        }

        @Test
        fun `createFile should throw exception for blank filename`() {
            val fileName = " "
            assertThrows<DocumentNotValidException> { createTestFile(fileName) }
        }

        @Test
        fun `createFile should throw exception for filename like dot txt`() {
            val fileName = ".txt"
            assertThrows<DocumentNotValidException> { createTestFile(fileName) }
        }

        @Test
        fun `createFile should throw exception for single dot filename`() {
            val fileName = "."
            assertThrows<DocumentNotValidException> { createTestFile(fileName) }
        }

        @Test
        fun `createFile should throw exception for filename with multiple dots but no valid name`() {
            val fileName = "..."
            assertThrows<DocumentNotValidException> { createTestFile(fileName) }
        }
    }

}
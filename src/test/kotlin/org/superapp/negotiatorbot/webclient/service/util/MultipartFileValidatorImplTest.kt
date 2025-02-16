package org.superapp.negotiatorbot.webclient.service.util

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.multipart.MultipartFile

@SpringBootTest(
    classes = [MultipartFileValidatorImpl::class],
)
class MultipartFileValidatorImplTest {

    @Test
    fun `happy path`() {
        //given
        val multipartFile = mock(MultipartFile::class.java)
        val fileNameWithException = "test.pdf"
        whenever(multipartFile.size).thenReturn(MAX_ALLOWED_SIZE_IN_MB)
        //then
        assertDoesNotThrow {
            multipartFileValidator.validate(multipartFile, fileNameWithException)
        }
    }

    @Test
    fun `too big file should fail`() {
        //given
        val multipartFile = mock(MultipartFile::class.java)
        val fileNameWithException = "test.pdf"
        whenever(multipartFile.size).thenReturn(MAX_ALLOWED_SIZE_IN_MB * 1024 * 1024 + 10)
        //then
        assertThrows<IllegalArgumentException> {
            multipartFileValidator.validate(multipartFile, fileNameWithException)
        }
    }

    @Test
    fun `wrong extension file should fail`() {
        //given
        val multipartFile = mock(MultipartFile::class.java)
        val fileNameWithException = "test.txt"
        whenever(multipartFile.size).thenReturn(MAX_ALLOWED_SIZE_IN_MB)

        //then
        assertThrows<IllegalArgumentException> {
            multipartFileValidator.validate(multipartFile, fileNameWithException)
        }
    }

    @Test
    fun `notFound extension file should fail`() {
        //given
        val multipartFile = mock(MultipartFile::class.java)
        val fileNameWithException = "testpdf"
        whenever(multipartFile.size).thenReturn(MAX_ALLOWED_SIZE_IN_MB)

        //then
        assertThrows<IllegalArgumentException> {
            multipartFileValidator.validate(multipartFile, fileNameWithException)
        }
    }


    @Autowired
    private lateinit var multipartFileValidator: MultipartFileValidator

}
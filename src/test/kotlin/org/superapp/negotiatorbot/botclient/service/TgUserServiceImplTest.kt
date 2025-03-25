package org.superapp.negotiatorbot.botclient.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.superapp.negotiatorbot.botclient.model.TgUser
import org.superapp.negotiatorbot.botclient.repository.TgUserRepository
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType

@SpringBootTest(
    classes = [TgUserServiceImpl::class],
)
class TgUserServiceImplTest {

    @Test
    fun `getDocAndPromptTypes should return Pair when both types are present`() {
        // Given

        val tgUser: TgUser = mock()
        whenever(tgUser.chosenPromptType).thenReturn(PromptType.RISKS)
        whenever(tgUser.chosenDocumentType).thenReturn(DocumentType.LABOR_CONTRACT)


        // When
        val result = tgUserService.getTypes(tgUser)

        // Then
        assertNotNull(result)
        assertEquals(DocumentType.LABOR_CONTRACT, result?.first)
        assertEquals(PromptType.RISKS, result?.second)
    }

    @Test
    fun `getDocAndPromptTypes should return null when prompt not present`() {
        // Given

        val tgUser: TgUser = mock()
        whenever(tgUser.chosenDocumentType).thenReturn(DocumentType.LABOR_CONTRACT)


        // When
        val result = tgUserService.getTypes(tgUser)

        // Then
        assertNull(result)
    }

    @Test
    fun `getDocAndPromptTypes should return null when contract not present`() {
        // Given

        val tgUser: TgUser = mock()
        whenever(tgUser.chosenPromptType).thenReturn(PromptType.RISKS)


        // When
        val result = tgUserService.getTypes(tgUser)

        // Then
        assertNull(result)
    }


    @Autowired
    private lateinit var tgUserService: TgUserService

    @MockitoBean
    private lateinit var tgUserRepository: TgUserRepository
}
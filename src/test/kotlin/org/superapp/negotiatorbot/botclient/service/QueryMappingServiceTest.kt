package org.superapp.negotiatorbot.botclient.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.superapp.negotiatorbot.botclient.dto.ChosenDocumentOption
import org.superapp.negotiatorbot.botclient.dto.ChosenCounterpartyOption
import org.superapp.negotiatorbot.webclient.enums.DocumentType
import org.superapp.negotiatorbot.webclient.enums.PromptType

@SpringBootTest(
    classes = [QueryMappingService::class],
)
class QueryMappingServiceTest {

    @Test
    fun `too long query should throw`() {
        val mappingQuery = "DocumentTypeQueryHandle"
        val addedQuery = "{\"tgDocumentId\":1,\"documentType\":\"LABOR_CONTRACT\"}"
        //then
        assertThrows<IllegalArgumentException> { queryMappingService.toCallbackQuery(mappingQuery, addedQuery) }
    }

    @Test
    fun `should return mapping query`() {
        //given
        val mappingQuery = "expectedMapping"
        val addedQuery = "addedQuery"
        val fullQuery = mappingQuery + queryMappingService.divider + addedQuery
        //then
        assertEquals(mappingQuery, queryMappingService.toMapQuery(fullQuery))
    }

    @Test
    fun `should parse and deparse chosenDocumentOption payload`() {
        //given
        val mappingQuery = "expectedMapping"
        val expected = ChosenDocumentOption(1, DocumentType.LABOR_CONTRACT_EMPLOYEE)

        val fullQuery = queryMappingService.toCallbackQuery(mappingQuery, expected)

        //then
        assertEquals(expected, queryMappingService.getPayload(fullQuery, ChosenDocumentOption::class.java))
    }

    @Test
    fun `should parse and deparse chosenPromptOption payload`() {
        //given
        val mappingQuery = "expectedMapping"
        val expected = ChosenCounterpartyOption(1, PromptType.RISKS)
        val fullQuery = queryMappingService.toCallbackQuery(mappingQuery, expected)

        //then
        assertEquals(expected, queryMappingService.getPayload(fullQuery, ChosenCounterpartyOption::class.java))
    }

    @Autowired
    private lateinit var queryMappingService: QueryMappingService
}
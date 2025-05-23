package org.superapp.negotiatorbot.botclient.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.superapp.negotiatorbot.botclient.dto.ChosenCounterpartyOption
import org.superapp.negotiatorbot.botclient.dto.ChosenDocumentOption
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentCounterpartyType
import org.superapp.negotiatorbot.botclient.model.TelegramDocumentType
import org.superapp.negotiatorbot.webclient.enums.DocumentType

@SpringBootTest(
    classes = [QueryMappingService::class],
)
class QueryMappingServiceTest {

    @Test
    fun `too long query should throw`() {
        val mappingQuery = "DocumentTypeQueryHandle"
        val addedQuery = "{\"tgDocumentId\":1,\"documentType\":\"SALES_CONTRACT\",\"contractorType\":\"CUSTOMER\"}"
        //then
        assertThrows<IllegalArgumentException> { queryMappingService.toCallbackQuery(mappingQuery, addedQuery) }
    }

    @Test
    fun `should return mapping query`() {
        //given
        val mappingQuery = "expectedMapping"
        val addedQuery = "addedQuery"
        val fullQuery = mappingQuery + QueryMappingService.DIVIDER + addedQuery
        //then
        assertEquals(mappingQuery, queryMappingService.toMapQuery(fullQuery))
    }

    @Test
    fun `should parse and deparse chosenDocumentOption payload`() {
        //given
        val mappingQuery = "expectedMapping"
        val expected = ChosenDocumentOption(1, TelegramDocumentType.SALES_CONTRACT)

        val fullQuery = queryMappingService.toCallbackQuery(mappingQuery, expected)

        //then
        assertEquals(expected, queryMappingService.getPayload(fullQuery, ChosenDocumentOption::class.java))
    }

    @Test
    fun `should parse and deparse chosenPromptOption payload`() {
        //given
        val mappingQuery = "expectedMapping"
        val expected = ChosenCounterpartyOption(1, TelegramDocumentCounterpartyType.GOODS_CUSTOMER)
        val fullQuery = queryMappingService.toCallbackQuery(mappingQuery, expected)

        //then
        assertEquals(expected, queryMappingService.getPayload(fullQuery, ChosenCounterpartyOption::class.java))
    }

    @Autowired
    private lateinit var queryMappingService: QueryMappingService
}
package org.superapp.negotiatorbot.botclient.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
 classes = [ObjectMapper::class, QueryMappingService::class],
)
class QueryMappingServiceTest{

 @Test
 fun `should return full query`(){
  //given
  val mappingQuery = "expectedMapping"
  val addedQuery = "addedQuery"
  val expectedFullQuery = mappingQuery + queryMappingService.divider + addedQuery
  //then
  assertEquals(expectedFullQuery, queryMappingService.toCallbackQuery(mappingQuery, addedQuery))
 }

 @Test
 fun `should return mapping query`(){
  //given
  val mappingQuery = "expectedMapping"
  val addedQuery = "addedQuery"
  val fullQuery = mappingQuery + queryMappingService.divider + "addedQuery"
  //then
  assertEquals(mappingQuery, queryMappingService.toMapQuery(fullQuery))
 }

  @Autowired
  private lateinit var queryMappingService: QueryMappingService
 }
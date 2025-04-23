package org.superapp.negotiatorbot.botclient.model

import org.superapp.negotiatorbot.botclient.model.TelegramDocumentCounterpartyType.*

enum class TelegramDocumentType(
    private val contractName: String,
    private val counterparties: List<TelegramDocumentCounterpartyType>
) {
    SALES_CONTRACT("Договор купли-продажи", listOf(SELLER, GOODS_CUSTOMER)),
    SERVICE_CONTRACT("Договор оказания услуг/выполнения работ", listOf(CONTRACTOR, SERVICES_CUSTOMER));

    fun getContractName() = this.contractName

    fun getCounterparties() = this.counterparties

}
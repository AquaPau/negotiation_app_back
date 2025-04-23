package org.superapp.negotiatorbot.botclient.model

enum class TelegramDocumentCounterpartyType(private val counterpartyName: String) {
    SELLER("Продавец"),
    CONTRACTOR("Исполнитель"),
    SERVICES_CUSTOMER("Заказчик"),
    GOODS_CUSTOMER("Покупатель");

    fun getCounterpartyName() = this.counterpartyName
}
package org.superapp.negotiatorbot.webclient.enums

import org.superapp.negotiatorbot.botclient.model.TelegramDocumentType

enum class DocumentType {
    /**
     * Is used by default if not classified
     */
    DEFAULT,

    /**
     * Enterprise
     */
    LABOR_CONTRACT_EMPLOYER,
    REAL_ESTATE_LEASE_CONTRACT_LANDLORD,
    SALES_CONTRACT_SELLER,
    REAL_ESTATE_SALES_CONTRACT_SELLER,
    REAL_ESTATE_SALES_CONTRACT_CUSTOMER,
    SERVICE_CONTRACT_CONTRACTOR,
    LICENSE_CONTRACT_LICENSOR,
    LICENSE_CONTRACT_LICENSEE,

    /**
     * Individuals
     */
    LABOR_CONTRACT_EMPLOYEE,

    /**
     * Both enterprise and individuals
     */
    REAL_ESTATE_LEASE_CONTRACT_TENANT,
    SALES_CONTRACT_CUSTOMER,
    SERVICE_CONTRACT_CUSTOMER;

    fun toTelegramType(): TelegramDocumentType = when (this) {
        SALES_CONTRACT_CUSTOMER, SALES_CONTRACT_SELLER -> TelegramDocumentType.SALES_CONTRACT
        SERVICE_CONTRACT_CUSTOMER, SERVICE_CONTRACT_CONTRACTOR -> TelegramDocumentType.SERVICE_CONTRACT
        else -> throw UnsupportedOperationException()
    }


}
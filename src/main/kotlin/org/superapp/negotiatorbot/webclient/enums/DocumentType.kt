package org.superapp.negotiatorbot.webclient.enums

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
    SERVICE_CONTRACT_CUSTOMER,


}
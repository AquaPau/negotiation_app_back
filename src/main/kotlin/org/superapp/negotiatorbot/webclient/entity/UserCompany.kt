package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enum.CompanyRegion

@Entity
@Table(name = "user_companies")
data class UserCompany(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val user: User,

    val customUserGeneratedName: String,

    val inn: Long,

    val ogrn: Long,

    @Enumerated(value = EnumType.STRING)
    val residence: CompanyRegion,

    val shortName: String,

    val address: String,

    val fullName: String
)

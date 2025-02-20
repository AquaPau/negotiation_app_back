package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.enum.CompanyRegion


@Entity
@Table(name = "counterparties")
data class UserCounterparty(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = User::class)
    val user: User,

    @Column
    val companyId: Long,

    @Column
    val customUserGeneratedName: String,

    @Column
    var inn: String? = null,

    @Column
    var ogrn: String? = null,

    @Enumerated(value = EnumType.STRING)
    @Column
    val residence: CompanyRegion,

    @Column
    var address: String? = null,

    @Column
    var managerTitle: String? = null,

    @Column
    var managerName: String? = null,

    @Column
    var fullName: String? = null,

    @Column
    var opportunities: String? = null
)

fun UserCounterparty.toDto(): CompanyProfileDto = CompanyProfileDto(
    id = this.id!!,
    customUserGeneratedName = this.customUserGeneratedName,
    userId = this.user.id!!,
    inn = this.inn.toString(),
    ogrn = this.ogrn.toString(),
    fullName = this.fullName,
    residence = this.residence,
    opportunities = this.opportunities
)
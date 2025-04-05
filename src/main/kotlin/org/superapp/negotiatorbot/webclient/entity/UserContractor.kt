package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.enums.CompanyRegion


@Entity
@Table(name = "contractors")
data class UserContractor(
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
    var opportunities: String? = null,

    @Column(nullable = true, unique = true)
    var assistantDbId: Long? = null
)

fun UserContractor.toDto(): CompanyProfileDto = CompanyProfileDto(
    id = this.id!!,
    customUserGeneratedName = this.customUserGeneratedName,
    userId = this.user.id!!,
    inn = this.inn.toString(),
    ogrn = this.ogrn.toString(),
    fullName = this.fullName,
    residence = this.residence,
    opportunities = this.opportunities
)
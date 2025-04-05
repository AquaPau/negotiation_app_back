package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.dto.company.CompanyProfileDto
import org.superapp.negotiatorbot.webclient.enums.CompanyRegion

@Entity
@Table(name = "user_companies")
class UserCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    var user: User? = null

    @Column
    var customUserGeneratedName: String = ""

    @Column
    var inn: String? = null

    @Column
    var ogrn: String? = null

    @Enumerated(value = EnumType.STRING)
    @Column
    var residence: CompanyRegion = CompanyRegion.RU

    @Column
    var address: String? = null

    @Column
    var managerTitle: String? = null

    @Column
    var managerName: String? = null

    @Column
    var fullName: String? = null

    @Column(nullable = true, unique = true)
    var assistantDbId: Long? = null
}

fun UserCompany.toDto(): CompanyProfileDto = CompanyProfileDto(
    id = this.id!!,
    customUserGeneratedName = this.customUserGeneratedName,
    userId = this.user!!.id!!,
    inn = this.inn.toString(),
    ogrn = this.ogrn.toString(),
    fullName = this.fullName,
    managerName = this.managerName,
    managerTitle = this.managerTitle,
    residence = this.residence
)


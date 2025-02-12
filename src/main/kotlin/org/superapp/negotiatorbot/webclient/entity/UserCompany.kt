package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enum.CompanyRegion

@Entity
@Table(name = "user_companies")
data class UserCompany(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(targetEntity = User::class)
    val user: User,

    @Column
    val customUserGeneratedName: String,

    @Column
    val inn: Long,

    @Column
    val ogrn: Long,

    @Enumerated(value = EnumType.STRING)
    @Column
    val residence: CompanyRegion,

    @Column
    val shortName: String,

    @Column
    val address: String,

    @Column
    val fullName: String
)

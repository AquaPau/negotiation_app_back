package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*


@Entity
@Table(name = "server_side_file", uniqueConstraints = [UniqueConstraint(columnNames = ["name","user_id"])])
class ServerSideFile{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false)
    var extension: String? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var businessType: BusinessType? = null

    @Column(nullable = false)
    var path: String? = null

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null //todo test cascade deletion logic on user deletion case
}

enum class BusinessType{USER, PARTNER}
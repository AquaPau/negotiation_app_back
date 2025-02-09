package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*


@Entity
@Table(name = "server_side_file")
class ServerSideFile{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

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
    var user: User? = null //todo test cascade deletion logic on user side
}

enum class BusinessType{USER, PARTNER}
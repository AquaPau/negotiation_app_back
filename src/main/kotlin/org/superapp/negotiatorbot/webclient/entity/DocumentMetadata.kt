package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enum.DocumentType


@Entity
@Table(name = "document_metadata", uniqueConstraints = [UniqueConstraint(columnNames = ["name","user_id"])])
class DocumentMetadata{

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

    @Column
    @Enumerated(EnumType.STRING)
    var documentType: DocumentType? = null

    @Column(nullable = false)
    var path: String? = null

    @Column(nullable = false)
    var userId: Long? = null

   @Column
    var description: String? = null

    @Column
    var counterPartyId: Long? = null
}

enum class BusinessType{USER, PARTNER}
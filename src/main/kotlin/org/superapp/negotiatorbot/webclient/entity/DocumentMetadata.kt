package org.superapp.negotiatorbot.webclient.entity

import jakarta.persistence.*
import org.superapp.negotiatorbot.webclient.enum.DocumentType


@Entity
@Table(name = "document_metadata", uniqueConstraints = [UniqueConstraint(columnNames = ["name", "user_id"])])
class DocumentMetadata {

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

    @Column(length = 5000)
    var description: String? = null

    @Column(length = 5000)
    var risks: String? = null

    @Column
    var companyId: Long? = null

    @Column
    var contractorId: Long? = null

    fun getNameWithExtension() = "$name.$extension"
}

enum class BusinessType { USER, PARTNER }
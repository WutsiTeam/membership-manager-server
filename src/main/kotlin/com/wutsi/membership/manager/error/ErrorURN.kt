package com.wutsi.membership.manager.error

enum class ErrorURN(val urn: String) {
    COUNTRY_NOT_SUPPORTED("urn:wutsi:error:membership-manager:country-not-supported"),
    MEMBER_ALREADY_BUSINESS("urn:wutsi:error:membership-manager:member-already-business"),
    MEMBER_ALREADY_REGISTERED("urn:wutsi:error:membership-manager:member-already-registered"),
    MEMBER_SUSPENDED("urn:wutsi:error:membership-manager:member-suspended"),
    MEMBER_NOT_ACTIVE("urn:wutsi:error:membership-manager:member-not-active"),
    MEMBER_NOT_BUSINESS("urn:wutsi:error:membership-manager:member-already-business"),
    MEMBER_NOT_FOUND("urn:wutsi:error:membership-manager:member-not-found")
}

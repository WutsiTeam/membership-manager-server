package com.wutsi.membership.manager.error

enum class ErrorURN(val urn: String) {
    MEMBER_ALREADY_REGISTERED("urn:wutsi:error:membership-manager:member-already-registered"),
    MEMBER_ALREADY_SUSPENDED("urn:wutsi:error:membership-manager:member-already-suspended"),
    MEMBER_NOT_ACTIVE("urn:wutsi:error:membership-manager:member-not-active"),
    MEMBER_NOT_FOUND("urn:wutsi:error:membership-manager:member-not-found")
}

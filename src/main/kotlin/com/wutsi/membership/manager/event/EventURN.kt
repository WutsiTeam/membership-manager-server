package com.wutsi.membership.manager.event

enum class EventURN(val urn: String) {
    BUSINESS_ACCOUNT_ENABLED("urn:wutsi:event:membership-manager:business-account-enabled"),
    BUSINESS_ACCOUNT_DISABLED("urn:wutsi:event:membership-manager:business-account-disabled"),

    MEMBER_ATTRIBUTE_UPDATED("urn:wutsi:event:membership-manager:member-attribute-updated"),
    MEMBER_DELETED("urn:wutsi:event:membership-manager:member-deleted"),
    MEMBER_REGISTERED("urn:wutsi:event:membership-manager:member-registered")
}

package com.wutsi.membership.manager.event

enum class EventURN(val urn: String) {
    MEMBER_ATTRIBUTE_UPDATED("urn:wutsi:event:membership-manager:member-attribute-updated"),
    MEMBER_DELETED("urn:wutsi:event:membership-manager:member-deleted"),
    MEMBER_REGISTRATION_STARTED("urn:wutsi:event:membership-manager:member-registration-started"),
    MEMBER_REGISTERED("urn:wutsi:event:membership-manager:member-registered")
}

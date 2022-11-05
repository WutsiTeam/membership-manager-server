package com.wutsi.membership.manager.event

data class MemberEventPayload(
    private val accountId: Long = -1,
    private val phoneNumber: String = "",
    private val pin: String? = null
)

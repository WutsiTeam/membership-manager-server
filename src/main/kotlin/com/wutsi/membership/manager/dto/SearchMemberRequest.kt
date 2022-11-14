package com.wutsi.membership.manager.dto

import kotlin.Int
import kotlin.String

public data class SearchMemberRequest(
    public val phoneNumber: String? = null,
    public val limit: Int = 100,
    public val offset: Int = 0
)

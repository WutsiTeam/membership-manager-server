package com.wutsi.membership.manager.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.String

public data class RegisterMemberRequest(
    @get:NotBlank
    public val phoneNumber: String = "",
    @get:NotBlank
    public val displayName: String = "",
    public val country: String = "",
    @get:NotBlank
    @get:Size(min = 6)
    public val pin: String = ""
)

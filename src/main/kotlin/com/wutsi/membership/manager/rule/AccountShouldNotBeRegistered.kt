package com.wutsi.membership.manager.rule

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.manager.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.workflow.Rule

class AccountShouldNotBeRegistered(
    private val phoneNumber: String,
    private val membershipAccess: MembershipAccessApi
) : Rule {
    override fun check() {
        val accounts = membershipAccess.searchAccount(
            request = SearchAccountRequest(
                phoneNumber = phoneNumber
            )
        ).accounts

        if (accounts.isNotEmpty()) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.MEMBER_ALREADY_REGISTERED.urn
                )
            )
        }
    }
}

package com.wutsi.membership.manager.rule

import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.workflow.Rule

class AccountShouldBeBusinessRule(private val account: Account) : Rule {
    override fun check() {
        if (!account.business) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_BUSINESS.urn
                )
            )
        }
    }
}

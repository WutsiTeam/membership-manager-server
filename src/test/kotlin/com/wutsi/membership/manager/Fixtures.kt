package com.wutsi.membership.manager

import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.AccountSummary
import com.wutsi.membership.access.enums.AccountStatus

object Fixtures {
    fun createAccountSummary() = AccountSummary()

    fun createAccount(
        status: AccountStatus = AccountStatus.ACTIVE,
        business: Boolean = false,
        country: String = "CM"
    ) = Account(
        status = status.name,
        business = business,
        country = country
    )
}

package com.wutsi.membership.manager

import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.AccountSummary
import com.wutsi.membership.access.enums.AccountStatus

object Fixtures {
    fun createAccountSummary() = AccountSummary(
        id = -1,
        displayName = "Ray Sponsible",
        language = "en",
        cityId = 1111L,
        country = "CM",
        business = false,
        categoryId = 2222L,
        status = AccountStatus.ACTIVE.name,
        pictureUrl = "https://www.img.com/100.png"
    )

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

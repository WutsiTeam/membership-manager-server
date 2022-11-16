package com.wutsi.membership.manager

import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.access.dto.AccountSummary
import com.wutsi.membership.access.dto.Category
import com.wutsi.membership.access.dto.Place
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
        storeId: Long? = null,
        businessId: Long? = null,
        country: String = "CM"
    ) = Account(
        id = 100L,
        status = status.name,
        business = business,
        storeId = storeId,
        businessId = businessId,
        country = country,
        email = "ray.sponsible@gmail.com",
        displayName = "Ray Sponsible",
        language = "en",
        pictureUrl = "https://www.img.com/100.png",
        city = Place(
            id = 111,
            name = "Yaounde",
            longName = "Yaounde, Cameroun"
        ),
        category = Category(
            id = 555,
            title = "Ads"
        )
    )
}

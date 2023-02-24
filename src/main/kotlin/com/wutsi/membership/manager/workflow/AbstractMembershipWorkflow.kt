package com.wutsi.membership.manager.workflow

import com.wutsi.enums.AccountStatus
import com.wutsi.event.MemberEventPayload
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.Account
import com.wutsi.membership.manager.dto.Category
import com.wutsi.membership.manager.dto.Member
import com.wutsi.membership.manager.dto.Place
import com.wutsi.membership.manager.util.SecurityUtil
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.AbstractWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractMembershipWorkflow<Req, Resp>(eventStream: EventStream) :
    AbstractWorkflow<Req, Resp, MemberEventPayload>(eventStream) {
    @Autowired
    protected lateinit var membershipAccessApi: MembershipAccessApi

    protected fun getCurrentAccountId(context: WorkflowContext): Long =
        context.accountId ?: SecurityUtil.getAccountId()

    protected fun getCurrentAccount(context: WorkflowContext): Account {
        val accountId = getCurrentAccountId(context)
        return getAccount(accountId)
    }

    protected fun getAccount(accountId: Long): Account =
        membershipAccessApi.getAccount(accountId).account

    protected fun toMember(account: Account) = Member(
        id = account.id,
        name = account.name,
        displayName = account.displayName,
        pictureUrl = account.pictureUrl,
        business = account.business,
        country = account.country,
        language = account.language,
        active = account.status == AccountStatus.ACTIVE.name,
        superUser = account.superUser,
        facebookId = account.facebookId,
        twitterId = account.twitterId,
        youtubeId = account.youtubeId,
        instagramId = account.instagramId,
        whatsapp = account.whatsapp,
        phoneNumber = account.phone.number,
        timezoneId = account.timezoneId,
        storeId = account.storeId,
        businessId = account.businessId,
        biography = account.biography,
        street = account.street,
        website = account.website,
        email = account.email,
        category = account.category
            ?.let {
                Category(
                    id = it.id,
                    title = it.title,
                )
            },
        city = account.city
            ?.let {
                Place(
                    id = it.id,
                    name = it.name,
                    longName = it.longName,
                    longitude = it.longitude,
                    latitude = it.latitude,
                    type = it.type,
                    timezoneId = it.timezoneId,
                )
            },
    )
}

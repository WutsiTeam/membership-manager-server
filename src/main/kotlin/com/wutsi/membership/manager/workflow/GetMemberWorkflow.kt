package com.wutsi.membership.manager.workflow

import com.wutsi.membership.access.enums.AccountStatus
import com.wutsi.membership.manager.dto.Category
import com.wutsi.membership.manager.dto.GetMemberResponse
import com.wutsi.membership.manager.dto.Member
import com.wutsi.membership.manager.dto.Place
import com.wutsi.membership.manager.event.MemberEventPayload
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.NotFoundException
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.workflow.RuleSet
import com.wutsi.workflow.WorkflowContext
import com.wutsi.workflow.error.ErrorURN
import org.springframework.stereotype.Service

@Service
class GetMemberWorkflow(eventStream: EventStream) :
    AbstractMembershipWorkflow<Void?, GetMemberResponse>(eventStream) {
    override fun getEventType(): String? = null

    override fun toEventPayload(
        request: Void?,
        response: GetMemberResponse,
        context: WorkflowContext
    ): MemberEventPayload? = null

    override fun getValidationRules(request: Void?, context: WorkflowContext) = RuleSet.NONE

    override fun doExecute(request: Void?, context: WorkflowContext): GetMemberResponse {
        try {
            val account = getCurrentAccount(context)
            return GetMemberResponse(
                member = Member(
                    id = account.id,
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
                                title = it.title
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
                                timezoneId = it.timezoneId
                            )
                        }
                )
            )
        } catch (ex: NotFoundException) {
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.MEMBER_NOT_FOUND.urn,
                    data = mapOf(
                        "account-id" to getCurrentAccount(context)
                    )
                )
            )
        }
    }
}

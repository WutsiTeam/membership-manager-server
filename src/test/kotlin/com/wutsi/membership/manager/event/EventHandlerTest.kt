package com.wutsi.membership.manager.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.event.EventURN
import com.wutsi.event.StoreEventPayload
import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.manager.Fixtures
import com.wutsi.platform.core.stream.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class EventHandlerTest {
    @MockBean
    private lateinit var membershipAccessApi: MembershipAccessApi

    @Autowired
    private lateinit var handler: EventHandler

    @Autowired
    private lateinit var mapper: ObjectMapper

    private val payload = StoreEventPayload(
        accountId = 11L,
        storeId = 22L
    )

    @Test
    fun onStoreEnabled() {
        // THEN
        val account = Fixtures.createAccount(storeId = null)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        // WHEN
        val event = Event(
            type = EventURN.STORE_ENABLED.urn,
            payload = mapper.writeValueAsString(payload)
        )
        handler.handleEvent(event)

        // THEN
        verify(membershipAccessApi).updateAccountAttribute(
            id = payload.accountId,
            UpdateAccountAttributeRequest(
                name = "store-id",
                value = payload.storeId.toString()
            )
        )
    }

    @Test
    fun onStoreSuspended() {
        // THEN
        val account = Fixtures.createAccount(storeId = payload.storeId)
        doReturn(GetAccountResponse(account)).whenever(membershipAccessApi).getAccount(any())

        // WHEN
        val event = Event(
            type = EventURN.STORE_SUSPENDED.urn,
            payload = mapper.writeValueAsString(payload)
        )
        handler.handleEvent(event)

        // THEN
        verify(membershipAccessApi).updateAccountAttribute(
            id = payload.accountId,
            UpdateAccountAttributeRequest(
                name = "store-id",
                value = null
            )
        )
    }
}

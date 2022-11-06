package com.wutsi.membership.manager.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.event.EventURN
import com.wutsi.membership.manager.event.MemberEventPayload
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateMemberAttributeControllerTest : AbstractSecuredControllerTest() {
    @LocalServerPort
    val port: Int = 0

    @Test
    fun update() {
        // WHEN
        val request = UpdateMemberAttributeRequest(
            name = "display-name",
            value = "Yo Man"
        )
        val response = rest.postForEntity(url(), request, Any::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val req = argumentCaptor<UpdateAccountAttributeRequest>()
        verify(membershipAccess).updateAccountAttribute(eq(ACCOUNT_ID), req.capture())
        assertEquals(request.name, req.firstValue.name)
        assertEquals(request.value, req.firstValue.value)

        verify(eventStream).publish(
            EventURN.MEMBER_ATTRIBUTE_UPDATED.urn,
            MemberEventPayload(accountId = ACCOUNT_ID)
        )
    }

    private fun url() = "http://localhost:$port/v1/members/attributes"
}

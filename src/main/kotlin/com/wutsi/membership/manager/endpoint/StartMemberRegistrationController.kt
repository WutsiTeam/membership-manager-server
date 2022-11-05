package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.StartMemberRegistrationDelegate
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.String

@RestController
public class StartMemberRegistrationController(
    public val `delegate`: StartMemberRegistrationDelegate
) {
    @GetMapping("/v1/members/start-registration")
    public fun invoke(@RequestParam(name = "phone-number", required = false) phoneNumber: String) {
        delegate.invoke(phoneNumber)
    }
}

package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.GetMemberDelegate
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class GetMemberController(
    public val `delegate`: GetMemberDelegate
) {
    @GetMapping("/v1/members")
    public fun invoke(): GetMemberResponse = delegate.invoke()
}

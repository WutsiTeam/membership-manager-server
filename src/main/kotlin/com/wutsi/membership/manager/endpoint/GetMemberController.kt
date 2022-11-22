package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.GetMemberDelegate
import com.wutsi.membership.manager.dto.GetMemberResponse
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetMemberController(
    public val `delegate`: GetMemberDelegate
) {
    @GetMapping("/v1/members")
    public fun invoke(@RequestParam(name = "id", required = false) id: Long? = null): GetMemberResponse =
        delegate.invoke(id)
}

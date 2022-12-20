package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.RegisterMemberDelegate
import com.wutsi.membership.manager.dto.RegisterMemberRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class RegisterMemberController(
    public val `delegate`: RegisterMemberDelegate,
) {
    @PostMapping("/v1/members")
    public fun invoke(
        @Valid @RequestBody
        request: RegisterMemberRequest,
    ) {
        delegate.invoke(request)
    }
}

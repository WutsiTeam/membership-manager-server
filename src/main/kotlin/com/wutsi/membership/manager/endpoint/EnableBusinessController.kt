package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.EnableBusinessDelegate
import com.wutsi.membership.manager.dto.EnableBusinessRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class EnableBusinessController(
    public val `delegate`: EnableBusinessDelegate
) {
    @PostMapping("/v1/members/business")
    public fun invoke(
        @Valid @RequestBody
        request: EnableBusinessRequest
    ) {
        delegate.invoke(request)
    }
}

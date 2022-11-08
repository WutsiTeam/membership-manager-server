package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.DisableBusinessDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class DisableBusinessController(
    public val `delegate`: DisableBusinessDelegate
) {
    @DeleteMapping("/v1/members/business")
    public fun invoke() {
        delegate.invoke()
    }
}

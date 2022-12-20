package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.DeactivateBusinessDelegate
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class DeactivateBusinessController(
    public val `delegate`: DeactivateBusinessDelegate,
) {
    @DeleteMapping("/v1/members/business")
    public fun invoke() {
        delegate.invoke()
    }
}

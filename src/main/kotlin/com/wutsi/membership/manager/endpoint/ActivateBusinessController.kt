package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.manager.`delegate`.ActivateBusinessDelegate
import com.wutsi.membership.manager.dto.ActivateBusinessRequest
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class ActivateBusinessController(
    public val `delegate`: ActivateBusinessDelegate,
) {
    @PostMapping("/v1/members/business")
    public fun invoke(
        @Valid @RequestBody
        request: ActivateBusinessRequest,
    ) {
        delegate.invoke(request)
    }
}

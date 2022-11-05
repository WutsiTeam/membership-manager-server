package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.dto.RegisterMemberRequest
import com.wutsi.membership.manager.workflow.RegisterMemberWorkflow
import org.springframework.stereotype.Service

@Service
public class RegisterMemberDelegate(private val workflow: RegisterMemberWorkflow) {
    public fun invoke(request: RegisterMemberRequest) {
        workflow.execute(request)
    }
}

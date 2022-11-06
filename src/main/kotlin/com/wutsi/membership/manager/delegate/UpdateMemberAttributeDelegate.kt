package com.wutsi.membership.manager.`delegate`

import com.wutsi.membership.manager.dto.UpdateMemberAttributeRequest
import com.wutsi.membership.manager.workflow.UpdateMemberAttributeWorkflow
import org.springframework.stereotype.Service

@Service
class UpdateMemberAttributeDelegate(private val workflow: UpdateMemberAttributeWorkflow) {
    fun invoke(request: UpdateMemberAttributeRequest) {
        workflow.execute(request)
    }
}

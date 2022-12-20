package com.wutsi.membership.manager.delegate

import com.wutsi.membership.manager.dto.SearchPlaceRequest
import com.wutsi.membership.manager.dto.SearchPlaceResponse
import com.wutsi.membership.manager.workflow.SearchPlaceWorkflow
import com.wutsi.workflow.WorkflowContext
import org.springframework.stereotype.Service

@Service
public class SearchPlaceDelegate(private val workflow: SearchPlaceWorkflow) {
    public fun invoke(request: SearchPlaceRequest): SearchPlaceResponse =
        workflow.execute(request, WorkflowContext())
}

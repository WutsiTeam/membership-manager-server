package com.wutsi.membership.manager.event

import com.wutsi.event.EventURN
import com.wutsi.platform.core.stream.Event
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val store: StoreEventHandler,
    private val business: BusinessEventHandler,
) {
    @EventListener
    fun handleEvent(event: Event) {
        when (event.type) {
            EventURN.STORE_ACTIVATED.urn -> store.onStoreActivated(event)
            EventURN.STORE_DEACTIVATED.urn -> store.onStoreDeactivated(event)
            EventURN.BUSINESS_CREATED.urn -> business.onBusinessCreated(event)
            else -> {}
        }
    }
}

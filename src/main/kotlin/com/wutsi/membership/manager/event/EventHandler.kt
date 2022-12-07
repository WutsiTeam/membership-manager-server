package com.wutsi.membership.manager.event

import com.wutsi.event.EventURN
import com.wutsi.platform.core.stream.Event
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class EventHandler(
    private val marketplaceEventHandler: MarketplaceEventHandler
) {
    @EventListener
    fun handleEvent(event: Event) {
        when (event.type) {
            EventURN.STORE_ACTIVATED.urn -> marketplaceEventHandler.onStoreActivated(event)
            EventURN.STORE_DEACTIVATED.urn -> marketplaceEventHandler.onStoreDeactivated(event)
            else -> {}
        }
    }
}

package com.wutsi.membership.manager.event

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
            com.wutsi.marketplace.manager.event.EventURN.STORE_ENABLED.urn -> marketplaceEventHandler.onStoreEnabled(
                event
            )
            com.wutsi.marketplace.manager.event.EventURN.STORE_SUSPENDED.urn -> marketplaceEventHandler.onStoreSuspended(
                event
            )
            else -> {}
        }
    }
}

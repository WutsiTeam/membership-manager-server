package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.manager.LanguageClientHttpRequestInterceptor
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.RestTemplate

abstract class AbstractControllerTest {
    @MockBean
    protected lateinit var membershipAccess: MembershipAccessApi

    @MockBean
    protected lateinit var eventStream: EventStream

    protected var language = "fr"

    protected var rest = RestTemplate()

    @BeforeEach
    fun setUp() {
        rest = RestTemplate()
        rest.interceptors.add(LanguageClientHttpRequestInterceptor(language))
    }
}

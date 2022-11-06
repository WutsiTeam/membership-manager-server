package com.wutsi.membership.manager.endpoint

import com.wutsi.membership.access.MembershipAccessApi
import com.wutsi.membership.manager.LanguageClientHttpRequestInterceptor
import com.wutsi.platform.core.stream.EventStream
import feign.FeignException
import feign.Request
import feign.RequestTemplate
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.web.client.RestTemplate
import java.nio.charset.Charset

abstract class AbstractControllerTest {
    @MockBean
    protected lateinit var membershipAccess: MembershipAccessApi

    @MockBean
    protected lateinit var eventStream: EventStream

    protected var language = "fr"

    protected var rest = RestTemplate()

    @BeforeEach
    open fun setUp() {
        rest = RestTemplate()
        rest.interceptors.add(LanguageClientHttpRequestInterceptor(language))
    }

    protected fun createFeignNotException(
        errorCode: String
    ) = FeignException.NotFound(
        "",
        Request.create(
            Request.HttpMethod.POST,
            "https://www.google.ca",
            emptyMap(),
            "".toByteArray(),
            Charset.defaultCharset(),
            RequestTemplate()
        ),
        """
            {
                "error":{
                    "code": "$errorCode"
                }
            }
        """.trimIndent().toByteArray(),
        emptyMap()
    )
}

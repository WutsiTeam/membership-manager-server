package com.wutsi.membership.manager.service

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
class FeignAcceptLanguageInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        val language = LocaleContextHolder.getLocale().language
        template.header(HttpHeaders.ACCEPT_LANGUAGE, language)
    }
}

package com.wutsi.membership.manager.config

import com.wutsi.regulation.LanguageRegulations
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LanguageRegulationConfiguration {
    @Bean
    fun languageRegulation() = LanguageRegulations()
}

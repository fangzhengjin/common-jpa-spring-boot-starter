package com.github.fangzhengjin.common.autoconfigure.jpa

import com.github.fangzhengjin.common.component.jpa.JpaHelper
import com.github.fangzhengjin.common.component.jpa.auditing.DefaultAuditorAware
import com.github.fangzhengjin.common.component.jpa.auditing.SpringSecurityAuditorAware
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import javax.persistence.EntityManager

/**
 * @version V1.0
 * @title: JpaHelperAutoConfiguration
 * @package com.github.fangzhengjin.common.autoconfigure.jpa
 * @description: 当项目使用JPA时，如果Spring容器中不存在JpaHelper则自动创建
 * @author fangzhengjin
 * @date 2019/1/28 16:59
 */
@Configuration
@EnableJpaAuditing
@ConditionalOnClass(EntityManager::class)
class JpaHelperAutoConfiguration {

    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Bean
    @ConditionalOnMissingBean(JpaHelper::class)
    fun jpaHelper(entityManager: EntityManager): JpaHelper {
        return JpaHelper(entityManager)
    }

    @Bean
    @ConditionalOnMissingClass("org.springframework.security.core.context.SecurityContextHolder")
    @ConditionalOnMissingBean(AuditorAware::class)
    fun defaultAuditorAware(): AuditorAware<String> {
        return DefaultAuditorAware()
    }

    @Bean
    @ConditionalOnClass(SecurityContextHolder::class)
    @ConditionalOnMissingBean(AuditorAware::class)
    fun springSecurityAuditorAware(): AuditorAware<String> {
        return SpringSecurityAuditorAware()
    }
}
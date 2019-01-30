package com.github.fangzhengjin.common.autoconfigure.jpa

import com.github.fangzhengjin.common.component.jpa.JpaHelper
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
@ConditionalOnClass(EntityManager::class)
class JpaHelperAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(JpaHelper::class)
    fun createJpaHelper(entityManager: EntityManager): JpaHelper {
        return JpaHelper(entityManager)
    }
}
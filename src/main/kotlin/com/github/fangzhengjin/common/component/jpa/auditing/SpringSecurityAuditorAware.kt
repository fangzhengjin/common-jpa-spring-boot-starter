package com.github.fangzhengjin.common.component.jpa.auditing

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import java.util.*

/**
 * @version V1.0
 * @title: SpringSecurityAuditorAware
 * @package com.github.fangzhengjin.common.component.jpa.auditing
 * @description:
 * @author fangzhengjin
 * @date 2019-2-24 18:39
 */
class SpringSecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        val principal = Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map {
                    when (it) {
                        is String -> it.toString()
                        is User -> it.username
                        else -> "system"
                    }
                }
        return if (principal.isPresent) {
            principal
        } else {
            Optional.of("system")
        }
    }
}
package com.github.fangzhengjin.common.component.jpa.auditing

import org.springframework.data.domain.AuditorAware
import java.util.*

/**
 * @version V1.0
 * @title: DefaultAuditorAware
 * @package com.github.fangzhengjin.common.component.jpa.auditing
 * @description:
 * @author fangzhengjin
 * @date 2019-2-24 18:39
 */
class DefaultAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of("system")
    }
}
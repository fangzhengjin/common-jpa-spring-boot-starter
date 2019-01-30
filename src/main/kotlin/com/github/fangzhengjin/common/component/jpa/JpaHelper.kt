@file:Suppress("DEPRECATION")

package com.github.fangzhengjin.common.component.jpa

import com.github.fangzhengjin.common.component.jpa.transformer.AliasToEntityCaseFormatMapResultTransformer
import org.hibernate.query.internal.NativeQueryImpl
import org.hibernate.transform.Transformers
import org.intellij.lang.annotations.Language
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import javax.persistence.EntityManager

/**
 * @author fangzhengjin
 * date 2018-7-13 16:55
 * @version V1.0
 * title: JpaHelper
 * package com.github.fangzhengjin.common.component.jpa
 * description:
 */
class JpaHelper(
        private val entityManager: EntityManager
) {

    /**
     *  本地查询 使用页码分页 每行结果封装至Map 结果集key处理为小驼峰
     */
    fun nativeQueryAndPage(
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            querySql: String,
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            countSql: String,
            page: Int,
            size: Int
    ): Page<Any> {
        return nativeQueryAndPage(querySql, countSql, getPageable(page, size))
    }

    /**
     * 本地查询 使用Pageable分页 每行结果封装至Map 结果集key处理为小驼峰
     */
    fun nativeQueryAndPage(
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            querySql: String,
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            countSql: String,
            pageable: Pageable
    ): Page<Any> {
        val query = entityManager.createNativeQuery(querySql)
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize
        query.unwrap(NativeQueryImpl::class.java).setResultTransformer(AliasToEntityCaseFormatMapResultTransformer.INSTANCE)

        val countQuery = nativeQuerySingleResult(countSql)
        return PageImpl<Any>(query.resultList, pageable, countQuery.toString().toLong())
    }

    fun nativeQuery(
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            querySql: String
    ): MutableList<Any?>? {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java).setResultTransformer(AliasToEntityCaseFormatMapResultTransformer.INSTANCE)
        return query.resultList
    }

    /**
     * 本地查询 每行结果封装至Map key全部大写 不作处理
     */
    fun nativeQueryNoTransformer(
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            querySql: String
    ): MutableList<Any?>? {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
        return query.resultList
    }

    /**
     * 本地查询 返回原生List
     */
    fun nativeQueryList(
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            querySql: String
    ): MutableList<Any?>? =
            entityManager.createNativeQuery(querySql).resultList


    /**
     * 本地查询 只返回第一个结果集
     */
    fun nativeQuerySingleResult(
            @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
            querySql: String
    ): Any? =
            entityManager.createNativeQuery(querySql).singleResult

    companion object {
        const val DEFAULT_JPA_NATIVE_QUERY_LANGUAGE = "Oracle"

        @JvmStatic
        fun getPageable(page: Int, size: Int): PageRequest {
            return PageRequest.of(if (page <= 1) 0 else page - 1, size)
        }
    }
}

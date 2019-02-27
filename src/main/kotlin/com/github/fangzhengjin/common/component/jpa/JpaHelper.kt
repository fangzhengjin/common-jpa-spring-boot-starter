@file:Suppress("DEPRECATION")

package com.github.fangzhengjin.common.component.jpa

import com.github.fangzhengjin.common.component.jpa.transformer.AliasToLittleCamelCaseMapResultTransformer
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
        entityManagerParam: EntityManager
) {
    init {
        entityManager = entityManagerParam
    }

    private companion object {
        const val DEFAULT_JPA_NATIVE_QUERY_LANGUAGE = "Oracle"

        private var entityManager: EntityManager? = null

        @JvmStatic
        fun getPageable(page: Int, size: Int): PageRequest {
            return PageRequest.of(if (page <= 1) 0 else page - 1, size)
        }

        /**
         *  本地查询 使用页码分页 每行结果封装至Map 结果集key处理为小驼峰
         */
        @JvmStatic
        fun nativeQueryTransformLittleCamelCaseMapResultToPage(
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                querySql: String,
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                countSql: String,
                page: Int,
                size: Int
        ): Page<Any> {
            return nativeQueryTransformLittleCamelCaseMapResultToPage(querySql, countSql, getPageable(page, size))
        }

        /**
         * 本地查询 使用Pageable分页 每行结果封装至Map 结果集key处理为小驼峰
         */
        @JvmStatic
        fun nativeQueryTransformLittleCamelCaseMapResultToPage(
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                querySql: String,
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                countSql: String,
                pageable: Pageable
        ): Page<Any> {
            val query = entityManager!!.createNativeQuery(querySql)
            query.firstResult = pageable.offset.toInt()
            query.maxResults = pageable.pageSize
            query.unwrap(NativeQueryImpl::class.java).setResultTransformer(AliasToLittleCamelCaseMapResultTransformer.INSTANCE)

            val countQuery = nativeQueryNoTransformerSingleObjectResult(countSql)
            return PageImpl<Any>(query.resultList, pageable, countQuery.toString().toLong())
        }

        /**
         * 本地查询 每行结果封装至Map 结果集key处理为小驼峰
         */
        @JvmStatic
        fun nativeQueryTransformLittleCamelCaseMapResult(
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                querySql: String
        ): MutableList<Any?> {
            val query = entityManager!!.createNativeQuery(querySql)
            query.unwrap(NativeQueryImpl::class.java).setResultTransformer(AliasToLittleCamelCaseMapResultTransformer.INSTANCE)
            return query.resultList
        }

        /**
         * 本地查询 每行结果封装至Map 原生样式 不作处理
         */
        @JvmStatic
        fun nativeQueryNoTransformerMapResultToList(
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                querySql: String
        ): MutableList<Any?> {
            val query = entityManager!!.createNativeQuery(querySql)
            query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
            return query.resultList
        }

        /**
         * 本地查询 每行结果封装至指定对象
         */
        @JvmStatic
        fun <T : Any> nativeQueryTransformerCustomizeEntityResultToList(
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                querySql: String,
                target: Class<T>
        ): MutableList<T> {
            val query = entityManager!!.createNativeQuery(querySql)
            query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.aliasToBean(target))
            return query.resultList as MutableList<T>
        }

        /**
         * 本地查询 返回原生List
         */
        @JvmStatic
        fun nativeQueryNoTransformerObjectToList(
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                querySql: String
        ): MutableList<Any?> =
                entityManager!!.createNativeQuery(querySql).resultList


        /**
         * 本地查询 只返回第一个结果集
         */
        @JvmStatic
        fun nativeQueryNoTransformerSingleObjectResult(
                @Language(DEFAULT_JPA_NATIVE_QUERY_LANGUAGE)
                querySql: String
        ): Any? =
                entityManager!!.createNativeQuery(querySql).singleResult
    }
}

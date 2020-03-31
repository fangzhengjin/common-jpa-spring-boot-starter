package com.github.fangzhengjin.common.component.jpa

import com.github.fangzhengjin.common.component.jpa.transformer.AliasToLittleCamelCaseMapResultTransformer
import org.hibernate.query.internal.NativeQueryImpl
import org.hibernate.transform.Transformers
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


object JpaHelper {
    private lateinit var entityManager: EntityManager
    fun init(entityManager: EntityManager): JpaHelper {
        this.entityManager = entityManager
        return this
    }

    @JvmStatic
    fun getPageable(page: Int, size: Int): PageRequest {
        return PageRequest.of(if (page <= 1) 0 else page - 1, size)
    }

    @JvmStatic
    fun getEntityManager(): EntityManager = entityManager

    /**
     *  本地查询 使用页码分页 每行结果封装至Map 结果集key处理为小驼峰
     */
    @JvmOverloads
    @JvmStatic
    fun nativeQueryTransformLittleCamelCaseMapResultToPage(
            querySql: String,
            countSql: String? = null,
            page: Int,
            size: Int
    ): Page<Any> {
        return nativeQueryTransformLittleCamelCaseMapResultToPage(querySql, countSql, getPageable(page, size))
    }

    /**
     * 本地查询 使用Pageable分页 每行结果封装至Map 结果集key处理为小驼峰
     */
    @JvmOverloads
    @JvmStatic
    fun nativeQueryTransformLittleCamelCaseMapResultToPage(
            querySql: String,
            countSql: String? = null,
            pageable: Pageable
    ): Page<Any> {
        val query = entityManager.createNativeQuery(querySql)
        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize
        query.unwrap(NativeQueryImpl::class.java)
                .setResultTransformer(AliasToLittleCamelCaseMapResultTransformer.INSTANCE)
        val countQuery = nativeQueryNoTransformerSingleObjectResult(countSql
                ?: "SELECT count(0) from ($querySql) as page")
        return PageImpl<Any>(query.resultList, pageable, countQuery.toString().toLong())
    }

    /**
     * 本地查询 每行结果封装至Map 结果集key处理为小驼峰
     */
    @JvmStatic
    fun nativeQueryTransformLittleCamelCaseMapResult(
            querySql: String
    ): MutableList<Any?> {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java)
                .setResultTransformer(AliasToLittleCamelCaseMapResultTransformer.INSTANCE)
        return query.resultList
    }

    /**
     * 本地查询 每行结果封装至Map 原生样式 不作处理
     */
    @JvmStatic
    fun nativeQueryNoTransformerMapResultToList(
            querySql: String
    ): MutableList<Any?> {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
        return query.resultList
    }

    /**
     * 本地查询 每行结果封装至指定对象
     */
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <T : Any> nativeQueryTransformerCustomizeEntityResultToList(
            querySql: String,
            target: Class<T>
    ): MutableList<T> {
        val query = entityManager.createNativeQuery(querySql)
        query.unwrap(NativeQueryImpl::class.java).setResultTransformer(Transformers.aliasToBean(target))
        @Suppress("UNCHECKED_CAST")
        return query.resultList as MutableList<T>
    }

    /**
     * 本地查询 返回原生List
     */
    @JvmStatic
    fun nativeQueryNoTransformerObjectToList(
            querySql: String
    ): MutableList<Any?> =
            entityManager.createNativeQuery(querySql).resultList


    /**
     * 本地查询 只返回第一个结果集
     */
    @JvmStatic
    fun nativeQueryNoTransformerSingleObjectResult(
            querySql: String
    ): Any? =
            entityManager.createNativeQuery(querySql).singleResult

    /**
     * 执行本地SQL
     */
    @JvmStatic
    fun executeNativeQuery(
            executeSql: String
    ) = entityManager.createNativeQuery(executeSql).executeUpdate()
}

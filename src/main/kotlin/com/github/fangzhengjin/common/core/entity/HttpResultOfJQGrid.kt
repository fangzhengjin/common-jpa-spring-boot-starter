package com.github.fangzhengjin.common.core.entity

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.domain.Page

@ApiModel("JQGrid分页数据集")
class JQGrid<T>(
        @ApiModelProperty("查询内容")
        val rows: List<T>,
        @ApiModelProperty("总记录数")
        val records: Long,
        @ApiModelProperty("总页数")
        val total: Int,
        @ApiModelProperty("当前页码")
        val page: Int
)

fun <T> HttpResult.Companion.jqGrid(page: Page<T>): JQGrid<T> {
    return JQGrid(
            rows = page.content,
            records = page.totalElements,
            total = page.totalPages,
            page = page.number + 1
    )
}

@JvmOverloads
fun <T> HttpResult.Companion.jqGrid(page: Page<T>, clazz: Class<T>, includeFields: Set<String> = setOf(), excludeFields: Set<String> = setOf()): String {
    val propertyPreFilter = SimplePropertyPreFilter(clazz)
    propertyPreFilter.includes.addAll(includeFields)
    propertyPreFilter.excludes.addAll(excludeFields)
    return JSON.toJSONString(jqGrid(page), propertyPreFilter)
}

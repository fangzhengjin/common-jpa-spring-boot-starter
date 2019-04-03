package com.github.fangzhengjin.common.core.entity

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus

@ApiModel("分页数据集")
class PageResult<T>(
        @ApiModelProperty("查询内容")
        val content: List<T>,
        @ApiModelProperty("总记录数")
        val totalElements: Long,
        @ApiModelProperty("总页数")
        val totalPages: Int,
        @ApiModelProperty("当前页码")
        val currentPage: Int
)


@JvmOverloads
fun <T> Result.Companion.page(page: Page<T>, message: String? = null): HttpResult<PageResult<T>> {
    return HttpResult(
            code = HttpStatus.OK.value(),
            message = message ?: HttpStatus.OK.reasonPhrase,
            body = PageResult(
                    content = page.content,
                    totalElements = page.totalElements,
                    totalPages = page.totalPages,
                    currentPage = page.number + 1
            )
    )
}
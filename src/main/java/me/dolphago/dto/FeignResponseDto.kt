package me.dolphago.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FeignResponseDto(
    val login: String,
    val html_url: String
)

package me.dolphago.dto

data class ResponseDto(
    val onlyFollowers: ResponseItem, // 나를 팔로우 하는 사람들(난 팔로우 안하고)
    val onlyFollowings: ResponseItem, // 나만 팔로우 하고 있는 사람들(그 사람들은 날 팔로우 안하고)
    val neighbors: ResponseItem // 서로 이웃인 사람들
) {
    companion object {
        fun create(): ResponseDto {
            return ResponseDto(ResponseItem(), ResponseItem(), ResponseItem())
        }
    }
}

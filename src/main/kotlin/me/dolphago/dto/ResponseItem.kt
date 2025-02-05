package me.dolphago.dto

data class ResponseItem(
    var count: Int = 0,
    var list: List<MemberDto> = ArrayList()
) {
    fun set(list: List<MemberDto>) {
        this.list = list
        this.count = list.size
    }
}

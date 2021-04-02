package me.dolphago.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class ResponseDto {
    private ResponseItem onlyFollowers; // 나를 팔로우 하는 사람들(난 팔로우 안하고)
    private ResponseItem onlyFollowings;  // 나만 팔로우 하고 있는 사람들(그 사람들은 날 팔로우 안하고)
    private ResponseItem neighbors; // 서로 이웃인 사람들

    public static ResponseDto create(){
        return ResponseDto.builder()
                .onlyFollowers(new ResponseItem())
                .onlyFollowings(new ResponseItem())
                .neighbors(new ResponseItem())
                .build();
    }
}

package me.dolphago.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ResponseItem {
    private int count;
    private List<MemberDto> list = new ArrayList<>();

    public void create(List<MemberDto> list){
        this.list = list;
        this.count = list.size();
    }
}

package me.dolphago.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BaseEntityTest {
    @DisplayName("test")
    @Test
    public void test(){
        Followers followers=Followers.builder()
                .build();
    }
}
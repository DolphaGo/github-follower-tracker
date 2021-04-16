package me.dolphago.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NeighborTest {

    @DisplayName("오버라이딩 테스트")
    @Test
    public void test() {
        Neighbor neighbor = Neighbor.builder()
                                    .url("aa")
                                    .githubLogin("bb")
                                    .build();
        assertEquals("aa", neighbor.getUrl());
        assertEquals("bb", neighbor.getGithubLogin());
    }

}
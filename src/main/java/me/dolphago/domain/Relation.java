package me.dolphago.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Relation {
    NEW_FOLLOWER,
    NEW_FOLLOWING,
    NEW_UNFOLLOWER,
    NEW_UNFOLLOWING,
    NEIGHBOR;
}

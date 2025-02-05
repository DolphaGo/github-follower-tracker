<template>
  <div class="container">
    <span><h1>{{ data.message }} </h1><h5><a href="https://github.com/DolphaGo/github-follow-unfollow">by DolphaGo</a></h5></span>
    <br />

    <div class="input-group mb-3">
      <input type="text" v-model="handle" class="form-control" placeholder="Github handle" aria-label="Github handle" aria-describedby="button-addon2">
      <button class="btn btn-outline-secondary" type="button" id="button-addon2" @click="getData(handle)">Search</button>
    </div>

    <table class="table">
      <thead v-show="show">
      <tr>
        <th scope="row">
          서로 이웃 : {{info.neighbors.list?.length}} 명
        </th>
        <th scope="row">
          나는 안하지만 나를 팔로우 하는 사람들 : {{info.onlyFollowers.list?.length}} 명
        </th>
        <th scope="row">
          상대는 안하지만, 나만 팔로우 하고 있는 사람들 : {{info.onlyFollowings.list?.length}} 명
        </th>
      </tr>
      </thead>
      <tbody>
      <td>
        <tr v-for ="neighbor in info.neighbors.list">
          <a :href="neighbor.url">{{ neighbor.githubLogin }}</a>
        </tr>
      </td>
      <td>
        <tr v-for ="follower in info.onlyFollowers.list">
          <a :href="follower.url">{{ follower.githubLogin }}</a>
        </tr>
      </td>
      <td>
        <tr v-for ="following in info.onlyFollowings.list">
          <a :href="following.url">{{ following.githubLogin }}</a>
        </tr>
      </td>
      </tbody>
    </table>

  </div>
</template>

<script setup lang="ts">
import {reactive, ref} from "vue";
import axios from "axios";

const data = {
  "message": "Github Follower Checker"
}

const show = ref(false);
const info = reactive<Info>({
  neighbors: { list: [] },
  onlyFollowers: { list: [] },
  onlyFollowings: { list: [] }
})

const handle = ref("");

function getData(handle: string) {
  show.value = false;
  axios.get("http://localhost:8080/check?handle=" + handle)
      .then(value => {
        info.neighbors.list = value.data.neighbors.list;
        info.onlyFollowers.list = value.data.onlyFollowers.list;
        info.onlyFollowings.list = value.data.onlyFollowings.list;
      })
  show.value = true;
}
</script>

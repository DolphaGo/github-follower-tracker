<template>
  <div>
    {{ data.message }}
    <br />
    <input v-model="handle">
    <button @click="getData(handle)">Get</button>

    <table>
      <thead>
      <tr>
        <th scope="row">
          서로 이웃
        </th>
        <th scope="row">
          나는 안하지만 나를 팔로우 하는 사람들
        </th>
        <th scope="row">
          상대는 안하지만, 나만 팔로우 하고 있는 사람들
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

<script lang="ts">
import {defineComponent, reactive} from "vue";
import axios from "axios";

export default defineComponent({
  name: "Home",
  data() {
    return {
      handle: ""
    }
  },
  setup() {
    const data = {
      "message": "Github Follower 체크"
    }

    const info = reactive<Info>({
      neighbors: [],
      onlyFollowers: [],
      onlyFollowings: []
    })

    function getData(handle: string) {
      console.log("누굴 요청했는지?", handle)

      axios.get("http://localhost:8080/check?handle=" + handle)
          .then(value => {
            info.neighbors = value.data.neighbors
            info.onlyFollowers = value.data.onlyFollowers
            info.onlyFollowings = value.data.onlyFollowings
          })
    }

    return {data, info, getData};
  }
});
</script>

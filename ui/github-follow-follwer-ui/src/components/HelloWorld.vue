<template>
  <div>
    {{ data.message }}
    <br />
    <input v-model="handle">
    <button @click="getData(handle)">Get</button>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import axios from "axios";

export default defineComponent({
  name: "Home",
  data(){
    return{
      handle : ""
    }
  },
  setup() {
    const data = {
      "message": "Github Follower 체크"
    }

    function getData(handle : string) {
      console.log("누굴 요청했는지?", handle)

      axios.get("http://localhost:8080/check?handle=" + handle)
          .then(value => {
            console.log("value", value);
          })
    }

    return {data, getData};
  }
});
</script>

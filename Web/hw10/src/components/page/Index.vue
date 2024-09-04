<template>
  <div class="posts">
    <Post v-for="post in reversedPosts" :key="post.id" :users="users" :post="post" :comments="comments"/>
  </div>
</template>

<script>
import Post from "@/components/page/Post";

export default {
  name: "Index",
  props: ["posts", "users", "comments"],
  components: {Post},
  computed: {
    reversedPosts: function () {
      return Object.values(this.posts).reverse()
    }
  }
}
</script>

<style scoped>

</style>
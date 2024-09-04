<template>
  <article>
    <div class="title"><a href="#" @click.prevent="viewPost()">{{ post.title }}</a></div>
    <div class="information">By {{ findUserById(post.userId).login }}</div>
    <div class="body">{{ post.text }}</div>
    <div class="footer">
      <div class="left">
        <img src="@/assets/img/comments_16x16.png" title="Vote Up" alt="Vote Up"/>
        <span class="positive-score">+173</span>
        <img src="@/assets/img/votedown.png" title="Vote Down" alt="Vote Down"/>
      </div>
      <div class="right">
      <img src="@/assets/img/comments_16x16.png" title="Comments" alt="Comments"/>
        <a href="#"> {{ Object.values(this.comments).filter(c => c.postId === this.post.id).length }} </a>
    </div>
    </div>
  </article>
</template>

<script>
export default {
  name: "Post",
  props: ["post", "users", "comments"],
  methods: {
    findUserById: function (id) {
      return this.users[id]
    },
    viewPost: function () {
      this.$root.$emit("onViewPost", this.post);
    }
  }

}
</script>

<style scoped>

</style>
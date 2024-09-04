<template>
  <article>
    <div class="title">{{ findUserById(comment.userId).login }}</div>
    <div class="body"> {{ comment.text }} </div>
  </article>
</template>

<script>
export default {
  name: "Comment",
  props: ["comment", "users"],
  methods: {
    findUserById: function (id) {
      return this.users[id]
    }
  }
}
</script>

<style scoped>

</style>
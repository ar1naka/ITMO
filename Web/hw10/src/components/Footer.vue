<template>
    <footer>
        Number of posts: {{posts_num}}
        Number of users: {{users_num}}
        <a href="/">Codeforces</a> &copy; 2022 by Mike Mirzayanov
    </footer>
</template>

<script>
export default {
    name: "Footer",
    props: ["posts_num", "users_num"]
}
</script>

<style scoped>

</style>
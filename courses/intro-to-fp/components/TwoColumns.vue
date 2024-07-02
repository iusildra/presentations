<script setup lang="ts">
</script>

<template>
  <div flex="~" style="justify-content: space-evenly; align-items: center">
    <div flex="~" style="align-items: center">
      <slot name="col1"/>
    </div>
    <div flex="~" style="align-items: center">
      <slot name="col2"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useNav } from "@slidev/client";
import type { TocItem } from "@slidev/types";
import { computed } from "vue";

const { currentPage, tocTree } = useNav();

const navigationBuilder = (tocItems: TocItem[]) => {
  const loop = (items: TocItem[], parents: TocItem[] = []): TocItem[] => {
    const active = items.find((_) => _.active);
    if (active) return parents;

    const activeParent = items.find((_) => _.activeParent);
    if (!activeParent) return parents;
    parents.push(activeParent);
    return loop(activeParent.children, parents);
  };

  const branch = loop(tocItems);
  return branch.map((_) => _.title).join(" / ");
};

const tree = computed(() => navigationBuilder(tocTree.value));
</script>

<template>
  <header class="absolute top-0 left-0 right-0 p-2" v-if="currentPage > 1">
    {{ tree }}
  </header>
</template>

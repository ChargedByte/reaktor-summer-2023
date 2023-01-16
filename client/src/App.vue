<script setup lang="ts">
import { RouterView } from "vue-router"
</script>

<template>
  <v-app id="projectbirdnest">
    <v-app-bar app color="primary">
      <v-toolbar-title>Project Birdnest</v-toolbar-title>
    </v-app-bar>

    <RouterView v-slot="{ Component }">
      <template v-if="Component">
        <Transition mode="out-in">
          <KeepAlive>
            <Suspense>
              <!-- main content -->
              <component :is="Component"></component>
              <!-- loading state -->
              <template #fallback>
                <v-main>
                  <v-progress-linear indeterminate color="secondary"></v-progress-linear>
                </v-main>
              </template>
            </Suspense>
          </KeepAlive>
        </Transition>
      </template>
    </RouterView>
  </v-app>
</template>

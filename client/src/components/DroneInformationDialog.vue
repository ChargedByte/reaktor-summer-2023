<script setup lang="ts">
import { ref } from "vue"
import { useDisplay } from "vuetify"

import { mdiClose } from "@mdi/js"

import { useDroneStore } from "@/stores/drone"

const store = useDroneStore()

const active = ref(false)

const openDialog = () => {
  active.value = true
}

defineExpose({ openDialog })

const { mdAndDown } = useDisplay()
</script>

<template>
  <v-dialog v-model="active">
    <v-container>
      <v-row justify="space-around">
        <v-card :width="mdAndDown ? '100%' : '75%'">
          <v-toolbar color="primary">
            <v-toolbar-title>Drone Information</v-toolbar-title>

            <v-btn v-on:click="active = false" :icon="mdiClose"></v-btn>
          </v-toolbar>

          <v-card-text>
            <v-list>
              <v-list-subheader>Device Information</v-list-subheader>
              <v-list-item title="Serial Number" :subtitle="store.drone.serialNumber"></v-list-item>
              <v-list-item title="Manufacturer" :subtitle="store.drone.manufacturer"></v-list-item>
              <v-list-item title="Model" :subtitle="store.drone.model"></v-list-item>
              <v-list-subheader>System Information</v-list-subheader>
              <v-divider></v-divider>
              <v-list-item title="Mac Address" :subtitle="store.drone.mac"></v-list-item>
              <v-list-item title="IPv4 Address" :subtitle="store.drone.ipv4"></v-list-item>
              <v-list-item title="IPv6 Address" :subtitle="store.drone.ipv6"></v-list-item>
              <v-list-item title="Firmware Version" :subtitle="store.drone.firmware"></v-list-item>
              <v-list-subheader>Current Position</v-list-subheader>
              <v-divider></v-divider>
              <v-list-item title="Position X" :subtitle="store.drone.positionX"></v-list-item>
              <v-list-item title="Position Y" :subtitle="store.drone.positionY"></v-list-item>
              <v-list-item title="Altitude" :subtitle="store.drone.altitude"></v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-row>
    </v-container>
  </v-dialog>
</template>

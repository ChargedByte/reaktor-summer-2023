<script setup lang="ts">
import { onBeforeUnmount, reactive, ref, toRaw } from "vue"

import { mdiOpenInNew } from "@mdi/js"

import type { Violation } from "@/model/Violation"

import type { Requester, RequestStreamCallbacks } from "@/utils/rsocket"
import { makeConnector, requestStream } from "@/utils/rsocket"
import { parseViolationMessage } from "@/model/ViolationMessage"
import { MessageAction } from "@/model/MessageAction"
import DroneInformationDialog from "@/components/DroneInformationDialog.vue"
import { useDroneStore } from "@/stores/drone"

// Define data & state
const store = useDroneStore()

const violations = reactive(new Map<string, Violation>())

const droneInformationDialog = ref<InstanceType<typeof DroneInformationDialog>>()

const headers = [
  {
    title: "Pilot Id",
    sortable: false,
    key: "pilot.pilotId",
  },
  {
    title: "Drone",
    sortable: false,
    key: "drone.serialNumber",
  },
  {
    title: "Full Name",
    key: "fullName",
  },
  {
    title: "Email Address",
    key: "pilot.email",
  },
  {
    title: "Phone Number",
    key: "pilot.phoneNumber",
  },
  {
    title: "Closest Recoded Distance (m)",
    key: "closestDistanceToNest",
  },
  {
    title: "Timestamp (UTC)",
    key: "recordedAt",
  },
]

const browserLocale = navigator.languages.at(0) ?? "en-US"

// Drone Information Dialog
const openDroneDialog = (serialNumber: string) => {
  const drone = toRaw(violations.get(serialNumber)?.drone)
  if (drone) {
    store.$patch({ drone })
    droneInformationDialog.value?.openDialog()
  }
}

// Load RSocket data
const connector = makeConnector(import.meta.env.VITE_API_RSOCKET)

const rsocket = await connector.connect()

const requestCount = 15
const maxPayloadCount = undefined

let requester: Requester | undefined = undefined

const callbacks: RequestStreamCallbacks = {
  onNext: (req, payload) => {
    if (!requester) requester = req

    const message = parseViolationMessage(`${payload.data}`)

    if (message != null) {
      if (message.action === MessageAction.Set) {
        const key = message.content.drone.serialNumber
        violations.set(key, message.content)
      } else {
        violations.delete(message.content)
      }
    }
  },
}

onBeforeUnmount(() => {
  if (requester) requester.cancel()
})

requestStream(rsocket, "api.v1.violations.events", requestCount, maxPayloadCount, callbacks)
</script>

<template>
  <v-main>
    <v-container fluid>
      <DroneInformationDialog ref="droneInformationDialog"></DroneInformationDialog>

      <v-data-table :headers="headers" :items="Array.from(violations.values())" class="elevation-1">
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Recorded Violations</v-toolbar-title>
          </v-toolbar>
        </template>

        <template v-slot:item.pilot.pilotId="{ item }">
          <span v-if="item.value.pilot">{{ item.value.pilot.pilotId }}</span>
          <span class="font-italic" v-else>Not Found</span>
        </template>

        <template v-slot:item.drone.serialNumber="{ item }">
          <v-btn v-on:click="openDroneDialog(item.value.drone.serialNumber)" color="secondary">
            <span><v-icon :icon="mdiOpenInNew"></v-icon> {{ item.value.drone.serialNumber }}</span>
          </v-btn>
        </template>

        <template v-slot:item.fullName="{ item }">
          <span v-if="item.value.pilot">{{ item.value.pilot.firstName }} {{ item.value.pilot.lastName }}</span>
          <span class="font-italic" v-else>Not Found</span>
        </template>

        <template v-slot:item.pilot.email="{ item }">
          <span v-if="item.value.pilot">{{ item.value.pilot.email }}</span>
          <span class="font-italic" v-else>Not Found</span>
        </template>

        <template v-slot:item.pilot.phoneNumber="{ item }">
          <span v-if="item.value.pilot">{{ item.value.pilot.phoneNumber }}</span>
          <span class="font-italic" v-else>Not Found</span>
        </template>

        <template v-slot:item.closestDistanceToNest="{ item }">
          {{ (item.value.closestDistanceToNest / 1000).toFixed(2) }}
        </template>

        <template v-slot:item.recordedAt="{ item }">
          {{ item.value.recordedAt.toLocaleString(browserLocale) }}
        </template>
      </v-data-table>
    </v-container>
  </v-main>
</template>

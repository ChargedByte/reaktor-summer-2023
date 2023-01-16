<script setup lang="ts">
import { onBeforeUnmount, reactive } from "vue"

import type { Violation } from "@/model/Violation"

import { makeConnector, Requester, requestStream, RequestStreamCallbacks } from "@/utils/rsocket"
import { parseViolationMessage } from "@/model/ViolationMessage"
import { MessageAction } from "@/model/MessageAction"

// Define data & state
const violations = reactive(new Map<string, Violation>())

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
    key: "full_name",
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
    title: "Timestamp",
    key: "recordedAt",
  },
]

// Load RSocket data
const connector = makeConnector("ws://localhost:8080/rsocket")

const rsocket = await connector.connect()

const requestCount = 15
const maxPayloadCount = undefined

let requester: Requester | undefined = undefined

const callbacks: RequestStreamCallbacks = {
  onNext: (rqstr, payload) => {
    if (!requester) requester = rqstr

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
      <v-data-table :headers="headers" :items="Array.from(violations.values())" class="elevation-1">
        <template v-slot:top>
          <v-toolbar flat>
            <v-toolbar-title>Recorded Violations</v-toolbar-title>
          </v-toolbar>
        </template>
        <template v-slot:item.full_name="{ item }"
          >{{ item.value.pilot.firstName }} {{ item.value.pilot.lastName }}
        </template>
        <template v-slot:item.recordedAt="{ item }">
          {{ item.value.recordedAt.toLocaleString() }}
        </template>
        <template v-slot:item.closestDistanceToNest="{ item }">
          {{ (item.value.closestDistanceToNest / 1000).toFixed(2) }}
        </template>
      </v-data-table>
    </v-container>
  </v-main>
</template>

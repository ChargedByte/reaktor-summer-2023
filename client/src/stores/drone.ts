import { defineStore } from "pinia"

import type { Drone } from "@/model/Drone"

export interface DroneStoreState {
  drone: Drone
}

export const useDroneStore = defineStore("drone", {
  state: (): DroneStoreState => ({
    drone: {
      serialNumber: "N/A",
      model: "N/A",
      manufacturer: "N/A",
      mac: "N/A",
      ipv4: "N/A",
      ipv6: "N/A",
      firmware: "N/A",
      positionY: 0,
      positionX: 0,
      altitude: 0,
    },
  }),
})

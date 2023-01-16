import * as t from "io-ts"

import { isLeft } from "fp-ts/Either"
import { PathReporter } from "io-ts/PathReporter"

export const DroneC = t.type({
  serialNumber: t.string,
  model: t.string,
  manufacturer: t.string,
  mac: t.string,
  ipv4: t.string,
  ipv6: t.string,
  firmware: t.string,
  positionY: t.number,
  positionX: t.number,
  altitude: t.number,
})

export type Drone = t.TypeOf<typeof DroneC>

export const parseDrone = (input: string): Drone | null => {
  const result = DroneC.decode(JSON.parse(input))
  if (isLeft(result)) {
    console.error(PathReporter.report(result))
    return null
  }
  return result.right
}

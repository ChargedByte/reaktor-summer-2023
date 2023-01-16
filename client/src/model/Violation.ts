import * as t from "io-ts"

import { isLeft } from "fp-ts/Either"
import { DateFromISOString } from "io-ts-types"
import { PathReporter } from "io-ts/PathReporter"

import { DroneC } from "@/model/Drone"
import { PilotC } from "@/model/Pilot"

export const ViolationC = t.intersection([
  t.type({
    drone: DroneC,
    recordedAt: DateFromISOString,
    closestDistanceToNest: t.number,
  }),
  t.partial({
    pilot: PilotC,
  }),
])

export type Violation = t.TypeOf<typeof ViolationC>

export const parseViolation = (input: string): Violation | null => {
  const result = ViolationC.decode(JSON.parse(input))
  if (isLeft(result)) {
    console.error(PathReporter.report(result))
    return null
  }
  return result.right
}

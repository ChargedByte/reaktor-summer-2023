import * as t from "io-ts"

import { isLeft } from "fp-ts/Either"
import { DateFromISOString } from "io-ts-types"
import { PathReporter } from "io-ts/PathReporter"

export const PilotC = t.type({
  pilotId: t.string,
  firstName: t.string,
  lastName: t.string,
  phoneNumber: t.string,
  createdAt: DateFromISOString,
  email: t.string,
})

export type Pilot = t.TypeOf<typeof PilotC>

export const parsePilot = (input: string): Pilot | null => {
  const result = PilotC.decode(JSON.parse(input))
  if (isLeft(result)) {
    console.log(PathReporter.report(result))
    return null
  }
  return result.right
}

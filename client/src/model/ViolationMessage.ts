import * as t from "io-ts"

import { isLeft } from "fp-ts/Either"
import { PathReporter } from "io-ts/PathReporter"

import { MessageAction } from "@/model/MessageAction"
import { ViolationC } from "@/model/Violation"

export const SetViolationMessageC = t.type({
  action: t.literal(MessageAction.Set),
  content: ViolationC,
})

export type SetViolationMessage = t.TypeOf<typeof SetViolationMessageC>

export const ExpiredViolationMessageC = t.type({
  action: t.literal(MessageAction.Expired),
  content: t.string,
})

export type ExpiredViolationMessage = t.TypeOf<typeof ExpiredViolationMessageC>

export const ViolationMessageC = t.union([SetViolationMessageC, ExpiredViolationMessageC])

export type ViolationMessage = t.TypeOf<typeof ViolationMessageC>

export const parseViolationMessage = (input: string): ViolationMessage | null => {
  const result = ViolationMessageC.decode(JSON.parse(input))
  if (isLeft(result)) {
    console.error(PathReporter.report(result))
    return null
  }
  return result.right
}

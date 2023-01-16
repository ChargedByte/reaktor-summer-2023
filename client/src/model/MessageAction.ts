import * as t from "io-ts"
import { isLeft } from "fp-ts/Either"

export enum MessageAction {
  Set = "SET",
  Expired = "EXPIRED",
}

export const MessageActionC = t.keyof({
  [MessageAction.Set]: null,
  [MessageAction.Expired]: null,
})

export const parseMessageAction = (input: unknown): MessageAction => {
  const result = MessageActionC.decode(input)
  if (isLeft(result)) throw result.left
  return result.right
}

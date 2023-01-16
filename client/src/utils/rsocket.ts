import type { Cancellable, OnExtensionSubscriber, Payload, Requestable, RSocket } from "rsocket-core"
import { RSocketConnector } from "rsocket-core"
import { WebsocketClientTransport } from "rsocket-websocket-client"
import { encodeCompositeMetadata, encodeRoute, WellKnownMimeType } from "rsocket-composite-metadata"
import { Buffer } from "buffer"

const dataMimeType = WellKnownMimeType.APPLICATION_JSON.string
const metadataMimeType = WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA.string

export function makeConnector(url: string): RSocketConnector {
  return new RSocketConnector({
    setup: { dataMimeType, metadataMimeType },
    transport: new WebsocketClientTransport({ url }),
  })
}

export type Requester = Requestable & Cancellable & OnExtensionSubscriber

export interface RequestStreamCallbacks {
  onError?: (requester: Requester, error: Error) => void
  onNext?: (requester: Requester, payload: Payload, isComplete: boolean) => void
  onComplete?: (requester: Requester) => void
  onExtension?: (
    requester: Requester,
    extendedType: number,
    content: Buffer | null | undefined,
    canBeIgnored: boolean
  ) => void
}

export async function requestStream(
  rsocket: RSocket,
  route?: string,
  requestCount: number = 5,
  maxPayloadCount?: number,
  callbacks?: RequestStreamCallbacks
) {
  let compositeMetaData: Buffer | undefined = undefined
  if (route) {
    const encodedRoute = encodeRoute(route)

    const map = new Map<WellKnownMimeType, Buffer>()
    map.set(WellKnownMimeType.MESSAGE_RSOCKET_ROUTING, encodedRoute)
    compositeMetaData = encodeCompositeMetadata(map)
  }

  await new Promise((resolve, reject) => {
    let payloadsReceived = 0
    const requester = rsocket.requestStream(
      {
        data: Buffer.from("request-stream"),
        metadata: compositeMetaData,
      },
      requestCount,
      {
        onError: (error) => {
          if (callbacks?.onError) callbacks.onError(requester, error)
          reject(error)
        },
        onNext: (payload, isComplete) => {
          payloadsReceived++

          if (callbacks?.onNext) callbacks.onNext(requester, payload, isComplete)

          if (maxPayloadCount) {
            if (payloadsReceived < maxPayloadCount) {
              requester.request(requestCount)
            } else if (payloadsReceived >= maxPayloadCount) {
              requester.cancel()
              setTimeout(() => resolve(null))
            }
          } else {
            requester.request(requestCount)
          }

          if (isComplete) resolve(null)
        },
        onComplete: () => {
          if (callbacks?.onComplete) callbacks.onComplete(requester)
          resolve(null)
        },
        onExtension: (extendedType, content, canBeIgnored) => {
          if (callbacks?.onExtension) callbacks.onExtension(requester, extendedType, content, canBeIgnored)
        },
      }
    )
  })
}

import { createApp } from "vue"
import { createPinia } from "pinia"

import App from "./App.vue"
import router from "./router"

import "vuetify/styles"
import { createVuetify } from "vuetify"
import * as components from "vuetify/components"
import * as directives from "vuetify/directives"
import { VDataTable } from "vuetify/labs/VDataTable"
import { aliases, mdi } from "vuetify/iconsets/mdi-svg"

import { Buffer } from "buffer"

globalThis.Buffer = Buffer

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(
  createVuetify({
    components: {
      VDataTable,
      ...components,
    },
    directives,
    icons: {
      defaultSet: "mdi",
      aliases,
      sets: {
        mdi,
      },
    },
  })
)

app.mount("#app")

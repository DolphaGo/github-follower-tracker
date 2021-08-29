import {createApp} from "vue";
import App from "./App.vue";
import { BootstrapVue } from 'bootstrap-vue'

async function bootstrapApplication(mainDiv: string): Promise<void> {
  const app = createApp(App)
  app.use(BootstrapVue)
  app.mount(mainDiv)
}

bootstrapApplication('#app').then().catch(err => console.log(err))

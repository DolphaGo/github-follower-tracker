import {createApp} from "vue";
import App from "./App.vue";

async function bootstrapApplication(mainDiv: string): Promise<void> {
  const app = createApp(App)
  app.mount(mainDiv)
}

bootstrapApplication('#app').then().catch(err => console.log(err))

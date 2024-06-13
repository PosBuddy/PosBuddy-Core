import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {provideServiceWorker} from '@angular/service-worker';
import {LoadingService} from "./service/loading.service";
import {LoadingInterceptor} from "./loading.interceptor";

export const appConfig: ApplicationConfig = {
  // TODO : check service worker - > !isDevMode()
  providers: [
    provideRouter(routes),
    importProvidersFrom(
      HttpClientModule,
      LoadingService
    ),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: LoadingInterceptor,
      multi: true,
    },
    provideServiceWorker('ngsw-worker.js', {
        enabled: false,
        registrationStrategy: 'registerWhenStable:30000'
      }
    )
  ]
};

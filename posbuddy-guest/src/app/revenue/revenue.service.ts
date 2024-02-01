import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {IdentityService} from "../identity/identity.service";
import {Revenue} from "../model/Revenue";
import {Observable} from "rxjs";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};


@Injectable({
  providedIn: 'root'
})
export class RevenueService {


  private revenueUrl = '/api/v1/revenue/';  // URL to web api

  constructor(private http: HttpClient) {

  }

  getRevenue(posBuddyId: String) {
    if (posBuddyId !== IdentityService.UNKNOWN_ID) {
      return this.http.get<Array<Revenue>>(this.revenueUrl + posBuddyId, httpOptions)
    } else {
      console.log("no valid posBuddyId - cannot request Server");
      return new Observable<Array<Revenue>>()
    }
  }

}

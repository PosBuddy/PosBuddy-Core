import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};

export const UNKNOWN_ID = "-";

export interface idData {
  "posBuddyId": string,
  "surName": string,
  "lastName": string,
  "youthProtectionAct": boolean,
  "balance": number,
  "revenueList": [
    {
      "itemText": string,
      "amount": number,
      "value": number,
      "action": string,
      "timeOfAction": string
    }
  ]
}

@Injectable({
  providedIn: 'root'
})
export class RevenueService {

  private identityDataUrl = '/api/v1/identityData/';  // URL to web api

  constructor(private http: HttpClient) {

  }

  getIdentityData(posBuddyId: String) {
    return this.http.get<idData>(this.identityDataUrl + posBuddyId, httpOptions)
  }

}

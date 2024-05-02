import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";


const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
    'Cache-Control': 'no-cache, no-store, must-revalidate, post-check=0, pre-check=0',
    'Pragma': 'no-cache',
    'Expires': '0'
  })
};

export const UNKNOWN_ID = "-";

export interface staticIdData {
  "posBuddyId": string,
  "balance": number,
  "syncTimeStamp": string,
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


export class StaticIdService {

  constructor(private http: HttpClient) {

  }

  getIdentity(posBuddyId: string): Observable<staticIdData> {
    return this.http.get<any>("/asset/" + posBuddyId, httpOptions)
  }


}

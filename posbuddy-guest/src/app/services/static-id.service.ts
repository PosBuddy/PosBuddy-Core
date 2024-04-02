import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";


const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};

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

import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};

export interface identity {
  "posBuddyId": "string",
  "surName": "string",
  "lastName": "string",
  "youthProtectionAct": boolean,
  "attribute1": "string",
  "attribute2": "string",
  "attribute3": "string",
  "balance": number
}

export interface revenue {
  "itemText": string,
  "amount": number,
  "value": number,
  "action": string,
  "timeOfAction": string
}
export interface item {
  "id": string,
  "itemtext": number,
  "value": number,
  "action": string,
  "timeOfAction": string
}

@Injectable({
  providedIn: 'root'
})

export class paymentService {
  private baseUrl = "api/v1/";

  constructor(private http: HttpClient) {

  }

  doPayout(posBuddyId: string) {
    return this.http.post<any>(this.baseUrl + "payout/" + posBuddyId, httpOptions)
  }

  doDeallocate(posBuddyId: string) {
    return this.http.post<any>(this.baseUrl + "deAllocate/" + posBuddyId, httpOptions)
  }

  getIdentity(posBuddyId: string): Observable<identity> {
    return this.http.get<any>(this.baseUrl + "identity/" + posBuddyId, httpOptions)
  }

  getRevenue(posBuddyId: string): Observable<revenue[]> {
    return this.http.get<any>(this.baseUrl + "revenue/" + posBuddyId, httpOptions)
  }

  getItem(station: string): Observable<revenue[]> {
    return this.http.get<any>(this.baseUrl + "items/" + station, httpOptions)
  }


  addDeposit(posBuddyId: string, value: number) {
    return this.http.post<any>(this.baseUrl + "deposit/" + posBuddyId + "?value=" + value, httpOptions)
  }

}

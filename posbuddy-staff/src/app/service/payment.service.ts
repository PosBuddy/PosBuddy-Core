import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};

interface identity {
  "posBuddyId": "string",
  "surName": "string",
  "lastName": "string",
  "youthProtectionAct": boolean,
  "attribute1": "string",
  "attribute2": "string",
  "attribute3": "string",
  "balance": number
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

  getIdentity(posBuddyId: string): Observable<identity> {
    return this.http.get<any>(this.baseUrl + "identity/" + posBuddyId, httpOptions)
  }

  addDeposit(posBuddyId: string, value: number) {
    return this.http.post<any>(this.baseUrl + "deposit/" + posBuddyId + "?value=" + value, httpOptions)
  }

}

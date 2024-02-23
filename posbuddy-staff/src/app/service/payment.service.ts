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
  private paymentUrl = 'api/v1/payment/';
  private depositUrl = 'api/v1/deposit/';
  private identityUrl = 'api/v1/xidentity/';


  constructor(private http: HttpClient) {

  }


  doPayout(posBuddyId: string, value: number) {
    return this.http.post<any>(this.paymentUrl + posBuddyId + "?value=" + value, httpOptions)
  }

  getIdentity(posBuddyId: string): Observable<identity> {
    return this.http.get<any>(this.identityUrl + posBuddyId, httpOptions)
  }

  addDeposit(posBuddyId: string, value: number) {
    return this.http.post<any>(this.depositUrl + posBuddyId + "?value=" + value, httpOptions)
  }

}

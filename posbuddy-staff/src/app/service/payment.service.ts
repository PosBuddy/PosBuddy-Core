import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};


@Injectable({
  providedIn: 'root'
})
export class paymentService {


  private paymentUrl = 'api/v1/payment/';
  private depositUrl = 'api/v1/deposit/';

  constructor(private http: HttpClient) {

  }


  doPayment(posBuddyId: string, value: number) {
    return this.http.post<any>(this.paymentUrl + posBuddyId + "?value=" + value, httpOptions)
  }

  addDeposit(posBuddyId: string, value: number) {
    return this.http.post<any>(this.depositUrl + posBuddyId + "?value=" + value, httpOptions)
  }

}

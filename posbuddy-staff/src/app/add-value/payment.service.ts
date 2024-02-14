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


  private serverUrl = 'secure/api/v1/payment/';  // URL to web api

  constructor(private http: HttpClient) {

  }


  addPayment(posBuddyId: string, value: number) {
    return this.http.post<any>(this.serverUrl + posBuddyId + "?value=" + value, httpOptions)
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

}

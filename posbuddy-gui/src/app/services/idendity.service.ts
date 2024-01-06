import {Injectable} from '@angular/core';
import {Identity} from "../model/Identity";
import {HttpClient, HttpHeaders} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
    'access-control-allow-origin': 'http://localhost:8080'
  })
};


@Injectable({
  providedIn: 'root'
})
export class IdendityService {

  private identityUrl = 'http://localhost:8080/api/v1/identity/b567dc6f-96e2-4c51-a8ca-83de6763afe3';  // URL to web api

  constructor(private http: HttpClient) {

  }

  getIentityById(posBuddyId: string) {
    return this.http.get<Identity>(this.identityUrl, httpOptions)
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }





}

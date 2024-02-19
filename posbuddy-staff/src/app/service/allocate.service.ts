import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};


interface AllocatePosBuddyIdRequest {
  allocatePosBuddyIdRequest: {
    surname: string | undefined;
    lastname: string | undefined;
    birthday: string | undefined;
    attribute1: string | undefined;
    attribute2: string | undefined;
    attribute3: string | undefined;
    balance: number | undefined;
  }
}

@Injectable({
  providedIn: 'root'
})
export class AllocateService {

  private serverUrl = 'api/v1/allocate/';  // URL to web api

  constructor(private http: HttpClient) {

  }


  allocatePosBuddyId(posBuddyId: string, allocatePosBuddyIdRequest: AllocatePosBuddyIdRequest) {
    return this
      .http
      .post<AllocatePosBuddyIdRequest>(
        this.serverUrl + posBuddyId,
        allocatePosBuddyIdRequest,
        httpOptions
      )
  }
}

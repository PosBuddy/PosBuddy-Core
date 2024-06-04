import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};


export interface AllocatePosBuddyIdRequest {
  surname?: string,
  lastname?: string,
  birthday?: string,
  attribute1?: string,
  attribute2?: string,
  attribute3?: string,
  balance?: number
}

@Injectable({
  providedIn: 'root'
})
export class AllocateService {

  private apiBase = 'api/v1/';  // URL to web api

  constructor(private http: HttpClient) {

  }


  allocateVolatilePosBuddyId(posBuddyId: string, allocatePosBuddyIdRequest: AllocatePosBuddyIdRequest) {
    return this
      .http
      .post<AllocatePosBuddyIdRequest>(
        this.apiBase + "allocateVolatile/" + posBuddyId,
        allocatePosBuddyIdRequest,
        httpOptions
      )
  }

  allocateStaticPosBuddyId(posBuddyId: string, allocatePosBuddyIdRequest: AllocatePosBuddyIdRequest) {
    return this
      .http
      .post<AllocatePosBuddyIdRequest>(
        this.apiBase + "allocateStatic/" + posBuddyId,
        allocatePosBuddyIdRequest,
        httpOptions
      )
  }

  allocateOneTimeId(allocatePosBuddyIdRequest: AllocatePosBuddyIdRequest) {
    return this
      .http
      .post<AllocatePosBuddyIdRequest>(
        this.apiBase + "allocateOneTimeId",
        allocatePosBuddyIdRequest,
        httpOptions
      )
  }
}

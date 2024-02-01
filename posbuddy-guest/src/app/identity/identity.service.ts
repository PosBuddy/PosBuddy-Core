import {Injectable} from '@angular/core';
import {Identity} from "../model/Identity";
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
export class IdentityService {

  public static UNKNOWN_ID = "Unbekannt";

  private identityUrl = '/api/v1/identity/';  // URL to web api

  private localPosBuddyId: string = IdentityService.UNKNOWN_ID

  constructor(private http: HttpClient) {

  }

  getIentityById(posBuddyId: string) {
    return this.http.get<Identity>(this.identityUrl + posBuddyId, httpOptions)
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

  isLocalIdentityValid(): boolean {
    let localPosBuddyId = localStorage.getItem('posBuddyId')
    let localPosBuddyTimestamp = localStorage.getItem('posBuddyIdTimestamp')
    if (localPosBuddyId == null) {
      this.localPosBuddyId = IdentityService.UNKNOWN_ID
      return false;
    }
    if (localPosBuddyTimestamp != null) {
      if (Date.now() - Number(localPosBuddyTimestamp) > (24 * 60 * 60 * 1000)) {
        console.log("posBuddyId is to old");
        this.localPosBuddyId = "UNKNOWN";
        return false;
      }
    }
    this.localPosBuddyId = localPosBuddyId;
    return true;
  }

  setLocalIdentity(posBuddyId: string) {
    localStorage.setItem('posBuddyId', posBuddyId)
    localStorage.setItem('posBuddyIdTimestamp', Date.now().toString())
  }

  getLocalidentity(): string {
    return this.localPosBuddyId;
  }

}

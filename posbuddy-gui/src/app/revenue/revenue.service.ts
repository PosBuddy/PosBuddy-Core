import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {IdentityService} from "../identity/identity.service";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
    'rejectUnauthorized': 'false',
  })
};


@Injectable({
  providedIn: 'root'
})
export class RevenueService {


  private identityUrl = '/api/v1/revenue/';  // URL to web api
  private localPosBuddyId: string = IdentityService.UNKNOWN_ID

  constructor(private http: HttpClient, private identityService: IdentityService) {

  }

  getRevenue() {
    if (this.localPosBuddyId == IdentityService.UNKNOWN_ID
      && this.identityService.isLocalIdentityValid()) {

    } else {
      console.log("no valid posBuddyId - cannot ")
    }
  }

}

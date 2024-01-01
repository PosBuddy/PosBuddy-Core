import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Identity} from "../model/Identity";

@Injectable({
    providedIn: 'root'
})
export class IdendityService {

    private headers = new Headers({'Content-Type': 'application/json'});
    private identityUrl = 'https://posbuddy.fritz.box:8443/api/v1/identity/b567dc6f-96e2-4c51-a8ca-83de6763afe3';  // URL to web api

    constructor(private http: HttpClient) {
    }

    getIentityById(): Promise<Identity> {
        return this.http.get(this.identityUrl)
            .toPromise()
            .then(response => {
                return response.json().data as Hero[];
            })
            .catch(this.handleError);
    }

    private handleError(error: any): Promise<any> {
        console.error('An error occurred', error); // for demo purposes only
        return Promise.reject(error.message || error);
    }

}

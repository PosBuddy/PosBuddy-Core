import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DownloadReportService {

  constructor(private http: HttpClient) {
  }

  downloadReport(url: string): Observable<HttpResponse<Blob>> {
    return this.http.get(url, {
        responseType: 'blob',
        observe: 'response'
      }
    );
  }

}

import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {DownloadReportService} from "../service/download-report.service";
import {NgbAlert} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-report-form',
  standalone: true,
  imports: [
    FormsModule,
    NgbAlert
  ],
  templateUrl: './report-form.component.html',
  styleUrl: './report-form.component.css'
})
export class ReportFormComponent {

  confirmDownload: boolean = false;

  constructor(private downloadReportService: DownloadReportService) {

  }

  protected createReport(reportApiEndpoint: string) {
    console.log("get " + reportApiEndpoint + " report")
    this.downloadReportService.downloadReport("api/v1/report/" + reportApiEndpoint)
      .subscribe((response) => {
          const blob = response.body;
          const contentDisposition = response.headers.get('content-disposition');
          let fileName = 'download.pdf';
          if (contentDisposition) {
            const fileNameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
            const matches = fileNameRegex.exec(contentDisposition);
            if (matches != null && matches[1]) {
              fileName = matches[1].replace(/['"]/g, '');
            }
          }
          if (blob) {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = fileName;
            a.click();
            window.URL.revokeObjectURL(url);
            this.confirmDownload = true;
          }
        }
      );
  }

  protected resetConfirm() {
    this.confirmDownload = false;
  }
}

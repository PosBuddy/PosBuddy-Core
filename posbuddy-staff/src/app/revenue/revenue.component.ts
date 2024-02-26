import {Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {paymentService, revenue} from "../service/payment.service";
import {DecimalPipe} from "@angular/common";


@Component({
  selector: 'app-revenue',
  standalone: true,
  imports: [
    NgbAlert,
    ReactiveFormsModule,
    ZXingScannerModule,
    FormsModule,
    DecimalPipe
  ],
  templateUrl: './revenue.component.html',
  styleUrl: './revenue.component.css'
})


export class RevenueComponent {
  @ViewChild('revenueOC') revenueOCTemplate: TemplateRef<any> | undefined;

  constructor(private paymentService: paymentService) {

  }

  private offcanvasService = inject(NgbOffcanvas);

  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;
  formValid: boolean = false;
  formValidText: string = "ID zur Anzeige der Umsätze scannen"
  posBuddyId: string = "-";
  balance = '0';
  revenues: Array<revenue> = [];

  scanQRCode(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'scanId'})
  }

  getRevenue() {
    this.offcanvasService.open(this.revenueOCTemplate, {ariaLabelledBy: 'revenue'})
    this.paymentService
      .getRevenue(this.posBuddyId)
      .subscribe(value => {
          this.revenues = value;
        }, err => {
          this.confirmError = true;
          this.offcanvasService.dismiss("error");
          switch (err.status) {
            case 400 : {
              this.serverResponse = "Ungültige ID";
              break
            }
            case 401 : {
              this.serverResponse = "Zugriff verweigert";
              break
            }
            case 404 : {
              this.serverResponse = "ID nicht zugeordnet";
              break
            }
            case 405 : {
              this.serverResponse = "keine Berechtigung";
              break
            }
            default : {
              this.serverResponse = "Fehlercode:" + err.status;
              break
            }
          }
        }
      )
  }

  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.formValid = true
    this.offcanvasService.dismiss("success");

    this.paymentService.getIdentity(this.posBuddyId).subscribe(
      next => {
        this.balance = "" + next.balance;
        if (this.balance.indexOf(".") > 0) {
          this.balance = this.balance.substring(0, this.balance.indexOf(".") + 2)
        }
        this.getRevenue()
      },
      err => {
        this.confirmError = true;
        switch (err.status) {
          case 400 : {
            this.serverResponse = "Ungültige ID";
            break
          }
          case 401 : {
            this.serverResponse = "Zugriff verweigert";
            break
          }
          case 404 : {
            this.serverResponse = "ID nicht zugeordnet";
            break
          }
          case 405 : {
            this.serverResponse = "keine Berechtigung";
            break
          }
          default : {
            this.serverResponse = "Fehlercode:" + err.status;
            break
          }
        }
      },
      () => console.log('HTTP request completed.')
    );
  }


  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
    this.posBuddyId = "-";
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.posBuddyId = "-";
  }


}

import {Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {NgbAlert, NgbOffcanvas, NgbPopover} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {paymentService, revenue} from "../service/payment.service";
import {CurrencyPipe, DatePipe, DecimalPipe} from "@angular/common";
import {PosBuddyConstants} from "../posBuddyConstants";

@Component({
    selector: 'app-revenue',
    standalone: true,
    imports: [
      NgbAlert,
      ReactiveFormsModule,
      ZXingScannerModule,
      FormsModule,
      DecimalPipe,
      DecimalPipe,
      DecimalPipe,
      DatePipe,
      NgbPopover,
      CurrencyPipe
    ],
    templateUrl: './revenue.component.html',
    styleUrl: './revenue.component.css'
  }
)

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
  posBuddyId: string = PosBuddyConstants.INVALID_POSBUDDY_ID;
  balance: number = 0.00;
  name: string = "";
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
    this.offcanvasService.dismiss("success");
    if (!this.paymentService.isUUID(this.posBuddyId)) {
      this.balance = 0.00;
      this.name = "";
      this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID
      this.confirmError = true;
      this.serverResponse = "ID ungültig"
      return
    }
    this.formValid = true
    this.paymentService.getIdentity(this.posBuddyId).subscribe(
      next => {
        console.log(next.balance);
        this.balance = next.balance;
        this.name = next.surName + " " + next.lastName
        this.getRevenue()
      },
      err => {
        this.balance
        0.00;
        this.name = "";
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

  closeOffcanvas() {
    this.resetOK()
    this.offcanvasService.dismiss();
  }

  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
    this.balance = 0.00;
    this.name = "";
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.balance = 0.00;
    this.name = "";
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
  }


}

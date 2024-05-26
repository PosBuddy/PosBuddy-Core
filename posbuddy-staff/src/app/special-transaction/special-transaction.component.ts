import {Component, inject, TemplateRef} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgbAlert, NgbDropdownModule, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {HttpErrorResponse} from "@angular/common/http";
import {paymentService, specialTransaction} from "../service/payment.service";
import {Constants} from "../constants";

@Component({
  selector: 'app-special-transaction',
  standalone: true,
  imports: [
    FormsModule,
    NgbAlert,
    ZXingScannerModule,
    NgbDropdownModule
  ],
  templateUrl: './special-transaction.component.html',
  styleUrl: './special-transaction.component.css'
})
export class SpecialTransactionComponent {
  private offcanvasService = inject(NgbOffcanvas);
  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;

  formValid: boolean = false;
  formValidText: string = "Betrag eingeben"

  serverResponseText: string = "";

  posBuddyId: string = "-";
  value = '0';
  operation: string = Constants.DEPOSIT;

  constructor(private paymentService: paymentService) {

  }


  checkAndSend(): void {
    let errorText = "";
    if (isNaN(Number(this.value))
      || Number(this.value) <= 0
      || Number(this.value) > Constants.MAX_DEPOSIT) {
      errorText += " / Wert ungültig 1<--> " + Constants.MAX_DEPOSIT + " EUR"
      this.formValid = false;
      this.formValidText = errorText;
      return;
    }
  }

  send(specialTransaction: specialTransaction) {
    console.info("formCheck:OK")
    this.formValid = true;
    this.formValidText = "";
    this.paymentService.doSpecialTransaction(this.posBuddyId, specialTransaction)
      .subscribe({
          next: (v) => {
            this.serverResponse = "OK"
            console.log("suceded")
            this.confirmOK = true;
          },
          error: (e: HttpErrorResponse) => {
            this.confirmError = true;
            switch (e.status) {
              case 400 : {
                this.serverResponse = "Daten ungültig";
                break
              }
              case 401 : {
                this.serverResponse = "Zugriff verweigert";
                break
              }
              case 403 : {
                this.serverResponse = "Guthaben zu gering / ID ungültig";
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
                this.serverResponse = "Fehlercode:" + e.status;
                break
              }
            }
          },
          complete: () => console.info('complete')
        }
      )
  }


  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.formValid = true
    this.offcanvasService.dismiss("success");
  }

  scanQRCode(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'})
  }

  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.value = "0";
    this.posBuddyId = "-";
  }

  protected readonly Constants = Constants;
}

import {Component, inject, TemplateRef} from '@angular/core';
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {paymentService} from "../service/payment.service";
import {HttpErrorResponse} from "@angular/common/http";
import {PosBuddyConstants} from "../posBuddyConstants";

@Component({
  selector: 'app-add-value',
  standalone: true,
  imports: [
    NgbAlert,
    FormsModule,
    ZXingScannerModule
  ],
  templateUrl: './add-value.component.html',
  styleUrl: './add-value.component.css'
})
export class AddValueComponent {
  private offcanvasService = inject(NgbOffcanvas);
  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;

  formValid: boolean = false;
  formValidText: string = "Betrag eingeben"

  serverResponseText: string = "";

  posBuddyId: string = "-";
  value = '0';


  constructor(private paymentService: paymentService) {

  }

  checkAndSend() {
    let formcheck = true;
    let errorText = "";
    if (isNaN(Number(this.value))
      || Number(this.value) <= 0
      || Number(this.value) > PosBuddyConstants.MAX_DEPOSIT) {
      formcheck = false;
      errorText += " / Wert ungültig 1 <--> " + PosBuddyConstants.MAX_DEPOSIT + " EUR"
    }
    if (formcheck) {
      console.info("formCheck:OK")
      this.formValid = true;
      this.formValidText = "";
      this.paymentService.addDeposit(this.posBuddyId, Number(this.value))
        .subscribe({
            next: (v) => {
              this.serverResponse = "OK"
              console.log("suceded")
              this.confirmOK = true;
            },
            error: (e: HttpErrorResponse) => {
              this.confirmError = true;
              switch (e.status) {
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
                  this.serverResponse = "Fehlercode:" + e.status;
                  break
                }
              }
            },
            complete: () => console.info('complete')
          }
        )
    } else {
      this.formValid = false;
      this.formValidText = errorText;
    }
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

}

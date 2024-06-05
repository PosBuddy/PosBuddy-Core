import {Component, inject, TemplateRef} from '@angular/core';
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {HttpErrorResponse} from "@angular/common/http";
import {paymentService} from "../service/payment.service";
import {PosBuddyConstants} from "../posBuddyConstants";


@Component({
  selector: 'app-payout',
  standalone: true,
  imports: [
    NgbAlert,
    ReactiveFormsModule,
    ZXingScannerModule,
    FormsModule
  ],
  templateUrl: './payout.component.html',
  styleUrl: './payout.component.css'
})

export class PayoutComponent {
  constructor(private paymentService: paymentService) {
  }

  private offcanvasService = inject(NgbOffcanvas);
  payoutPossible = false;
  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;
  formValid: boolean = false;
  formValidText: string = "ID zur Auszahlung scannen"
  posBuddyId: string = PosBuddyConstants.INVALID_POSBUDDY_ID;
  value = '0';


  check() {
    // -- Check if balance is valid
    if (isNaN(Number(this.value))
      || Number(this.value) <= 0.00) {
      this.payoutPossible = false;
      this.serverResponse = "Guthaben ungültig 0.1<-->200 EUR"
      this.confirmError = true;
    } else {
      this.payoutPossible = true;
    }
  }

  doPayout() {
    this.paymentService.doPayout(this.posBuddyId)
      .subscribe({
          next: () => {
            this.serverResponse = "Auszahlung erfolgreich"
            console.log("suceded")
            this.confirmOK = true;
          },
          error: (e: HttpErrorResponse) => {
            this.confirmError = true;
            switch (e.status) {
              case 400 : {
                this.serverResponse = "Kein Guthaben";
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
    // check uuid
    if (!this.paymentService.isUUID(this.posBuddyId)) {
      this.value = "0";
      this.serverResponse = "Keine gültige ID"
      this.confirmError = true;
      this.formValid = true
      return
    }
    // get balance
    this.paymentService.getIdentity(this.posBuddyId).subscribe(
      next => {
        this.value = "" + next.balance;
        if (this.value.indexOf(".") > 0) {
          this.value = this.value.substring(0, this.value.indexOf(".") + 2)
        }
        this.check();
      },
      err => {
        this.value = "0";
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

  scanQRCode(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'})
  }

  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
    this.value = "0";
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
    this.payoutPossible = false;
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.value = "0";
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
    this.payoutPossible = false;
  }


}

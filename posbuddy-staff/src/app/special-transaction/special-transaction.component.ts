import {Component, inject, TemplateRef} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgbAlert, NgbDropdownModule, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {HttpErrorResponse} from "@angular/common/http";
import {paymentService, specialTransaction} from "../service/payment.service";
import {PosBuddyConstants} from "../posBuddyConstants";
import {DecimalPipe} from "@angular/common";

@Component({
  selector: 'app-special-transaction',
  standalone: true,
  imports: [
    FormsModule,
    NgbAlert,
    ZXingScannerModule,
    NgbDropdownModule,
    DecimalPipe
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
  itemTexts: string[] = [
    'Gutschrift wegen Überbuchung',
    'Gutschrift wegen Storno',
    'Auszahlung wegen Falschbuchung',
    'Sonstiges',
  ];
  posBuddyId: string = PosBuddyConstants.INVALID_POSBUDDY_ID;
  value = '0';
  balance: number = 0;
  operation: string = PosBuddyConstants.DEPOSIT;
  itemText: string = this.itemTexts[0];

  constructor(private paymentService: paymentService) {

  }


  checkAndSend(): void {
    if (this.posBuddyId == PosBuddyConstants.INVALID_POSBUDDY_ID) {
      this.formValidText = "Bitte ID Scannen"
      this.formValid = false;
      return;
    }
    if (isNaN(Number(this.value))
      || Number(this.value) <= 0
      || Number(this.value) > PosBuddyConstants.MAX_DEPOSIT) {
      this.formValid = false;
      this.formValidText = "Wert ungültig 1<--> " + PosBuddyConstants.MAX_DEPOSIT + " EUR";
      return;
    }
    if (this.operation == PosBuddyConstants.PAYMENT
      && (this.balance - Number(this.value) < 0)) {
      this.formValid = false;
      this.formValidText = "Kein ausreichendes Guthaben";
      return;
    }
    this.formValid = true;
    this.formValidText = "";
    this.send({
        value: Number(this.value),
        action: this.operation,
        itemText: this.itemText
      }
    )
  }

  send(specialTransaction: specialTransaction) {
    console.log(specialTransaction)
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
    this.paymentService.getIdentity(this.posBuddyId).subscribe(
      next => {
        this.balance = next.balance;
        if (this.value.indexOf(".") > 0) {
          this.value = this.value.substring(0, this.value.indexOf(".") + 2)
        }
      },
      err => {
        console.error(err.status)
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
    this.balance = 0;
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.value = "0";
    this.balance = 0;
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
  }

  protected readonly PosBuddyConstants = PosBuddyConstants;
}

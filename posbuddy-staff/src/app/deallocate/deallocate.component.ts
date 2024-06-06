import {Component, inject, TemplateRef} from '@angular/core';
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {HttpErrorResponse} from "@angular/common/http";
import {paymentService} from "../service/payment.service";
import {PosBuddyConstants} from "../posBuddyConstants";

@Component({
  selector: 'app-deallocate',
  standalone: true,
  imports: [
    NgbAlert,
    ReactiveFormsModule,
    ZXingScannerModule,
    FormsModule
  ],
  templateUrl: './deallocate.component.html',
  styleUrl: './deallocate.component.css'
})


export class DeallocateComponent {
  constructor(private paymentService: paymentService) {
  }

  private offcanvasService = inject(NgbOffcanvas);
  deallocatePossible = false;

  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;
  formValid: boolean = false;
  formValidText: string = "ID zur Freigabe scannen"

  posBuddyId: string = PosBuddyConstants.INVALID_POSBUDDY_ID;
  value = '0';
  isStatic: boolean = false;

  check() {
    // -- Check if balance is valid
    if (this.isStatic) {
      this.deallocatePossible = false;
      this.serverResponse = "Diese ID hat eine feste zuordnung"
      this.confirmError = true;
    } else if (isNaN(Number(this.value)) || Number(this.value) != 0) {
      this.deallocatePossible = false;
      this.serverResponse = "Es ist noch Guthaben vorhanden"
      this.confirmError = true;
    } else {
      this.serverResponse = "Freigabe kann erfolgen"
      this.deallocatePossible = true;
    }
    console.info("deallocate possible:" + this.deallocatePossible)
  }

  doDeallocate() {
    this.paymentService.doDeallocate(this.posBuddyId)
      .subscribe({
          next: (v) => {
            this.serverResponse = "Freigabe durchgeführt"
            console.log("suceded")
            this.confirmOK = true;
          },
          error: (e: HttpErrorResponse) => {
            this.confirmError = true;
            switch (e.status) {
              case 400 : {
                this.serverResponse = "Fehler:" + e.error;
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
    this.offcanvasService.dismiss("success");
    // check id
    this.posBuddyId = scanResult;
    if (!this.paymentService.isUUID(this.posBuddyId)) {
      this.value = "0";
      this.confirmError = true;
      this.serverResponse = "Ungültige ID";
      this.formValid = false;
      return
    }
    this.formValid = true
    this.paymentService.getIdentity(this.posBuddyId).subscribe(
      identity => {
        this.value = "" + identity.balance;
        this.isStatic = identity.staticId;
        if (this.value.indexOf(".") > 0) {
          this.value = this.value.substring(0, this.value.indexOf(".") + 2)
        }
        console.log("balance:" + identity.balance);
        this.check();
      },
      err => {
        console.error(err.status)
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
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.value = "0";
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
    this.deallocatePossible = false;
  }


}

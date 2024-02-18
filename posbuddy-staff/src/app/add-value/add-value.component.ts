import {Component, inject, TemplateRef} from '@angular/core';
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {paymentService} from "../service/payment.service";

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

  formValid: boolean = false;
  formValidText: string = "Betrag eingeben"

  serverResponse: string = "-"
  serverResponseText: string = "";

  posBuddyId: string = "-";
  value = '0';


  constructor(private paymentService: paymentService) {

  }

  send() {
    if (this.posBuddyId != "-") {
      this.formValid = true
    }
    this.paymentService
      .addPayment(this.posBuddyId, Number(this.value))
      .subscribe({
          next: (v) => {
            this.serverResponse = "OK"
            console.log("suceded")
          },
          error: (e) => {
            this.serverResponse = "ERROR"
            this.serverResponseText = e.statusText
            console.error(e)
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
    this.serverResponse = "-"
    this.value = "0"
    this.posBuddyId = "-"
  }

}

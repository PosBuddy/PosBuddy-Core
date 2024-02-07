import {Component, inject, TemplateRef} from '@angular/core';
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";

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
  posBuddyId: string = "-";
  value = '0';


  send() {
    if (this.posBuddyId != "-") {
      this.formValid = true
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


}

import {Component, inject, TemplateRef} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgbOffcanvas, OffcanvasDismissReasons} from "@ng-bootstrap/ng-bootstrap";
import {ZXingScannerModule} from "@zxing/ngx-scanner";


@Component({
  selector: 'app-allocate-id',
  standalone: true,
  imports: [FormsModule, ZXingScannerModule],
  templateUrl: './allocate-id.component.html',
  styleUrl: './allocate-id.component.css'
})
export class AllocateIdComponent {
  private offcanvasService = inject(NgbOffcanvas);
  closeResult = '';
  formValid: boolean = false;
  posBuddyId: string = "-";
  surname = '';
  lastname = '';
  birthday = '08.03.1975';
  attribute1 = '';
  attribute2 = '';
  attribute3 = '';
  balance = '0'


  scanQRCode(content: TemplateRef<any>) {
    //@ts-ignore
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'}).result.then(
      //@ts-ignore
      (result) => {
        this.closeResult = `Closed with: ${result}`;
      },
      //@ts-ignore
      (reason) => {
        this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      },
    );
  }

  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.formValid = true
    this.offcanvasService.dismiss("success");
  }

  private getDismissReason(reason: any): string {
    switch (reason) {
      case OffcanvasDismissReasons.ESC:
        return 'by pressing ESC';
      case OffcanvasDismissReasons.BACKDROP_CLICK:
        return 'by clicking on the backdrop';
      default:
        return `with: ${reason}`;
    }
  }

}

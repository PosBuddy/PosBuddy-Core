import {Component, inject, TemplateRef} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgbAlert, NgbOffcanvas, OffcanvasDismissReasons} from "@ng-bootstrap/ng-bootstrap";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {AllocateService} from "../service/allocate.service";


@Component({
  selector: 'app-allocate-id',
  standalone: true,
  imports: [FormsModule, ZXingScannerModule, NgbAlert],
  templateUrl: './allocate-id.component.html',
  styleUrl: './allocate-id.component.css'
})

export class AllocateIdComponent {
  private offcanvasService = inject(NgbOffcanvas);
  closeResult = '';
  formValid: boolean = false;
  formValidText: string = "Min. ID und Wert "
  serverResponse: string = "-"
  posBuddyId: string = "-";
  surname = '';
  lastname = '';
  birthday = '1975-03-08';
  attribute1 = '';
  attribute2 = '';
  attribute3 = '';
  balance = '0'

  constructor(private allocateService: AllocateService) {
  }


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
    if (this.isUUID(this.posBuddyId)) {
      this.formValid = true
    } else {
      this.formValidText = "ID ungültig"
      this.formValid = false
    }
    this.offcanvasService.dismiss("success");
  }

  checkAndSend() {
    let formcheck = true;
    let errorText = "";
    if (!this.isUUID(this.posBuddyId)) {
      formcheck = false;
      errorText += "ID ungültig"
    }
    if (isNaN(Number(this.balance)) || Number(this.balance) <= 0) {
      formcheck = false;
      errorText += " / Wert ungültig"
    }

    if (formcheck) {
      console.info("formCheck:OK")
      this.formValid = true;
      this.formValidText = "";
      this.allocateService.allocatePosBuddyId(
        this.posBuddyId,
        {
          allocatePosBuddyIdRequest: {
            attribute1: this.attribute1,
            attribute2: this.attribute2,
            attribute3: this.attribute3,
            balance: Number(this.balance),
            birthday: this.birthday,
            lastname: this.lastname,
            surname: this.surname
          }
        }
      ).subscribe({
          next: (v) => {
            this.serverResponse = "OK"
            console.log("suceded")
          },
          error: (e) => {
            this.serverResponse = "ERROR"
            console.error("ERROR:" + e)
          },
          complete: () => console.info('complete')
        }
      )
    } else {
      this.formValid = false;
      this.formValidText = errorText;
    }
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


  private isUUID(s: string): boolean {
    if (s.match("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$") != null) {
      return true
    }
    return false
  }

}

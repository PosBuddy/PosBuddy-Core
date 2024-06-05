import {AfterViewInit, Component, ElementRef, inject, Input, TemplateRef, ViewChild} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgbAlert, NgbOffcanvas, OffcanvasDismissReasons} from "@ng-bootstrap/ng-bootstrap";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {AllocatePosBuddyIdRequest, AllocateService} from "../service/allocate.service";
import {HttpErrorResponse} from "@angular/common/http";
import {timer} from "rxjs";
import {PosBuddyConstants} from "../posBuddyConstants";
import {paymentService} from "../service/payment.service";


@Component({
  selector: 'app-allocate-id',
  standalone: true,
  imports: [FormsModule, ZXingScannerModule, NgbAlert],
  templateUrl: './allocate-id.component.html',
  styleUrl: './allocate-id.component.css'
})

export class AllocateIdComponent implements AfterViewInit {
  @Input() permissions = {
    "servePermission": false,
    "checkoutPermission": false,
    "adminPermission": false,
  }
  @ViewChild("surnameInput") surnameInputElement!: ElementRef;
  private offcanvasService = inject(NgbOffcanvas);
  closeResult = '';
  formValid: boolean = false;
  formValidText: string = "Mindestens Id und Wert angeben."
  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;
  // -- Form
  posBuddyId: string = PosBuddyConstants.INVALID_POSBUDDY_ID;
  disableIdScan = false;
  borrowCard: boolean = true;
  oneTimeCard: boolean = false;
  staticCard: boolean = false;
  surname = '';
  lastname = '';
  birthday = '1975-03-08';
  attribute1 = '';
  attribute2 = '';
  attribute3 = '';
  balance = '0'

  constructor(
    private allocateService: AllocateService,
    private paymentService: paymentService) {
  }


  ngAfterViewInit(): void {
    timer(500).subscribe(() => this.surnameInputElement.nativeElement.focus());
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

  toggleCardType(cardType: string) {
    this.borrowCard = false;
    this.oneTimeCard = false;
    this.staticCard = false;
    if (cardType === PosBuddyConstants.CARD_TYPE_BORROW) {
      this.borrowCard = true
      this.disableIdScan = false
    }
    if (cardType === PosBuddyConstants.CARD_TYPE_ONE_TIME) {
      this.oneTimeCard = true
      this.disableIdScan = true
      this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID
    }
    if (cardType === PosBuddyConstants.CARD_TYPE_STATIC) {
      this.staticCard = true
      this.disableIdScan = false
    }
  }

  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    if (this.paymentService.isUUID(this.posBuddyId)) {
      this.formValid = true
    } else {
      this.formValidText = "ID ungültig"
      this.formValid = false
      this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID
    }
    this.offcanvasService.dismiss("success");
  }

  checkAndSend() {
    // check ID
    if (this.borrowCard || this.staticCard) {
      if (this.paymentService.isUUID(this.posBuddyId) == false) {
        this.formValid = false;
        this.formValidText = "ID ungültig - Bitte Id scannen";
        return
      }
    }
    // check value
    if (isNaN(Number(this.balance))
      || Number(this.balance) <= 0
      || Number(this.balance) > PosBuddyConstants.MAX_DEPOSIT) {
      this.formValid = false;
      this.formValidText = "Wert ungültig 1 <--> " + PosBuddyConstants.MAX_DEPOSIT + " EUR"
      return
    }
    // send to backend
    this.formValid = true;
    this.formValidText = "";
    const request: AllocatePosBuddyIdRequest = {
      surname: this.surname,
      lastname: this.lastname,
      birthday: this.birthday,
      attribute1: this.attribute1,
      attribute2: this.attribute2,
      attribute3: this.attribute3,
      balance: Number(Number(this.balance))
    }
    if (this.borrowCard) {
      this.allocateService.allocateVolatilePosBuddyId(
        this.posBuddyId, request).subscribe({
          next: (v) => {
            this.serverResponse = "OK"
            this.confirmOK = true;
          },
          error: (e: HttpErrorResponse) => {
            this.confirmError = true;
            switch (e.status) {
              case 400 : {
                this.serverResponse = "ID ungültig";
                break
              }
              case 401 : {
                this.serverResponse = "Zugriff verweigert";
                break
              }
              case 405 : {
                this.serverResponse = "keine Berechtigung";
                break
              }
              case 409 : {
                this.serverResponse = "ID bereits zugewiesen";
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
    if (this.staticCard) {
      this.allocateService.allocateStaticPosBuddyId(
        this.posBuddyId, request).subscribe({
          next: (v) => {
            this.serverResponse = "OK ID zugewiesen"
            this.confirmOK = true;
          },
          error: (e: HttpErrorResponse) => {
            this.confirmError = true;
            switch (e.status) {
              case 400 : {
                this.serverResponse = "ID ungültig";
                break
              }
              case 401 : {
                this.serverResponse = "Zugriff verweigert";
                break
              }
              case 405 : {
                this.serverResponse = "keine Berechtigung";
                break
              }
              case 409 : {
                this.serverResponse = "ID bereits zugewiesen";
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
    if (this.oneTimeCard) {
      this.allocateService.allocateOneTimeId(request).subscribe({
          next: (v) => {
            this.serverResponse = "OK"
            this.confirmOK = true;
          },
          error: (e: HttpErrorResponse) => {
            this.confirmError = true;
            switch (e.status) {
              case 400 : {
                this.serverResponse = "ID ungültig";
                break
              }
              case 401 : {
                this.serverResponse = "Zugriff verweigert";
                break
              }
              case 405 : {
                this.serverResponse = "keine Berechtigung";
                break
              }
              case 409 : {
                this.serverResponse = "ID bereits zugewiesen";
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
  }

  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.balance = "0";
    this.surname = "";
    this.lastname = "";
    this.attribute1 = "";
    this.attribute2 = "";
    this.attribute3 = "";
    this.toggleCardType(PosBuddyConstants.CARD_TYPE_BORROW);
    this.posBuddyId = PosBuddyConstants.INVALID_POSBUDDY_ID;
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


  protected readonly PosBuddyConstants = PosBuddyConstants;
}

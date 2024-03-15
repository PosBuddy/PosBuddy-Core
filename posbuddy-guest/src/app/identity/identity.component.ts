import {AfterViewInit, Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {ZXingScannerModule} from '@zxing/ngx-scanner';
import {IdentityService} from "./identity.service";
import {HttpClientModule} from "@angular/common/http";
import {DecimalPipe} from "@angular/common";
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";


@Component({
  selector: 'app-identity',
  standalone: true,
  imports: [ZXingScannerModule, HttpClientModule, DecimalPipe, ZXingScannerModule, NgbAlert],
  templateUrl: './identity.component.html',
  styleUrl: './identity.component.css'
})

export class IdentityComponent implements AfterViewInit {
  @ViewChild('revenueOC') revenueOCTemplate: TemplateRef<any> | undefined;
  private offcanvasService = inject(NgbOffcanvas);

  name: string = "Bitte Scannen";
  revenue: number = 0;
  youthLaw: boolean = true;
  posBuddyId: string = IdentityService.UNKNOWN_ID;
  serverResponse: string = "";
  confirmError: boolean = false;


  constructor(private idendityService: IdentityService) {
    if (idendityService.isLocalIdentityValid()) {
      this.posBuddyId = idendityService.getLocalidentity();
      this.idendityService.getIentityById(this.posBuddyId)
        .subscribe(data => {
            this.name = data.surName + " " + data.lastName
            this.revenue = Number(data.balance)
          }, error => {
            this.confirmError = true;
            switch (error.status) {
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
                this.serverResponse = "Fehlercode:" + error.status;
                break
              }
            }
          }
        );
    }

  }


  ngAfterViewInit() {
    console.log("AfterViewinit:" + this.posBuddyId)
    if (this.posBuddyId == IdentityService.UNKNOWN_ID) {
      console.log("No ID in Localcache")
    }
  }

  getYouthLawText(): string {
    if (this.youthLaw) {
      return "Ja"
    } else {
      return "Nein"
    }
  }

  scanQRCode(content: TemplateRef<any>): void {
    this.offcanvasService.open(content, {ariaLabelledBy: 'scanId'})
  }

  scanError(error: Error) {
    console.error("Scan error:" + error);
  }

  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
    this.posBuddyId = "-";
    this.revenue = 0;
    this.youthLaw = false;
  }


  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.offcanvasService.dismiss("success");
    
    this.idendityService.getIentityById(this.posBuddyId)
      .subscribe(data => {
          this.name = data.surName + " " + data.lastName
          this.revenue = Number(data.balance)
          this.idendityService.setLocalIdentity(this.posBuddyId)
        }, error => {
          this.confirmError = true;
          switch (error.status) {
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
              this.serverResponse = "Fehlercode:" + error.status;
              break
            }
          }
        }
      );
  }

}

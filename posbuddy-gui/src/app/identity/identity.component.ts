import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {ZXingScannerComponent, ZXingScannerModule} from '@zxing/ngx-scanner';
import {IdendityService} from "./idendity.service";
import {HttpClientModule} from "@angular/common/http";


@Component({
  selector: 'app-identity',
  standalone: true,
  imports: [ZXingScannerModule, HttpClientModule],
  templateUrl: './identity.component.html',
  styleUrl: './identity.component.css'
})
export class IdentityComponent implements AfterViewInit {

  @ViewChild(ZXingScannerComponent) qrCodeScanner: ZXingScannerComponent | undefined;

  name: string = "Bitte Scannen"
  revenue: number = 0
  youthLaw: boolean = true
  posBuddyId: string = IdendityService.UNKNOWN_ID

  constructor(private idendityService: IdendityService) {
    if (idendityService.isLocalIdentityValid()) {
      this.posBuddyId = idendityService.getLocalidentity();
      this.idendityService.getIentityById(this.posBuddyId)
        .subscribe(data => {
            this.name = data.surName + " " + data.lastName
            this.revenue = Number(data.balance)
          }
        );
    }

  }


  ngAfterViewInit() {
    console.log("AfterViewinit:"+ this.posBuddyId)
    if (this.posBuddyId == IdendityService.UNKNOWN_ID) {
      console.log("Dä" + this.qrCodeScanner)

      this.qrCodeScanner!.enable = true;
    }
  }

  getYouthLawText(): string {
    if (this.youthLaw) {
      return "Ja"
    } else {
      return "Nein"
    }
  }

  scanQRCode(): void {
    if (this.qrCodeScanner!.enable == true) {
      this.qrCodeScanner!.enable = false;
    } else {
      this.qrCodeScanner!.enable = true;
    }
  }

  scanError(error: Error) {
    console.error("Scan error:" + error);
  }

  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.qrCodeScanner!.enable = false;
    this.idendityService.getIentityById(this.posBuddyId)
      .subscribe(data => {
          this.name = data.surName + " " + data.lastName
          this.revenue = Number(data.balance)
        }
      );
    this.idendityService.setLocalIdentity(this.posBuddyId)
  }

}

import {Component, ViewChild} from '@angular/core';
import {ZXingScannerComponent, ZXingScannerModule} from '@zxing/ngx-scanner';


@Component({
  selector: 'app-identity',
  standalone: true,
  imports: [ZXingScannerModule],
  templateUrl: './identity.component.html',
  styleUrl: './identity.component.css'
})
export class IdentityComponent {

  @ViewChild(ZXingScannerComponent) qrCodeScanner: ZXingScannerComponent | undefined;

  name: string = "Unbekannt"
  revenue: number = 0
  youthLaw: boolean = true;


  getYouthLawText(): string {
    if (this.youthLaw) {
      return "Ja"
    } else {
      return "Nein"
    }
  }

  scanQRCode(): void {
    this.qrCodeScanner!.enable = true;

  }

  scanError(error: Error) {
    console.error(error);
  }

  onScanSuccess(e: string) {
    console.log("->" + e + "<-")
    this.qrCodeScanner!.enable = false;
  }

}

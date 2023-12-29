import {Component} from '@angular/core';
import {ZXingScannerModule} from '@zxing/ngx-scanner';


@Component({
  selector: 'app-identity',
  standalone: true,
  imports: [ZXingScannerModule],
  templateUrl: './identity.component.html',
  styleUrl: './identity.component.css'
})
export class IdentityComponent {

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
}

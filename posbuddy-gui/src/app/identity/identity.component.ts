import {Component} from '@angular/core';

@Component({
  selector: 'app-identity',
  standalone: true,
  imports: [],
  templateUrl: './identity.component.html',
  styleUrl: './identity.component.css'
})
export class IdentityComponent {

  name: string = "Unbekannt"
  revenue: number = 0
  youthLaw: boolean = true;


  function getYouthLawText(): string {
    if (this.youthLaw) {
      return "Ja"
    } else {
      return "Nein"
    }
  }

}

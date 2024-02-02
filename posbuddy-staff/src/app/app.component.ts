import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NgbAccordionModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule} from "@angular/forms";
import {AllocateIdComponent} from "./allocate-id/allocate-id.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgbAccordionModule, RouterOutlet, FormsModule, AllocateIdComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'posbuddy-staff';
  items = ['Karte zuweisen', 'Einzahlen', 'Auszahlen', 'Karte trennen'];


  playerName: string = ""

  onSubmit() {
    return this.playerName
  }
}

import {Component} from '@angular/core';
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-allocate-id',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './allocate-id.component.html',
  styleUrl: './allocate-id.component.css'
})
export class AllocateIdComponent {
  posBuddyId:string = "-";
  surname = '';
  lastname = '';
  birthday = '08.03.1975';
  attribute1 = '';
  attribute2 = '';
  attribute3 = '';
  balance = '0'

}

import { Component } from '@angular/core';

@Component({
  selector: 'app-add-value',
  standalone: true,
  imports: [],
  templateUrl: './add-value.component.html',
  styleUrl: './add-value.component.css'
})
export class AddValueComponent {
  formValid: boolean = false;
  posBuddyId: string = "-";
  surname = '';


}

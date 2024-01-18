import { Component } from '@angular/core';
import {Revenue} from "../model/Revenue";
import {NgbHighlight} from "@ng-bootstrap/ng-bootstrap";
import {NgForOf} from "@angular/common";

@Component({
  selector: 'app-revenue',
  standalone: true,
  imports: [
    NgbHighlight,
    NgForOf
  ],
  templateUrl: './revenue.component.html',
  styleUrl: './revenue.component.css'
})


export class RevenueComponent {

  revenueEntryList: Array<Revenue> | undefined;


  protected readonly Revenue = Revenue;
}


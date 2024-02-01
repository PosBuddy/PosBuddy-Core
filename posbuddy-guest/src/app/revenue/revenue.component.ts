import {AfterViewInit, Component} from '@angular/core';
import {Revenue} from "../model/Revenue";
import {NgbHighlight} from "@ng-bootstrap/ng-bootstrap";
import {NgForOf} from "@angular/common";
import {RevenueService} from "./revenue.service";
import {IdentityService} from "../identity/identity.service";

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


export class RevenueComponent implements AfterViewInit {

  revenues: Array<Revenue> = [];
  localPosBuddyId = IdentityService.UNKNOWN_ID;

  constructor(
    private revenueService: RevenueService,
    private identityService: IdentityService
  ) {
    this.localPosBuddyId = this.identityService.getLocalidentity();

  }


  getRevenues(): void {
    this.revenueService.getRevenue(this.localPosBuddyId).subscribe(data => {
        this.revenues = data;
      }
    );
  }

  ngAfterViewInit(): void {
    this.getRevenues();
  }

}


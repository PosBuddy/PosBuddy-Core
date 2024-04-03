import {Component} from '@angular/core';
import {DatePipe, DecimalPipe, NgForOf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {staticIdData, StaticIdService} from "../services/static-id.service";
import {NgbAlert} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-static-data',
  standalone: true,
  imports: [
    DecimalPipe,
    ReactiveFormsModule,
    NgbAlert,
    DatePipe,
    NgForOf
  ],
  templateUrl: './static-data.component.html',
  styleUrl: './static-data.component.css'
})

export class StaticDataComponent {
  serverResponse: string = "";
  balance: number = 0;
  syncDate: string = "";
  staticData: staticIdData = {
    posBuddyId: "",
    syncTimeStamp: "",
    balance: 0,
    revenueList: [{
      action: "",
      amount: 0,
      itemText: "",
      timeOfAction: "",
      value: 0
    }]
  }

  constructor(private staticIdService: StaticIdService) {
  }

  ngAfterViewInit() {
    this.staticIdService.getIdentity("6be777b9-4cfe-4c3b-a8c5-5f6874efe84f.json")
      .subscribe(data => {
          this.staticData = data;
          console.log(this.staticData)
        }, error => {
          switch (error.status) {
            case 404 : {
              this.serverResponse = "Keine Daten verfügbar";
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

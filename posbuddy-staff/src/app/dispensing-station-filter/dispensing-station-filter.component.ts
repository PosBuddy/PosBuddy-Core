import {AfterViewInit, Component} from '@angular/core';
import {dispensingStation,paymentService} from "../service/payment.service";

@Component({
  selector: 'app-dispensing-station-filter',
  standalone: true,
  imports: [],
  templateUrl: './dispensing-station-filter.component.html',
  styleUrl: './dispensing-station-filter.component.css'
})
export class DispensingStationFilterComponent implements AfterViewInit {

  dispensingStations: Array<dispensingStation> = [];
  serverResponse: string = "-"
  confirmError: boolean = false;

  constructor(private paymentService: paymentService) {

  }

  ngAfterViewInit() {
    console.log("get items and dispensing stations")
    this.paymentService
      .getDispensingStations()
      .subscribe(dispensingStations => {
          this.dispensingStations = dispensingStations;
        }, err => {
          this.confirmError = true;
          this.serverResponse = "Fehler bei Laden der Ausgabestationen"
        }
      )
  }
}

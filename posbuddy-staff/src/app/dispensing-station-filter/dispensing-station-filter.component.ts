import {AfterViewInit, Component} from '@angular/core';
import {dispensingStation, paymentService} from "../service/payment.service";
import {PosBuddyConstants} from "../posBuddyConstants";

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

  setDispFilterToLocalStorage(dispensingStatiionId: String, event: any) {
    //console.log("SetFilter on dispensingStationId:" + dispensingStatiionId + "->" + event.target.checked)
    if (event.target.checked) {
      localStorage.setItem(
        PosBuddyConstants.DISPENSING_STATION_FILTER_PRE + dispensingStatiionId,
        String(true)
      );
    } else {
      localStorage.removeItem(
        PosBuddyConstants.DISPENSING_STATION_FILTER_PRE + dispensingStatiionId
      );
    }
  }

  getDispFilterFromLocalStorage(dispensingStatiionId: String): boolean {
    //console.log("getFilter localStorage:" + localStorage.getItem(STATION_FILTER_PRE + dispensingStatiionId))
    if (localStorage
      .getItem(PosBuddyConstants.DISPENSING_STATION_FILTER_PRE + dispensingStatiionId) == null) {
      return false
    }
    return true
  }

}

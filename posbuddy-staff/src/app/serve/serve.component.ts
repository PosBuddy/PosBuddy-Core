import {AfterViewInit, Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import {NgbAlert, NgbCollapse, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {dispensingStation, item, paymentService, serve} from "../service/payment.service";
import {PosBuddyConstants} from "../posBuddyConstants";

@Component({
  selector: 'app-serve',
  standalone: true,
  imports: [
    DecimalPipe,
    NgbAlert,
    ReactiveFormsModule,
    ZXingScannerModule,
    FormsModule,
    DecimalPipe,
    NgbCollapse
  ],
  templateUrl: './serve.component.html',
  styleUrl: './serve.component.css'
})
export class ServeComponent implements AfterViewInit {
  @ViewChild('serveOC') serveOCTemplate: TemplateRef<any> | undefined;

  constructor(private paymentService: paymentService) {

  }

  private offcanvasService = inject(NgbOffcanvas);
  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;
  iscollapsed: boolean = false;
  posBuddyId: string = "-";
  name: string = "";
  balance = '0';

  orderValue: number = 0;

  items: Array<item> = [];
  dispensingStations: Array<dispensingStation> = [];
  serveitems: Array<serve> = [];

  ngAfterViewInit() {
    console.log("get items and dispensing stations")
    this.paymentService
      .getDispensingStations()
      .subscribe(dispensingStations => {
          this.dispensingStations = dispensingStations;
        }, err => {
          this.confirmError = true;
          this.serverResponse = "Fehler bei laden der Ausgabestationen"
        }
      )
    this.paymentService
      .getItems()
      .subscribe(items => {
          this.items = items;
          this.serveitems.length = 0;
          this.items.map(value => this.serveitems.push({"itemId": value.id, "count": 0}))
        }, err => {
          this.serveitems.length = 0;
          this.confirmError = true;
          this.serverResponse = "Fehler bei laden der Artikel"
        }
      )
  }

  doServe() {
    let postServeitems: Array<serve> = [];
    this.serveitems.map(value => {
      if (value.count > 0) {
        postServeitems.push(value)
      }
    })
    console.log(postServeitems)
    this.paymentService.serve(postServeitems, this.posBuddyId)
      .subscribe(
        value => {
          this.confirmOK = true;
        },
        error => {
          this.confirmError = true;
          switch (error.status) {
            case 400 : {
              this.serverResponse = "Guthaben zu gering";
              break
            }
            case 403 : {
              this.serverResponse = "Zugriff verweigert";
              break
            }
            case 404 : {
              this.serverResponse = "ID nicht zugeordnet";
              break
            }
            case 405 : {
              this.serverResponse = "keine Berechtigung";
              break
            }
            default : {
              this.serverResponse = "Fehlercode:" + error.status;
              break
            }
          }
        }
      )
  }

  scanQRCode(content: TemplateRef<any>) {
    this.resetServeData()
    this.offcanvasService.open(content, {ariaLabelledBy: 'scanId'})
  }

  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.offcanvasService.dismiss("success");
    this.paymentService.getIdentity(this.posBuddyId).subscribe(
      next => {
        this.balance = "" + next.balance;
        if (this.balance.indexOf(".") > 0) {
          this.balance = this.balance.substring(0, this.balance.indexOf(".") + 2)
        }
        this.name = next.surName + " " + next.lastName
        this.offcanvasService.open(this.serveOCTemplate, {ariaLabelledBy: 'revenue'})
      },
      err => {
        this.balance = "0";
        this.name = "";
        this.confirmError = true;
        switch (err.status) {
          case 400 : {
            this.serverResponse = "Ungültige ID";
            break
          }
          case 401 : {
            this.serverResponse = "Zugriff verweigert";
            break
          }
          case 404 : {
            this.serverResponse = "ID nicht zugeordnet";
            break
          }
          case 405 : {
            this.serverResponse = "keine Berechtigung";
            break
          }
          default : {
            this.serverResponse = "Fehlercode:" + err.status;
            break
          }
        }
      },
      () => console.log('HTTP request completed.')
    );
  }


  checkDisableAddItem(price: number): boolean {
    if (this.orderValue + price > Number(this.balance)) {
      return true;
    }
    return false;
  }

  incServeItemCount(itemId: string) {
    const cost = this.items.find(sitem => sitem.id == itemId)?.price ?? 0;
    this.serveitems.map(value => (value.itemId === itemId ? {"count": value.count += 1} : value));
    this.orderValue += cost;
  }

  decServeItemCount(itemId: string) {
    const cost = this.items.find(sitem => sitem.id == itemId)?.price ?? 0;
    if (this.serveitems.find(value => value.itemId == itemId && value.count > 0)) {
      this.orderValue -= cost;
      this.serveitems.map(value => (value.itemId === itemId ? {"count": value.count -= 1} : value));
    }
  }

  getServeItemCount(itemId: string) {
    return this.serveitems.find(value => value.itemId == itemId)?.count
  }


  resetServeData() {
    this.balance = "0";
    this.orderValue = 0;
    this.serveitems.map(value => value.count = 0)
  }

  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
    this.posBuddyId = "-";
    this.resetServeData();
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.posBuddyId = "-";
    this.resetServeData();
  }

  isItemsFromStationSelectable(dispensingStationId: string): boolean {
    if (localStorage.getItem(
      PosBuddyConstants.DISPENSING_STATION_FILTER_PRE + dispensingStationId
    ) != null) {
      return true;
    }
    return false;
  }

}

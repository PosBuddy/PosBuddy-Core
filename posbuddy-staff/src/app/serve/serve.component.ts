import {Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {DecimalPipe} from "@angular/common";
import {NgbAlert, NgbCollapse, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {ZXingScannerModule} from "@zxing/ngx-scanner";
import {dispensingStation, item, paymentService, serve} from "../service/payment.service";

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
export class ServeComponent {
  @ViewChild('serveOC') serveOCTemplate: TemplateRef<any> | undefined;

  constructor(private paymentService: paymentService) {

  }

  private offcanvasService = inject(NgbOffcanvas);
  serverResponse: string = "-"
  confirmError: boolean = false;
  confirmOK: boolean = false;
  iscollapsed: boolean = false;
  formValidText: string = "ID für Bestellung scannen"
  posBuddyId: string = "-";
  balance = '12';

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
    //this.paymentService.serve()
  }

  scanQRCode(content: TemplateRef<any>) {
    //this.offcanvasService.open(content, {ariaLabelledBy: 'scanId'})
    this.offcanvasService.open(this.serveOCTemplate, {ariaLabelledBy: 'scanId'})
  }

  onScanSuccess(scanResult: string) {
    this.posBuddyId = scanResult;
    this.offcanvasService.dismiss("success");
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


  resetError() {
    this.confirmError = false;
    this.serverResponse = "-";
    this.posBuddyId = "-";
  }

  resetOK() {
    this.confirmOK = false;
    this.serverResponse = "-";
    this.posBuddyId = "-";
  }


}

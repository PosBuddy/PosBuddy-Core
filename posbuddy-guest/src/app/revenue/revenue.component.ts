import {Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {NgbAlert, NgbHighlight, NgbOffcanvas, NgbPopover} from "@ng-bootstrap/ng-bootstrap";
import {DatePipe, DecimalPipe, NgForOf} from "@angular/common";
import {idData, RevenueService, UNKNOWN_ID} from "../services/revenue.service";
import {ZXingScannerModule} from "@zxing/ngx-scanner";

@Component({
  selector: 'app-revenue',
  standalone: true,
  imports: [
    NgbHighlight,
    NgForOf,
    DecimalPipe,
    DatePipe,
    NgbAlert,
    NgbPopover,
    ZXingScannerModule
  ],
  templateUrl: './revenue.component.html',
  styleUrl: './revenue.component.css'
})


export class RevenueComponent {
  @ViewChild('scanIdOC') scanIdOCTemplate: TemplateRef<any> | undefined;
  private offcanvasService = inject(NgbOffcanvas);
  protected actPosBuddyId: string = UNKNOWN_ID;

  serverResponse: string = "";
  nullIdData: idData = {
    posBuddyId: "",
    surName: "",
    lastName: "",
    youthProtectionAct: true,
    balance: 0,
    revenueList: [{action: "", amount: 0, itemText: "", timeOfAction: "", value: 0}]
  }
  idData: idData = this.nullIdData;

  constructor(private revenueService: RevenueService) {
  }

  scanQRCode(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'scanId'})
  }

  onScanSuccess(scanResult: string) {
    this.actPosBuddyId = scanResult;
    console.log("scan success:" + this.actPosBuddyId)
    this.offcanvasService.dismiss("success");
    this.loadData()
  }

  resetData() {
    this.actPosBuddyId = UNKNOWN_ID;
    this.idData = this.nullIdData;
  }

  private loadData() {
    this.revenueService.getIdentityData(this.actPosBuddyId)
      .subscribe(data => {
          this.idData = data;
          this.serverResponse = "";
        }, error => {
          console.error("Server response error" + error)
          this.resetData();
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


  protected readonly UNKNOWN_ID = UNKNOWN_ID;
}


import {Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {DatePipe, DecimalPipe, NgForOf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {staticIdData, StaticIdService, UNKNOWN_ID} from "../services/static-id.service";
import {NgbAlert, NgbOffcanvas} from "@ng-bootstrap/ng-bootstrap";
import {ZXingScannerModule} from "@zxing/ngx-scanner";

@Component({
  selector: 'app-static-data',
  standalone: true,
  imports: [
    DecimalPipe,
    ReactiveFormsModule,
    NgbAlert,
    DatePipe,
    NgForOf,
    ZXingScannerModule
  ],
  templateUrl: './static-data.component.html',
  styleUrl: './static-data.component.css'
})

export class StaticDataComponent {
  @ViewChild('scanIdOC') revenueOCTemplate: TemplateRef<any> | undefined;
  private offcanvasService = inject(NgbOffcanvas);
  protected actPosBuddyId: string = UNKNOWN_ID;

  serverResponse: string = "";
  staticData: staticIdData = {
    posBuddyId: "",
    syncTimeStamp: "",
    balance: 0,
    revenueList: [{action: "", amount: 0, itemText: "", timeOfAction: "", value: 0}]
  }

  constructor(private staticIdService: StaticIdService) {
  }

  onScanSuccess(scanResult: string) {
    this.actPosBuddyId = scanResult;
    console.log("scan success:" + this.actPosBuddyId)
    this.offcanvasService.dismiss("success");
    this.loadData()
  }

  ngAfterViewInit() {
    console.log("ngAfterViewInit" + this.actPosBuddyId)
    this.checkLocalStorage()
    if (this.actPosBuddyId != UNKNOWN_ID) {
      this.loadData();
    }
  }

  scanQRCode(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'scanId'})
  }


  private loadData() {
    this.staticIdService.getIdentity(this.actPosBuddyId + ".json")
      .subscribe(data => {
          this.setLocalIdentity();
          this.staticData = data;
          this.serverResponse = "";
        }, error => {
          console.error("Server response error" + error)
          this.actPosBuddyId = UNKNOWN_ID;
          this.unSetLocalIdentity();
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

  checkLocalStorage() {
    let localPosBuddyId = localStorage.getItem('posBuddyId')
    let localPosBuddyTimestamp = localStorage.getItem('posBuddyIdTimestamp')
    if (localPosBuddyId == null) {
      console.log("localposbuddyID is null")
      this.actPosBuddyId = UNKNOWN_ID
      return;
    }
    if (localPosBuddyTimestamp != null) {
      if (Date.now() - Number(localPosBuddyTimestamp) > (24 * 60 * 60 * 1000)) {
        console.log("posBuddyId is to old");
        this.actPosBuddyId = UNKNOWN_ID;
        return;
      }
    }
    this.actPosBuddyId = localPosBuddyId;
  }

  setLocalIdentity() {
    console.log("set local id to :" + this.actPosBuddyId)
    localStorage.setItem('posBuddyId', this.actPosBuddyId)
    localStorage.setItem('posBuddyIdTimestamp', Date.now().toString())
  }

  unSetLocalIdentity() {
    localStorage.removeItem('posBuddyId');
    localStorage.removeItem('posBuddyIdTimestamp');
  }

  protected readonly UNKNOWN_ID = UNKNOWN_ID;
}

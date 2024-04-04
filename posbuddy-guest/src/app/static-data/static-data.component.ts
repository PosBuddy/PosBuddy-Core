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
  @ViewChild('revenueOC') revenueOCTemplate: TemplateRef<any> | undefined;
  private offcanvasService = inject(NgbOffcanvas);
  posBuddyId: string = UNKNOWN_ID;

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
    this.posBuddyId = scanResult;
    console.log("scan success:" + this.posBuddyId)
    this.offcanvasService.dismiss("success");
    this.loadData()
  }

  ngAfterViewInit() {
    console.log(this.posBuddyId)
    if (this.posBuddyId != UNKNOWN_ID) {
      this.loadData();
    }
  }

  scanQRCode(content: TemplateRef<any>) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'scanId'})
  }


  private loadData() {
    this.staticIdService.getIdentity(this.posBuddyId + ".json")
      .subscribe(data => {
          this.staticData = data;
          this.setLocalIdentity(this.posBuddyId);
        }, error => {
          console.error(error)
          this.posBuddyId = UNKNOWN_ID;
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

  isLocalIdentityValid(): boolean {
    let localPosBuddyId = localStorage.getItem('posBuddyId')
    let localPosBuddyTimestamp = localStorage.getItem('posBuddyIdTimestamp')
    if (localPosBuddyId == null) {
      this.posBuddyId = UNKNOWN_ID
      return false;
    }
    if (localPosBuddyTimestamp != null) {
      if (Date.now() - Number(localPosBuddyTimestamp) > (24 * 60 * 60 * 1000)) {
        console.log("posBuddyId is to old");
        this.posBuddyId = UNKNOWN_ID;
        return false;
      }
    }
    this.posBuddyId = localPosBuddyId;
    return true;
  }

  setLocalIdentity(posBuddyId: string) {
    console.log("set local id to :" + posBuddyId)
    localStorage.setItem('posBuddyId', posBuddyId)
    localStorage.setItem('posBuddyIdTimestamp', Date.now().toString())
  }

  unSetLocalIdentity() {
    localStorage.removeItem('posBuddyId');
    localStorage.removeItem('posBuddyIdTimestamp');
  }

  protected readonly UNKNOWN_ID = UNKNOWN_ID;
}

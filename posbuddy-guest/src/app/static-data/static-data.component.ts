import {AfterViewInit, Component, inject, TemplateRef, ViewChild} from '@angular/core';
import {DatePipe, DecimalPipe, NgForOf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {staticIdData, StaticIdService, UNKNOWN_ID} from "../services/static-id.service";
import {
  NgbAlert,
  NgbDropdown,
  NgbDropdownButtonItem,
  NgbDropdownItem,
  NgbDropdownMenu,
  NgbDropdownToggle,
  NgbOffcanvas,
  NgbPopover
} from "@ng-bootstrap/ng-bootstrap";
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
    ZXingScannerModule,
    NgbPopover,
    NgbDropdown,
    NgbDropdownMenu,
    NgbDropdownButtonItem,
    NgbDropdownToggle,
    NgbDropdownItem,
    DatePipe,
    NgbPopover
  ],
  templateUrl: './static-data.component.html',
  styleUrl: './static-data.component.css'
})

export class StaticDataComponent implements AfterViewInit {
  @ViewChild('scanIdOC') scanIdOCTemplate: TemplateRef<any> | undefined;
  private offcanvasService = inject(NgbOffcanvas);
  protected actPosBuddyId: string = UNKNOWN_ID;
  protected readonly UNKNOWN_ID = UNKNOWN_ID;

  availableDevices?: MediaDeviceInfo[];
  deviceCurrent?: MediaDeviceInfo;
  deviceSelected?: string;
  hasDevices?: boolean;

  dateFilter = "1d"

  serverResponse: string = "";
  staticData: staticIdData = {
    posBuddyId: "",
    syncTimeStamp: "",
    balance: 0,
    revenueList: [{action: "", amount: 0, itemText: "", timeOfAction: "", value: 0}]
  }

  constructor(private staticIdService: StaticIdService) {
  }

  revenueDateFilter(range: string) {
    console.debug("date range:" + range)
    this.dateFilter = range;
    this.reloadData();
  }

  /**
   * filters all revenues in Date range
   * @param revenueDate
   */
  isInDateFilter(revenueDateString: string): boolean {
    let revenueDate = new Date(Date.parse(revenueDateString));
    switch (this.dateFilter) {
      case "ALL":
        return false;
        break;
      case "1d":
        if (revenueDate > this.subDate(1)) {
          return true;
        }
        return false;
        break;
      case "1w":
        if (revenueDate > this.subDate(7)) {
          return true;
        }
        return false;
        break;
      case "1m":
        if (revenueDate > this.subDate(31)) {
          return true;
        }
        return false;
        break;
      case "1y":
        if (revenueDate > this.subDate(365)) {
          return true;
        }
        return false;
        break;
    }
    return true;
  }

  subDate(days: number): Date {
    let date = new Date();
    date.setDate(date.getDate() - days);
    return date;
  }

  onDeviceSelectChange(selected: string) {
    console.debug("Camera:" + selected)
    const selectedStr = selected || '';
    if (this.deviceSelected === selectedStr) {
      return;
    }
    this.deviceSelected = selectedStr;
    // @ts-ignore
    const device = this.availableDevices.find(x => x.deviceId === selected);
    this.deviceCurrent = device || undefined;
  }

  onDeviceChange(device: MediaDeviceInfo) {
    const selectedStr = device?.deviceId || '';
    if (this.deviceSelected === selectedStr) {
      return;
    }
    this.deviceSelected = selectedStr;
    this.deviceCurrent = device || undefined;
  }

  onCamerasFound(devices: MediaDeviceInfo[]): void {
    this.availableDevices = devices;
    this.hasDevices = Boolean(devices && devices.length);
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

  reloadData() {
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

  protected readonly Date = Date;
}

import {Component, TemplateRef} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {inject} from "@angular/core/testing";
import {NgbOffcanvas, OffcanvasDismissReasons} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-allocate-id',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './allocate-id.component.html',
  styleUrl: './allocate-id.component.css'
})
export class AllocateIdComponent {
  // @ts-ignore
  private offcanvasService = inject(NgbOffcanvas);
  closeResult = '';

  posBuddyId: string = "-";
  surname = '';
  lastname = '';
  birthday = '08.03.1975';
  attribute1 = '';
  attribute2 = '';
  attribute3 = '';
  balance = '0'


  scanQRCode(content: TemplateRef<any>) {
    //@ts-ignore
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'}).result.then(
      //@ts-ignore
      (result) => {
        this.closeResult = `Closed with: ${result}`;
      },
      //@ts-ignore
      (reason) => {
        this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
      },
    );
  }

  private getDismissReason(reason: any): string {
    switch (reason) {
      case OffcanvasDismissReasons.ESC:
        return 'by pressing ESC';
      case OffcanvasDismissReasons.BACKDROP_CLICK:
        return 'by clicking on the backdrop';
      default:
        return `with: ${reason}`;
    }
  }

}

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DispensingStationFilterComponent } from './dispensing-station-filter.component';

describe('DispensingStationFilterComponent', () => {
  let component: DispensingStationFilterComponent;
  let fixture: ComponentFixture<DispensingStationFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DispensingStationFilterComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DispensingStationFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

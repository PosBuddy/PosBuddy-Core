import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StaticDataComponent } from './static-data.component';

describe('StaticDataComponent', () => {
  let component: StaticDataComponent;
  let fixture: ComponentFixture<StaticDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StaticDataComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StaticDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpecialTransactionComponent } from './special-transaction.component';

describe('SpecialTransactionComponent', () => {
  let component: SpecialTransactionComponent;
  let fixture: ComponentFixture<SpecialTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SpecialTransactionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SpecialTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

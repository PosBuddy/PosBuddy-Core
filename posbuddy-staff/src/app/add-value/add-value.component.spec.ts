import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddValueComponent } from './add-value.component';

describe('AddValueComponent', () => {
  let component: AddValueComponent;
  let fixture: ComponentFixture<AddValueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddValueComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddValueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

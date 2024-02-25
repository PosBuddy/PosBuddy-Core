import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeallocateComponent } from './deallocate.component';

describe('DeallocateComponent', () => {
  let component: DeallocateComponent;
  let fixture: ComponentFixture<DeallocateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeallocateComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DeallocateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

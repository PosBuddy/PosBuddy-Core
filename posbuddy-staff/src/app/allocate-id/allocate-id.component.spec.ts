import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllocateIdComponent } from './allocate-id.component';

describe('AllocateIdComponent', () => {
  let component: AllocateIdComponent;
  let fixture: ComponentFixture<AllocateIdComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AllocateIdComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AllocateIdComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

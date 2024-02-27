import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServeComponent } from './serve.component';

describe('ServeComponent', () => {
  let component: ServeComponent;
  let fixture: ComponentFixture<ServeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ServeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

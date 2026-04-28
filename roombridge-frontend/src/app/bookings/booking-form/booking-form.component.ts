import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Room, AppUser } from '../../shared/models/models';
import { BookingService } from '../../shared/services/booking.service';
import { RoomService } from '../../shared/services/room.service';
import { UserService } from '../../shared/services/user.service';

function futureDateValidator(control: any) {
  if (!control.value) return null;
  return new Date(control.value) > new Date() ? null : { futureDate: true };
}

@Component({
  selector: 'app-booking-form',
  templateUrl: './booking-form.component.html',
  styleUrls: ['./booking-form.component.scss']
})
export class BookingFormComponent implements OnInit {
  form!: FormGroup;
  isEdit = false;
  bookingId?: number;
  rooms: Room[] = [];
  users: AppUser[] = [];
  loading = false;
  error = '';
  today = new Date().toISOString().split('T')[0];

  constructor(
    private fb: FormBuilder,
    private bookingService: BookingService,
    private roomService: RoomService,
    private userService: UserService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      roomId:       [null, Validators.required],
      userId:       [null, Validators.required],
      checkInDate:  ['', [Validators.required, futureDateValidator]],
      checkOutDate: ['', [Validators.required, futureDateValidator]],
    }, { validators: this.dateRangeValidator });

    this.roomService.getAll(0, 100).subscribe({ next: p => this.rooms = p.content });
    this.userService.getAll().subscribe({ next: u => this.users = u });

    this.bookingId = Number(this.route.snapshot.paramMap.get('id')) || undefined;
    if (this.bookingId) {
      this.isEdit = true;
      this.bookingService.getById(this.bookingId).subscribe({
        next: b => this.form.patchValue(b),
        error: () => this.error = 'Failed to load booking'
      });
    }
  }

  dateRangeValidator(g: FormGroup) {
    const ci = g.get('checkInDate')?.value;
    const co = g.get('checkOutDate')?.value;
    if (ci && co && co <= ci) return { dateRange: true };
    return null;
  }

  get f() { return this.form.controls; }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    this.error = '';
    const req = this.isEdit
      ? this.bookingService.update(this.bookingId!, this.form.value)
      : this.bookingService.create(this.form.value);

    req.subscribe({
      next: () => this.router.navigate(['/bookings']),
      error: err => { this.error = err.error?.message || 'Save failed'; this.loading = false; }
    });
  }
}

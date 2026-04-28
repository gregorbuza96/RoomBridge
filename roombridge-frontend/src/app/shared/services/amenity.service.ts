import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Amenity } from '../models/models';

@Injectable({ providedIn: 'root' })
export class AmenityService {
  private readonly API = 'http://localhost:8080/api/amenities';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Amenity[]> {
    return this.http.get<Amenity[]>(this.API);
  }

  create(amenity: Amenity): Observable<Amenity> {
    return this.http.post<Amenity>(this.API, amenity);
  }

  update(id: number, amenity: Amenity): Observable<Amenity> {
    return this.http.put<Amenity>(`${this.API}/${id}`, amenity);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API}/${id}`);
  }
}

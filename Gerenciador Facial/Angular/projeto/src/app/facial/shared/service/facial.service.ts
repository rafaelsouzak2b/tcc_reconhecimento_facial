import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FacialService {

  constructor(private http: HttpClient) { }
  getFaces(){
    return this.http.get<any>("http://localhost:5454/api/v1/faces");
  }
  removeFace(identification:string){
    const httpOptions = {
      headers: new HttpHeaders({ 'Content-Type': 'application/json' }), body: {id:identification}
  };
    return this.http.delete("http://localhost:5454/api/v1/face", httpOptions);
  }
}

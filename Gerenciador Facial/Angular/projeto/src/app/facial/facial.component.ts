import { Component, OnInit } from '@angular/core';
import { IRetorno } from './shared/model/facial.model';
import { FacialService } from './shared/service/facial.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SelectionModel } from '@angular/cdk/collections';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-facial',
  templateUrl: './facial.component.html',
  styleUrls: ['./facial.component.scss']
})
export class FacialComponent implements OnInit {
  displayedColumns: string[] = ['select', 'name', 'identification'];
  dataSource = new MatTableDataSource<IRetorno>();
  selection = new SelectionModel<IRetorno>(true, []);


  constructor(private facialService: FacialService) { }
  ngOnInit(): void {
    this.buscaFaces();
  }

  buscaFaces() {
    this.dataSource = new MatTableDataSource<IRetorno>();
    this.dataSource = new MatTableDataSource<IRetorno>();
    this.facialService.getFaces().subscribe(retorno => this.dataSource = new MatTableDataSource<IRetorno>(retorno.result),
      erro => console.log("Erro ao buscar as faces")
    )
    this.selection = new SelectionModel<IRetorno>(true, []);
  }

  removeFace() {
    this.selection.selected.forEach((val) => {
      this.facialService.removeFace(val.identification).subscribe(success => ()=>{console.log("Ok"); this.buscaFaces();},
        erro => console.log("Erro ao deletar a(s) face(s)")
      )
    });
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }
  masterToggle() {
    if (this.isAllSelected()) {
      this.selection.clear();
      return;
    }

    this.selection.select(...this.dataSource.data);
  }

  /*checkboxLabel(row?: FacialModule): string {
    if (!row) {
      return `${this.isAllSelected() ? 'deselect' : 'select'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.position + 1}`;
  }*/


}

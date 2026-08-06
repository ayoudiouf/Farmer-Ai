import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-a-propos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './a-propos.component.html',
  styleUrl: './a-propos.component.css'
})
export class AProposComponent {}

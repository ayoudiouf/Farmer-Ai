import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-conditions',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './conditions.component.html',
  styleUrl: './conditions.component.css'
})
export class ConditionsComponent {}

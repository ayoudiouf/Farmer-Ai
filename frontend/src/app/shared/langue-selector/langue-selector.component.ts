import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LangueService } from '../../core/services/langue.service';

@Component({
  selector: 'app-langue-selector',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './langue-selector.component.html',
  styleUrl: './langue-selector.component.css'
})
export class LangueSelectorComponent {
  constructor(public langueService: LangueService) {}
}

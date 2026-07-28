import { Component, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DiagnosticService, Diagnostic } from '../../core/services/diagnostic.service';
import { LangueService } from '../../core/services/langue.service';
import { LangueSelectorComponent } from '../../shared/langue-selector/langue-selector.component';

@Component({
  selector: 'app-diagnostic',
  standalone: true,
  imports: [CommonModule, FormsModule, LangueSelectorComponent],
  templateUrl: './diagnostic.component.html',
  styleUrl: './diagnostic.component.css'
})
export class DiagnosticComponent {
  @ViewChild('video') videoRef!: ElementRef<HTMLVideoElement>;
  @ViewChild('canvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  culture = '';
  photoSelectionnee: File | null = null;
  apercuUrl: string | null = null;
  resultat: Diagnostic | null = null;
  chargement = false;

  cameraActive = false;
  erreurCamera = '';
  private streamCamera: MediaStream | null = null;

  constructor(
    private diagnosticService: DiagnosticService,
    public langueService: LangueService
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.arreterCamera();
      this.photoSelectionnee = input.files[0];
      this.apercuUrl = URL.createObjectURL(this.photoSelectionnee);
      this.resultat = null;
    }
  }

  async demarrerCamera(): Promise<void> {
    this.erreurCamera = '';
    this.resultat = null;
    try {
      this.streamCamera = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment' }
      });
      this.cameraActive = true;
      setTimeout(() => {
        if (this.videoRef) {
          this.videoRef.nativeElement.srcObject = this.streamCamera;
        }
      });
    } catch (err) {
      this.erreurCamera = this.langueService.t('erreur_camera');
    }
  }

  capturerPhoto(): void {
    const video = this.videoRef.nativeElement;
    const canvas = this.canvasRef.nativeElement;
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
    canvas.toBlob((blob) => {
      if (blob) {
        this.photoSelectionnee = new File([blob], `photo-${Date.now()}.jpg`, { type: 'image/jpeg' });
        this.apercuUrl = URL.createObjectURL(blob);
      }
      this.arreterCamera();
    }, 'image/jpeg', 0.9);
  }

  arreterCamera(): void {
    if (this.streamCamera) {
      this.streamCamera.getTracks().forEach((track) => track.stop());
      this.streamCamera = null;
    }
    this.cameraActive = false;
  }

 erreurAnalyse: string | null = null;

analyser(): void {
  if (!this.photoSelectionnee || !this.culture) return;
  this.chargement = true;
  this.erreurAnalyse = null;
  this.resultat = null;

  this.diagnosticService.analyserPhoto(this.photoSelectionnee, this.culture, 1).subscribe({
    next: (res) => {
      this.chargement = false;
      this.resultat = res;
    },
    error: (err) => {
      this.chargement = false;
      if (err.status === 422 && err.error?.code === 'PHOTO_INVALIDE') {
        this.erreurAnalyse = err.error.message;
      } else {
        this.erreurAnalyse = this.langueService.t('erreur_analyse_generique');
      }
    }
  });
}

  ngOnDestroy(): void {
    this.arreterCamera();
  }
}

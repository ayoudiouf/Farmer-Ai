import { Injectable, signal } from '@angular/core';

export type Langue = 'FRANCAIS' | 'WOLOF' | 'DIOLA' | 'SERERE';

type Dictionnaire = Record<string, string>;

const TRADUCTIONS: Record<Langue, Dictionnaire> = {
  FRANCAIS: {
    connexion_titre: 'FarmerAI — Connexion',
    telephone: 'Téléphone',
    mot_de_passe: 'Mot de passe',
    se_connecter: 'Se connecter',
    erreur_login: 'Numéro ou mot de passe incorrect.',
    erreur_inscription: 'Un compte existe déjà avec ce numéro.',
    pas_de_compte: "Pas encore de compte ?",
    creer_compte: 'Créer un compte',
    inscription_titre: 'FarmerAI — Inscription',
    nom_complet: 'Nom complet',
    region: 'Région',
    langue_preferee: 'Langue préférée',
    deja_compte: 'Déjà un compte ?',
    sinscrire: "S'inscrire",
    tableau_de_bord: 'Tableau de bord FarmerAI',
    nouveau_diagnostic: '+ Nouveau diagnostic photo',
    poser_question: 'Poser une question agronomique',
    placeholder_question: 'ex: quand semer le mil ?',
    demander_conseil: 'Demander conseil',
    recherche_en_cours: 'Recherche...',
    sources: 'Sources',
    historique_titre: 'Historique des diagnostics',
    chargement: 'Chargement...',
    aucun_diagnostic: "Aucun diagnostic pour l'instant.",
    confiance: 'confiance',
    diagnostic_titre: 'Diagnostic de maladie par photo',
    culture_concernee: 'Culture concernée',
    placeholder_culture: 'ex: mil, arachide',
    photo_plante: 'Photo de la plante',
    prendre_photo: 'Prendre une photo',
    choisir_fichier: 'Choisir un fichier',
    analyser_photo: 'Analyser la photo',
    analyse_en_cours: 'Analyse en cours...',
    resultat: 'Résultat',
    maladie_detectee: 'Maladie détectée',
    recommandation: 'Recommandation',
    deconnexion: 'Déconnexion',
    erreur_camera: "Impossible d'accéder à la caméra."
  },
  WOLOF: {
    connexion_titre: 'FarmerAI — Dugg',
    telephone: 'Nimero telefon',
    mot_de_passe: 'Baatub tëriim',
    se_connecter: 'Dugg',
    erreur_login: 'Nimero mba baatub tëriim baaxul.',
    erreur_inscription: 'Am na benn compte ak nimero bi.',
    pas_de_compte: 'Amuloo compte?',
    creer_compte: 'Sos compte',
    inscription_titre: 'FarmerAI — Bindu',
    nom_complet: 'Tur wu mat',
    region: 'Diiwaan',
    langue_preferee: 'Làkk bu la neex',
    deja_compte: 'Am nga compte ba noppi?',
    sinscrire: 'Bindu',
    tableau_de_bord: 'Xët bu njëkk FarmerAI',
    nouveau_diagnostic: '+ Diagnostik bu bees ak nataal',
    poser_question: 'Laaj ci mbay',
    placeholder_question: 'misaal: kañ lañu jiy dugub?',
    demander_conseil: 'Laaj xalaat',
    recherche_en_cours: 'Di seet...',
    sources: 'Ci kaw',
    historique_titre: 'Diagnostik yi jot na',
    chargement: 'Di yeb...',
    aucun_diagnostic: 'Amul benn diagnostik lekk.',
    confiance: 'wóolu',
    diagnostic_titre: 'Diagnostik feebar ci nataal',
    culture_concernee: 'Mbay mi',
    placeholder_culture: 'misaal: dugub, gerte',
    photo_plante: 'Nataalu garab gi',
    prendre_photo: 'Jël nataal',
    choisir_fichier: 'Tann fichier',
    analyser_photo: 'Analize nataal bi',
    analyse_en_cours: 'Di analize...',
    resultat: 'Njort',
    maladie_detectee: 'Feebar bi gis',
    recommandation: 'Digal',
    deconnexion: 'Génn',
    erreur_camera: 'Mënuma jëfandikoo kamera bi.'
  },
  DIOLA: {
    connexion_titre: 'FarmerAI — Ehul',
    telephone: 'Nimero telefon',
    mot_de_passe: 'Assiŋ',
    se_connecter: 'Ehul',
    erreur_login: 'Nimero mba assiŋ baaxatul.',
    erreur_inscription: 'Ibaje compte ku nimero.',
    pas_de_compte: 'Ibaje compte?',
    creer_compte: 'Ussum compte',
    inscription_titre: 'FarmerAI — Essum',
    nom_complet: 'Kanak kayay',
    region: 'Kal',
    langue_preferee: 'Kalak kabaje',
    deja_compte: 'Ubaje na compte?',
    sinscrire: 'Essum',
    tableau_de_bord: 'Kalak kajaŋut FarmerAI',
    nouveau_diagnostic: '+ Essum diagnostik ku nataal',
    poser_question: 'Kaañ kalak',
    placeholder_question: 'ubu: eleŋ bujañ mil?',
    demander_conseil: 'Kaañ xalaat',
    recherche_en_cours: 'Ejaŋut...',
    sources: 'Kal kaje',
    historique_titre: 'Diagnostik ikatiye',
    chargement: 'Ejaŋut...',
    aucun_diagnostic: 'Ibaje diagnostik.',
    confiance: 'essuu',
    diagnostic_titre: 'Diagnostik ku bacar ku nataal',
    culture_concernee: 'Bugañ',
    placeholder_culture: 'ubu: mil, gerte',
    photo_plante: 'Nataal ku garab',
    prendre_photo: 'Emat nataal',
    choisir_fichier: 'Essobe fichier',
    analyser_photo: 'Analize nataal',
    analyse_en_cours: 'Ejaŋut analize...',
    resultat: 'Njort',
    maladie_detectee: 'Bacar ejaŋu',
    recommandation: 'Digal',
    deconnexion: 'Efit',
    erreur_camera: 'Ibaje kamera essobe.'
  },
  SERERE: {
    connexion_titre: 'FarmerAI — Duge',
    telephone: 'Nimero telefon',
    mot_de_passe: 'Baat u f省',
    se_connecter: 'Duge',
    erreur_login: 'Nimero walla baat baaxul.',
    erreur_inscription: 'O am na compte ak nimero.',
    pas_de_compte: 'O am o compte?',
    creer_compte: 'Sos compte',
    inscription_titre: 'FarmerAI — Bind',
    nom_complet: 'Tur o mat',
    region: 'Diiwaan',
    langue_preferee: 'Lakk o neex',
    deja_compte: 'O am na compte?',
    sinscrire: 'Bind',
    tableau_de_bord: 'Xet o njek FarmerAI',
    nouveau_diagnostic: '+ Diagnostik o bees ak nataal',
    poser_question: 'Laj ci mbay',
    placeholder_question: 'misaal: kan lanu jiy dugub?',
    demander_conseil: 'Laj xalaat',
    recherche_en_cours: 'Di sit...',
    sources: 'Ci kaw',
    historique_titre: 'Diagnostik yi am na',
    chargement: 'Di yeb...',
    aucun_diagnostic: 'Amul diagnostik.',
    confiance: 'wool',
    diagnostic_titre: 'Diagnostik bacar ci nataal',
    culture_concernee: 'Mbay',
    placeholder_culture: 'misaal: dugub, gerte',
    photo_plante: 'Nataal o garab',
    prendre_photo: 'Jel nataal',
    choisir_fichier: 'Tan fichier',
    analyser_photo: 'Analiz nataal',
    analyse_en_cours: 'Di analiz...',
    resultat: 'Njort',
    maladie_detectee: 'Bacar o gis',
    recommandation: 'Digal',
    deconnexion: 'Gen',
    erreur_camera: 'O am na kamera essobe.'
  }
};

const LABELS_LANGUE: Record<Langue, string> = {
  FRANCAIS: 'Français',
  WOLOF: 'Wolof',
  DIOLA: 'Diola',
  SERERE: 'Sérère'
};

@Injectable({ providedIn: 'root' })
export class LangueService {
  private readonly storageKey = 'farmerai_langue';
  langueActuelle = signal<Langue>(this.chargerLangueInitiale());

  readonly languesDisponibles: { code: Langue; label: string }[] = (
    Object.keys(LABELS_LANGUE) as Langue[]
  ).map((code) => ({ code, label: LABELS_LANGUE[code] }));

  private chargerLangueInitiale(): Langue {
    const sauvegardee = localStorage.getItem(this.storageKey) as Langue | null;
    return sauvegardee && TRADUCTIONS[sauvegardee] ? sauvegardee : 'FRANCAIS';
  }

  changerLangue(langue: Langue): void {
    this.langueActuelle.set(langue);
    localStorage.setItem(this.storageKey, langue);
  }

  t(cle: string): string {
    const dict = TRADUCTIONS[this.langueActuelle()];
    return dict[cle] ?? cle;
  }
}




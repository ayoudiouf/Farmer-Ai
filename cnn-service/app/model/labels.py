"""
Classes de maladies supportées et recommandations agronomiques associées.
À enrichir avec les fiches INERA/FAO au fur et à mesure (source du RAG).
Basé sur les cultures prioritaires du pitch FarmerAI : mil, arachide, tomate, manioc.
"""

CLASSES = [
    "sain",
    "mil_mildiou",
    "arachide_cercosporiose",
    "arachide_rouille",
    "tomate_mildiou",
    "tomate_fletrissement_bacterien",
    "manioc_mosaique",
]

RECOMMANDATIONS = {
    "sain": "Aucune maladie détectée. Continuez la surveillance régulière de vos plants.",
    "mil_mildiou": (
        "Mildiou du mil détecté. Retirez et détruisez les plants atteints. "
        "Évitez les semis trop denses et privilégiez des variétés résistantes lors du prochain cycle."
    ),
    "arachide_cercosporiose": (
        "Cercosporiose de l'arachide détectée. Appliquez un fongicide homologué si l'infestation "
        "dépasse 10% des feuilles, et pratiquez la rotation des cultures l'année suivante."
    ),
    "arachide_rouille": (
        "Rouille de l'arachide détectée. Surveillez l'humidité du champ et espacez davantage les plants "
        "lors du prochain semis pour améliorer l'aération."
    ),
    "tomate_mildiou": (
        "Mildiou de la tomate détecté. Retirez les feuilles atteintes, évitez l'arrosage par aspersion "
        "en fin de journée, et traitez avec un fongicide à base de cuivre si disponible."
    ),
    "tomate_fletrissement_bacterien": (
        "Flétrissement bactérien détecté. Arrachez immédiatement les plants atteints pour limiter la "
        "propagation ; désinfectez vos outils avant de passer à un autre plant."
    ),
    "manioc_mosaique": (
        "Mosaïque du manioc détectée (virus transmis par mouche blanche). Utilisez des boutures saines "
        "pour la prochaine plantation et éliminez les plants fortement atteints."
    ),
}

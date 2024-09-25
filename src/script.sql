CREATE TABLE clients
(
    id                SERIAL PRIMARY KEY,
    nom               VARCHAR(255) NOT NULL,
    adresse           TEXT         NOT NULL,
    telephone         VARCHAR(20)  NOT NULL,
    est_professionnel BOOLEAN      NOT NULL
);

CREATE TYPE etatProjet AS ENUM ('ENCOURS', 'TERMINE', 'ANNULE');

CREATE TABLE projets (
                         id SERIAL PRIMARY KEY,
                         nom_projet VARCHAR(255) NOT NULL,
                         marge_beneficiaire DECIMAL(5,2) NOT NULL,
                         cout_total DECIMAL(12,2) NOT NULL,
                         etat_projet etatProjet,
                         client_id INTEGER,
                         FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE composants (
                            id SERIAL PRIMARY KEY,
                            nom VARCHAR(255) NOT NULL,
                            type_composant VARCHAR(50) NOT NULL,
                            taux_tva DECIMAL(5,2) NOT NULL,
                            projet_id INTEGER,
                            FOREIGN KEY (projet_id) REFERENCES projets(id) ON DELETE CASCADE

);

CREATE TABLE materiaux (
                           cout_unitaire DECIMAL(10,2) NOT NULL,
                           quantite DECIMAL(10,2) NOT NULL,
                           cout_transport DECIMAL(10,2) NOT NULL,
                           coefficient_qualite DECIMAL(3,2) NOT NULL
) INHERITS (composants);

CREATE TABLE mainOeuvres (
                             taux_horaire DECIMAL(10,2) NOT NULL,
                             heures_travail DECIMAL(10,2) NOT NULL,
                             productivite_ouvrier DECIMAL(3,2) NOT NULL
) INHERITS (composants);

CREATE TABLE devis (
                       id SERIAL PRIMARY KEY,
                       montant_estime DECIMAL(12,2) NOT NULL,
                       date_emission DATE NOT NULL,
                       date_validite DATE NOT NULL,
                       accepte BOOLEAN NOT NULL,
                       projet_id INTEGER REFERENCES projets(id)
);
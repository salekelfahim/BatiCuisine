package service;


import domain.Projet;
import repository.interfaces.IProjetRepository;

import java.util.List;
import java.util.Optional;

public class ProjetService {
    private final IProjetRepository projetRepository;

    public ProjetService(IProjetRepository projetRepository) {
        this.projetRepository = projetRepository;
    }

    public Projet createProjet(Projet projet) {
        return projetRepository.save(projet);
    }

    public Optional<Projet> getProjetById(Long id) {
        return projetRepository.findById(id);
    }

    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    public Projet updateProjet(Projet projet) {
        return projetRepository.update(projet);
    }

    public void deleteProjet(Long id) {
        projetRepository.delete(id);
    }

    public List<Projet> getProjetsByClientId(Long clientId) {
        return projetRepository.findByClientId(clientId);
    }
}


package service;

import domain.Composant;
import repository.interfaces.IComposantRepository;

import java.util.List;
import java.util.Optional;

public class ComposantService {
    private final IComposantRepository composantRepository;

    public ComposantService(IComposantRepository composantRepository) {
        this.composantRepository = composantRepository;
    }

    public Composant createComposant(Composant composant) {
        return composantRepository.save(composant);
    }

    public Optional<Composant> getComposantById(Long id) {
        return composantRepository.findById(id);
    }

    public List<Composant> getAllComposants() {
        return composantRepository.findAll();
    }

    public List<Composant> getComposantsByProjetId(Long projetId) {
        return composantRepository.findByProjetId(projetId);
    }

    public Composant updateComposant(Composant composant) {
        return composantRepository.update(composant);
    }

    public void deleteComposant(Long id) {
        composantRepository.delete(id);
    }
}

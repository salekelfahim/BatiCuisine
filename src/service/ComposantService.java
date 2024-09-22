package service;

import domain.Composant;
import domain.Materiau;
import domain.MainOeuvre;
import repository.interfaces.IComposantRepository;

import java.util.List;

public class ComposantService {
    private final IComposantRepository composantRepository;

    public ComposantService(IComposantRepository composantRepository) {
        this.composantRepository = composantRepository;
    }

    public void addComposant(Composant composant) {
        composantRepository.addComposant(composant);
    }

    public List<Composant> getAllComposants() {
        return composantRepository.getAllComposants();
    }

    public Composant getComposantById(Long id) {
        return composantRepository.getComposantById(id);
    }

    public void updateComposant(Composant composant) {
        composantRepository.updateComposant(composant);
    }

    public void deleteComposant(Long id) {
        composantRepository.deleteComposant(id);
    }

    public List<Materiau> getAllMateriaux() {
        return composantRepository.getAllMateriaux();
    }

    public List<MainOeuvre> getAllMainOeuvres() {
        return composantRepository.getAllMainOeuvres();
    }
}
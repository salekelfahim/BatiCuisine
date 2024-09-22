package service;

import domain.Devis;
import repository.interfaces.IDevisRepository;

import java.util.List;
import java.util.Optional;

public class DevisService {
    private final IDevisRepository devisRepository;

    public DevisService(IDevisRepository devisRepository) {
        this.devisRepository = devisRepository;
    }

    public Devis createDevis(Devis devis) {
        return devisRepository.save(devis);
    }

    public Optional<Devis> getDevisById(Long id) {
        return devisRepository.findById(id);
    }

    public List<Devis> getAllDevis() {
        return devisRepository.findAll();
    }

    public Devis updateDevis(Devis devis) {
        return devisRepository.update(devis);
    }

    public void deleteDevis(Long id) {
        devisRepository.delete(id);
    }

    public List<Devis> getDevisByProjetId(Long projetId) {
        return devisRepository.findByProjetId(projetId);
    }
}
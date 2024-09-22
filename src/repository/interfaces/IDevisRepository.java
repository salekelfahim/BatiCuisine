package repository.interfaces;

import domain.Devis;

import java.util.List;
import java.util.Optional;

public interface IDevisRepository {
    Devis save(Devis devis);
    Optional<Devis> findById(Long id);
    List<Devis> findAll();
    Devis update(Devis devis);
    void delete(Long id);
    List<Devis> findByProjetId(Long projetId);
}
package repository.interfaces;

import domain.Composant;

import java.util.List;
import java.util.Optional;

public interface IComposantRepository {
    Composant save(Composant composant);
    Optional<Composant> findById(Long id);
    List<Composant> findAll();
    List<Composant> findByProjetId(Long projetId);
    Composant update(Composant composant);
    void delete(Long id);
}

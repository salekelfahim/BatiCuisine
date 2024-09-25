package repository.interfaces;


import domain.Projet;

import java.util.List;
import java.util.Optional;

public interface IProjetRepository {
    Projet save(Projet projet);
    Optional<Projet> findById(Long id);
    List<Projet> findAll();
    Projet update(Projet projet);
    void delete(Long id);
    List<Projet> findByClientId(Long clientId);
}

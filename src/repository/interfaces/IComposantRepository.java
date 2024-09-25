package repository.interfaces;

import domain.Composant;
import domain.Materiau;
import domain.MainOeuvre;

import java.util.List;

public interface IComposantRepository {
    void addComposant(Composant composant);
    List<Composant> getAllComposants();
    Composant getComposantById(Long id);
    void updateComposant(Composant composant);
    void deleteComposant(Long id);

    List<Materiau> getAllMateriaux();
    List<MainOeuvre> getAllMainOeuvres();
}
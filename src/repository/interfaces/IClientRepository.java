package repository.interfaces;

import domain.Client;

import java.util.List;
import java.util.Optional;

public interface IClientRepository {
    void save(Client client);
    Optional<Client> findById(Long id);
    List<Client> findAll();
    void update(Client client);
    void delete(Long id);
}

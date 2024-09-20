package service;

import domain.Client;
import repository.interfaces.IClientRepository;

import java.util.List;
import java.util.Optional;

public class ClientService {
    private final IClientRepository clientRepository;

    public ClientService(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void save(Client client) {
        clientRepository.save(client);
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public void update(Client client) {
        clientRepository.update(client);
    }

    public void delete(Long id) {
        clientRepository.delete(id);
    }
}

import config.DataBaseConnection;
import repository.impl.ClientRepositoryImpl;
import repository.impl.ProjetRepositoryImpl;
import repository.impl.ComposantRepositoryImpl;
import repository.impl.DevisRepositoryImpl;
import repository.interfaces.IClientRepository;
import repository.interfaces.IProjetRepository;
import repository.interfaces.IComposantRepository;
import repository.interfaces.IDevisRepository;
import service.ClientService;
import service.ProjetService;
import service.ComposantService;
import service.DevisService;
import view.ConsoleView;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataBaseConnection.getInstance();

        IClientRepository clientRepository = new ClientRepositoryImpl();
        IProjetRepository projetRepository = new ProjetRepositoryImpl();
        IComposantRepository composantRepository = new ComposantRepositoryImpl();
        IDevisRepository devisRepository = new DevisRepositoryImpl();

        ClientService clientService = new ClientService(clientRepository);
        ProjetService projetService = new ProjetService(projetRepository);
        ComposantService composantService = new ComposantService(composantRepository);
        DevisService devisService = new DevisService(devisRepository);

        ConsoleView consoleView = new ConsoleView(clientService, projetService, composantService, devisService);

        consoleView.displayMenu();
    }
}
import config.DataBaseConnection;
import repository.impl.ClientRepositoryImpl;
import repository.interfaces.IClientRepository;
import service.ClientService;
import view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        DataBaseConnection.getInstance();

        IClientRepository clientRepository = new ClientRepositoryImpl();

        ClientService clientService = new ClientService(clientRepository);

        ConsoleView consoleView = new ConsoleView(clientService);

        consoleView.displayMenu();
    }
}
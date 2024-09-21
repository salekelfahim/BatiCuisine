package view;

import domain.Client;
import service.ClientService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleView {
    private final ClientService clientService;
    private final Scanner scanner;

    public ConsoleView(ClientService clientService) {
        this.clientService = clientService;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\n=== Client Management ===");
            System.out.println("1. Add a new client");
            System.out.println("2. Show all clients");
            System.out.println("3. Update a client");
            System.out.println("4. Delete a client");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addClient();
                    break;
                case 2:
                    showAllClients();
                    break;
                case 3:
                    updateClient();
                    break;
                case 4:
                    deleteClient();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void addClient() {
        System.out.println("\n=== Add a new client ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Is professional (true/false): ");
        boolean isProfessional = scanner.nextBoolean();

        Client newClient = new Client(name, address, phone, isProfessional);
        clientService.save(newClient);
        System.out.println("Client added successfully!");
    }

    private void showAllClients() {
        System.out.println("\n=== All Clients ===");
        List<Client> clients = clientService.findAll();
        if (clients.isEmpty()) {
            System.out.println("No clients found.");
        } else {
            for (Client client : clients) {
                System.out.println(client);
            }
        }
    }

    private void updateClient() {
        System.out.println("\n=== Update a client ===");
        System.out.print("Enter client ID to update: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Optional<Client> clientOpt = clientService.findById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            System.out.println("Current client details: " + client);

            System.out.print("Enter new name (or press enter to keep current): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) client.setNom(name);

            System.out.print("Enter new address (or press enter to keep current): ");
            String address = scanner.nextLine();
            if (!address.isEmpty()) client.setAdresse(address);

            System.out.print("Enter new phone (or press enter to keep current): ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) client.setTelephone(phone);

            System.out.print("Update professional status (true/false, or press enter to keep current): ");
            String isProfessionalStr = scanner.nextLine();
            if (!isProfessionalStr.isEmpty()) {
                client.setEstProfessionnel(Boolean.parseBoolean(isProfessionalStr));
            }

            clientService.update(client);
            System.out.println("Client updated successfully!");
        } else {
            System.out.println("Client not found.");
        }
    }

    private void deleteClient() {
        System.out.println("\n=== Delete a client ===");
        System.out.print("Enter client ID to delete: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        Optional<Client> clientOpt = clientService.findById(id);
        if (clientOpt.isPresent()) {
            System.out.println("Are you sure you want to delete this client? (y/n)");
            System.out.println(clientOpt.get());
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                clientService.delete(id);
                System.out.println("Client deleted successfully!");
            } else {
                System.out.println("Deletion cancelled.");
            }
        } else {
            System.out.println("Client not found.");
        }
    }
}

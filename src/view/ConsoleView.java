package view;

import domain.*;
import service.ClientService;
import service.ProjetService;
import service.ComposantService;
import service.DevisService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleView {
    private final ClientService clientService;
    private final ProjetService projetService;
    private final ComposantService composantService;
    private final DevisService devisService;
    private final Scanner scanner;

    public ConsoleView(ClientService clientService, ProjetService projetService, ComposantService composantService, DevisService devisService) {
        this.clientService = clientService;
        this.projetService = projetService;
        this.composantService = composantService;
        this.devisService = devisService;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() throws SQLException {
        while (true) {
            System.out.println("\n=== Management Menu ===");
            System.out.println("1. Add a new client");
            System.out.println("2. Show all clients");
            System.out.println("3. Find Client by Name");
            System.out.println("4. Update a client");
            System.out.println("5. Delete a client");
            System.out.println("6. Add a new project");
            System.out.println("7. Generate devis");
            System.out.println("8. Exit");
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
                    findClientByNom();
                    break;
                case 4:
                    updateClient();
                    break;
                case 5:
                    deleteClient();
                    break;
                case 6:
                    addProject();
                    break;
                case 7:
                    generateDevis();
                    break;
                case 8:
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
                System.out.printf("ID: %d, Nom: %s, Téléphone: %s, Adresse : %s , Pro: %s %n",
                        client.getId(), client.getNom(), client.getTelephone(),client.getAdresse(), client.isEstProfessionnel() ? "true" : "false");
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

    private void addProject() throws SQLException {
        System.out.println("\n=== Add a new project ===");
        Client client = findClientByNom();
        if (client == null) {
            System.out.println("Client not found. Project cannot be added.");
            return;
        }

        System.out.print("Enter project name: ");
        String nomProjet = scanner.nextLine();
        System.out.print("Enter profit margin: ");
        BigDecimal margeBeneficiaire = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.print("Enter project state (ENCOURS, TERMINE, ANNULE): ");
        String etatProjetStr = scanner.nextLine();
        EtatProjet etatProjet;
        try {
            etatProjet = EtatProjet.valueOf(etatProjetStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid project state. Please use a valid state (e.g., ENCOURS, TERMINE, ANNULE).");
            return;
        }

        Projet projet = new Projet(nomProjet, margeBeneficiaire, BigDecimal.ZERO, etatProjet, client);
        projetService.createProjet(projet);
        System.out.println("Project added successfully!");

        addComponent(projet);
    }

    private void  addComponent(Projet projet) {
        while (true) {
            System.out.println("\n=== Add a new component to project: " + projet.getNomProjet() + " ===");
            System.out.print("Enter component name: ");
            String nomComposant = scanner.nextLine();
            System.out.print("Enter component type (Materiau, MainOeuvre): ");
            String typeComposant = scanner.nextLine();
            System.out.print("Enter taux TVA: ");
            BigDecimal tauxTva = scanner.nextBigDecimal();
            scanner.nextLine();

            Composant composant;
            if (typeComposant.equalsIgnoreCase("Materiau")) {
                System.out.print("Enter cout unitaire: ");
                BigDecimal coutUnitaire = scanner.nextBigDecimal();
                System.out.print("Enter quantite: ");
                BigDecimal quantite = scanner.nextBigDecimal();
                System.out.print("Enter cout transport: ");
                BigDecimal coutTransport = scanner.nextBigDecimal();
                System.out.print("Enter coefficient qualite: ");
                BigDecimal coefficientQualite = scanner.nextBigDecimal();
                scanner.nextLine();
                composant = new Materiau(nomComposant, typeComposant, tauxTva, projet, coutUnitaire, quantite, coutTransport, coefficientQualite);
            } else if (typeComposant.equalsIgnoreCase("MainOeuvre")) {
                System.out.print("Enter taux horaire: ");
                BigDecimal tauxHoraire = scanner.nextBigDecimal();
                System.out.print("Enter heures travail: ");
                BigDecimal heuresTravail = scanner.nextBigDecimal();
                System.out.print("Enter productivite ouvrier: ");
                BigDecimal productiviteOuvrier = scanner.nextBigDecimal();
                scanner.nextLine();
                composant = new MainOeuvre(nomComposant, typeComposant, tauxTva, projet, tauxHoraire, heuresTravail, productiviteOuvrier);
            } else {
                System.out.println("Invalid component type. Component not added.");
                continue;
            }

            composantService.save(composant);
            System.out.println("Component added successfully!");

            System.out.print("Do you want to add another component to this project? (yes/no): ");
            String answer = scanner.nextLine();
            if (!answer.equalsIgnoreCase("yes")) {
                break;
            }
        }
        System.out.println("Returning to main menu.");
    }

    private void generateDevis () {
            System.out.println("\n=== Generate a quote (devis) ===");
            System.out.print("Enter project ID: ");
            Long projetId = scanner.nextLong();
            Projet projet = projetService.getProjetById(projetId).orElse(null);

            if (projet != null) {
                System.out.print("Enter estimated amount: ");
                BigDecimal montantEstime = scanner.nextBigDecimal();
                System.out.print("Enter emission date (YYYY-MM-DD): ");
                String dateEmissionStr = scanner.next();
                LocalDate dateEmission = LocalDate.parse(dateEmissionStr);
                System.out.print("Enter validity date (YYYY-MM-DD): ");
                String dateValiditeStr = scanner.next();
                LocalDate dateValidite = LocalDate.parse(dateValiditeStr);
                System.out.print("Is the quote accepted (true/false): ");
                boolean accepte = scanner.nextBoolean();

                Devis devis = new Devis(montantEstime, dateEmission, dateValidite, accepte, projet);
                devisService.createDevis(devis);
                System.out.println("Quote generated successfully!");
            } else {
                System.out.println("Project not found.");
            }
        }
    private Client findClientByNom() throws SQLException {
        System.out.println("\n=== Find Client by Name ===");
        System.out.print("Enter client name: ");
        String nom = scanner.nextLine();

        Optional<Client> clientOpt = clientService.findByNom(nom);
        if (clientOpt.isPresent()) {
            System.out.println("Client found: " + clientOpt.get());
            return clientOpt.get();
        } else {
            System.out.println("No client found with the name: " + nom);
            return null;
        }
    }
}

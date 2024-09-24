package view;

import domain.*;
import service.ClientService;
import service.ProjetService;
import service.ComposantService;
import service.DevisService;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            System.out.println("=== Main Menu ===");
            System.out.println("1. Create a new project");
            System.out.println("2. Show existing projects");
            System.out.println("3. Calculate project cost");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    newProjet();
                    break;
                case 2:
                    showAllProjects();
                    break;
                case 3:
                    generateDevis();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void newProjet() throws SQLException {
        System.out.println("\n--- Client Search ---");
        System.out.println("Would you like to search for an existing client or add a new one?");
        System.out.println("1. Search for existing client");
        System.out.println("2. Add a new client");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        Client client;
        if (choice == 1) {
            client = findClientByNom();
        } else {
            client = addClient();
        }

        if (client != null) {
            System.out.println("Continue with this client? (y/n): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                addProject(client);
            } else {
                System.out.println("Returning to main menu.");
            }
        }
    }

    private Client addClient() {
        System.out.println("\n=== Add a new client ===");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter address: ");
        String address = scanner.nextLine();
        System.out.print("Enter phone: ");
        String phone = scanner.nextLine();
        System.out.print("Is professional (true/false): ");
        boolean isProfessional = scanner.nextBoolean();
        scanner.nextLine();
        Client newClient = new Client(name, address, phone, isProfessional);
        clientService.save(newClient);
        System.out.println("Client added successfully!");
        return newClient;
    }

//    private void showAllClients() {
//        System.out.println("\n=== All Clients ===");
//        List<Client> clients = clientService.findAll();
//        if (clients.isEmpty()) {
//            System.out.println("No clients found.");
//        } else {
//            for (Client client : clients) {
//                System.out.printf("ID: %d, Nom: %s, Téléphone: %s, Adresse : %s , Pro: %s %n",
//                        client.getId(), client.getNom(), client.getTelephone(),client.getAdresse(), client.isEstProfessionnel() ? "true" : "false");
//            }
//        }
//    }

//    private void updateClient() {
//        System.out.println("\n=== Update a client ===");
//        System.out.print("Enter client ID to update: ");
//        Long id = scanner.nextLong();
//        scanner.nextLine();
//
//        Optional<Client> clientOpt = clientService.findById(id);
//        if (clientOpt.isPresent()) {
//            Client client = clientOpt.get();
//            System.out.println("Current client details: " + client);
//
//            System.out.print("Enter new name (or press enter to keep current): ");
//            String name = scanner.nextLine();
//            if (!name.isEmpty()) client.setNom(name);
//
//            System.out.print("Enter new address (or press enter to keep current): ");
//            String address = scanner.nextLine();
//            if (!address.isEmpty()) client.setAdresse(address);
//
//            System.out.print("Enter new phone (or press enter to keep current): ");
//            String phone = scanner.nextLine();
//            if (!phone.isEmpty()) client.setTelephone(phone);
//
//            System.out.print("Update professional status (true/false, or press enter to keep current): ");
//            String isProfessionalStr = scanner.nextLine();
//            if (!isProfessionalStr.isEmpty()) {
//                client.setEstProfessionnel(Boolean.parseBoolean(isProfessionalStr));
//            }
//
//            clientService.update(client);
//            System.out.println("Client updated successfully!");
//        } else {
//            System.out.println("Client not found.");
//        }
//    }

//    private void deleteClient() {
//        System.out.println("\n=== Delete a client ===");
//        System.out.print("Enter client ID to delete: ");
//        Long id = scanner.nextLong();
//        scanner.nextLine();
//
//        Optional<Client> clientOpt = clientService.findById(id);
//        if (clientOpt.isPresent()) {
//            System.out.println("Are you sure you want to delete this client? (y/n)");
//            System.out.println(clientOpt.get());
//            String confirm = scanner.nextLine();
//            if (confirm.equalsIgnoreCase("y")) {
//                clientService.delete(id);
//                System.out.println("Client deleted successfully!");
//            } else {
//                System.out.println("Deletion cancelled.");
//            }
//        } else {
//            System.out.println("Client not found.");
//        }
//    }

    private void addProject(Client client) throws SQLException {
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

    private void generateDevis() {
        System.out.println("\n=== Generate a devis ===");

        System.out.print("Enter project ID: ");
        Long projetId = scanner.nextLong();
        Projet projet = projetService.getProjetById(projetId).orElse(null);

        if (projet != null) {
            System.out.print("Enter emission date (YYYY-MM-DD): ");
            String dateEmissionStr = scanner.next();
            LocalDate dateEmission = LocalDate.parse(dateEmissionStr);

            System.out.print("Enter validity date (YYYY-MM-DD): ");
            String dateValiditeStr = scanner.next();
            LocalDate dateValidite = LocalDate.parse(dateValiditeStr);

            BigDecimal coutTotal = projet.getCoutTotal();
            BigDecimal margeBeneficiaire = projet.getMargeBeneficiaire();
            BigDecimal montantEstime = coutTotal.add(margeBeneficiaire);

            if (projet.getClient().isEstProfessionnel()){
                montantEstime = montantEstime.multiply(BigDecimal.valueOf(9)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
            }

            System.out.print("Is the devis accepted? (1 for yes, 0 for no): ");
            int choix = scanner.nextInt();
            boolean accepte = (choix == 1);

            Devis devis = new Devis(montantEstime, dateEmission, dateValidite, accepte, projet);
            devisService.createDevis(devis);

            System.out.println("Devis generated successfully!");
            System.out.println("Estimated amount: " + montantEstime);
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
            Client client = clientOpt.get();
            System.out.println("\nClient Details:");
            System.out.println("ID: " + client.getId());
            System.out.println("Name: " + client.getNom());
            System.out.println("Address: " + client.getAdresse());
            System.out.println("Phone: " + client.getTelephone());
            System.out.println("Is Professional: " + (client.isEstProfessionnel() ? "Yes" : "No"));
            return client;
        } else {
            System.out.println("No client found with the name: " + nom);
            return null;
        }
    }

    public void showAllProjects() {
        List<Projet> projects = projetService.getAllProjets();

        if (projects.isEmpty()) {
            System.out.println("No projects found.");
        } else {
            System.out.println("List of available projects:\n");
            System.out.println("-----------------------------------------------------------------");
            System.out.printf("| %-5s | %-20s | %-15s | %-15s |\n", "ID", "Project Name", "Margin", "Total Cost");
            System.out.println("-----------------------------------------------------------------");

            for (Projet project : projects) {
                System.out.printf("| %-5d | %-20s | %-15s | %-15s |\n",
                        project.getId(),
                        project.getNomProjet(),
                        project.getMargeBeneficiaire() != null ? project.getMargeBeneficiaire().toString() : "N/A",
                        project.getCoutTotal() != null ? project.getCoutTotal().toString() : "N/A"
                );
            }
            System.out.println("-----------------------------------------------------------------");
        }
    }
}

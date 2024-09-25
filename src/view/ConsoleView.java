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
import java.time.format.DateTimeParseException;
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

            int choice = getIntInput(1, 4);

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
            }
        }
    }

    private void newProjet() throws SQLException {
        System.out.println("\n--- Client Search ---");
        System.out.println("Would you like to search for an existing client or add a new one?");
        System.out.println("1. Search for existing client");
        System.out.println("2. Add a new client");
        System.out.print("Choose an option: ");
        int choice = getIntInput(1, 2);

        Client client;
        if (choice == 1) {
            client = findClientByNom();
        } else {
            client = addClient();
        }

        if (client != null) {
            System.out.println("Continue with this client? (y/n): ");
            String confirm = getStringInput();
            if (confirm.equalsIgnoreCase("y")) {
                addProject(client);
            } else {
                System.out.println("Returning to main menu.");
            }
        }
    }

    private Client addClient() {
        System.out.println("\n=== Add a new client ===");
        String name = getStringInput("Enter name: ");
        String address = getStringInput("Enter address: ");
        String phone = getStringInput("Enter phone: ");
        boolean isProfessional = getBooleanInput("Is professional (true/false): ");

        Client newClient = new Client(name, address, phone, isProfessional);
        clientService.save(newClient);
        System.out.println("Client added successfully!");
        return newClient;
    }

    private void addProject(Client client) throws SQLException {
        String nomProjet = getStringInput("Enter project name: ");
        BigDecimal margeBeneficiaire = getBigDecimalInput("Enter profit margin: ");
        EtatProjet etatProjet = getEtatProjet();

        Projet projet = new Projet(nomProjet, margeBeneficiaire, BigDecimal.ZERO, etatProjet, client);
        projetService.createProjet(projet);
        System.out.println("Project added successfully!");

        addComponent(projet);
    }

    private void addComponent(Projet projet) {
        while (true) {
            System.out.println("\n=== Add a new component to project: " + projet.getNomProjet() + " ===");
            String nomComposant = getStringInput("Enter component name: ");
            String typeComposant = getStringInput("Enter component type (Materiau, MainOeuvre): ");
            BigDecimal tauxTva = getBigDecimalInput("Enter taux TVA: ");

            Composant composant;
            if (typeComposant.equalsIgnoreCase("Materiau")) {
                BigDecimal coutUnitaire = getBigDecimalInput("Enter cout unitaire: ");
                BigDecimal quantite = getBigDecimalInput("Enter quantite: ");
                BigDecimal coutTransport = getBigDecimalInput("Enter cout transport: ");
                BigDecimal coefficientQualite = getBigDecimalInput("Enter coefficient qualite: ");
                composant = new Materiau(nomComposant, typeComposant, tauxTva, projet, coutUnitaire, quantite, coutTransport, coefficientQualite);
            } else if (typeComposant.equalsIgnoreCase("MainOeuvre")) {
                BigDecimal tauxHoraire = getBigDecimalInput("Enter taux horaire: ");
                BigDecimal heuresTravail = getBigDecimalInput("Enter heures travail: ");
                BigDecimal productiviteOuvrier = getBigDecimalInput("Enter productivite ouvrier: ");
                composant = new MainOeuvre(nomComposant, typeComposant, tauxTva, projet, tauxHoraire, heuresTravail, productiviteOuvrier);
            } else {
                System.out.println("Invalid component type. Component not added.");
                continue;
            }

            composantService.save(composant);
            System.out.println("Component added successfully!");

            if (!getBooleanInput("Do you want to add another component to this project? (true/false): ")) {
                break;
            }
        }
        System.out.println("Returning to main menu.");
    }

    private void generateDevis() {
        System.out.println("\n=== Generate a devis ===");

        Long projetId = getLongInput("Enter project ID: ");
        Projet projet = projetService.getProjetById(projetId).orElse(null);

        if (projet != null) {
            LocalDate dateEmission = getDateInput("Enter emission date (YYYY-MM-DD): ");
            LocalDate dateValidite;
            do {
                dateValidite = getDateInput("Enter validity date (YYYY-MM-DD): ");
                if (!dateValidite.isAfter(dateEmission)) {
                    System.out.println("Invalid date. The validity date must be after the emission date. Please try again.");
                }
            } while (!dateValidite.isAfter(dateEmission));

            BigDecimal coutTotal = projet.getCoutTotal();
            BigDecimal margeBeneficiaire = projet.getMargeBeneficiaire();
            BigDecimal montantEstime = coutTotal.add(margeBeneficiaire);

            if (projet.getClient().isEstProfessionnel()) {
                montantEstime = montantEstime.multiply(BigDecimal.valueOf(0.9)).setScale(2, RoundingMode.HALF_UP);
            }

            boolean accepte = getBooleanInput("Is the devis accepted? (true/false): ");

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
        String nom = getStringInput("Enter client name: ");

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

    private int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private long getLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private BigDecimal getBigDecimalInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal number.");
            }
        }
    }

    private boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                return Boolean.parseBoolean(input);
            }
            System.out.println("Invalid input. Please enter 'true' or 'false'.");
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private String getStringInput() {
        return scanner.nextLine();
    }

    private LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private EtatProjet getEtatProjet() {
        while (true) {
            System.out.print("Enter project state (ENCOURS, TERMINE, ANNULE): ");
            try {
                return EtatProjet.valueOf(scanner.nextLine().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid project state. Please use a valid state (e.g., ENCOURS, TERMINE, ANNULE).");
            }
        }
    }
}
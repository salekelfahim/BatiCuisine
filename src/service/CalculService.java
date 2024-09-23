package service;

import domain.Materiau;
import domain.MainOeuvre;
import java.math.BigDecimal;

public class CalculService {

    // Method to calculate cost including VAT for Materiau
    public BigDecimal calculateCostWithVAT(Materiau materiau) {
        return materiau.calculerCout();
    }

    // Method to calculate cost excluding VAT for Materiau
    public BigDecimal calculateCostWithoutVAT(Materiau materiau) {
        return materiau.calculerCoutSansTVA();
    }

    // Method to calculate cost including VAT for MainOeuvre
    public BigDecimal calculateCostWithVAT(MainOeuvre mainOeuvre) {
        return mainOeuvre.calculerCout();
    }

    // Method to calculate cost excluding VAT for MainOeuvre
    public BigDecimal calculateCostWithoutVAT(MainOeuvre mainOeuvre) {
        return mainOeuvre.calculerCoutSansTVA();
    }
}
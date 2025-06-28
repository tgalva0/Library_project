package main.database.strategy;

public class MultaEscalonada implements EstrategiaMulta {
    @Override
    public double calcularMulta(int dias) {
        return dias <= 5 ? 0 : dias * 0.75;
    }
}
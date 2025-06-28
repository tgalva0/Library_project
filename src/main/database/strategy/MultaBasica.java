package main.database.strategy;

public class MultaBasica implements EstrategiaMulta {
    @Override
    public double calcularMulta(int diasAtraso) {
        return diasAtraso * 0.5;
    }
}
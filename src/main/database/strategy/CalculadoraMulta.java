package main.database.strategy;

public class CalculadoraMulta {

    private EstrategiaMulta estrategia;

    public CalculadoraMulta(EstrategiaMulta estrategia) {
        this.estrategia = estrategia;
    }

    public void setEstrategia(EstrategiaMulta estrategia) {
        this.estrategia = estrategia;
    }

    public double calcular(int diasAtraso) {
        return estrategia.calcularMulta(diasAtraso);
    }
}
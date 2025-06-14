package main.database;

import java.sql.Timestamp;

public class Emprestimo {
    private int idEmprestimo;
    private int idCopiaLivro;
    private int idMembro;
    private Timestamp dataEmprestimo;
    private Timestamp dataDevolucao;
    private String statusEmprestimo;
    private double multa;

    public Emprestimo(int idEmprestimo, int idCopiaLivro, int idMembro, Timestamp dataEmprestimo, Timestamp dataDevolucao, String statusEmprestimo, double multa) {
        this.idEmprestimo = idEmprestimo;
        this.idCopiaLivro = idCopiaLivro;
        this.idMembro = idMembro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.statusEmprestimo = statusEmprestimo;
        this.multa = multa;
    }

    public int getIdEmprestimo() { return idEmprestimo; }
    public int getIdCopiaLivro() { return idCopiaLivro; }
    public int getIdMembro() { return idMembro; }
    public Timestamp getDataEmprestimo() { return dataEmprestimo; }
    public Timestamp getDataDevolucao() { return dataDevolucao; }
    public String getStatusEmprestimo() { return statusEmprestimo; }
    public double getMulta() { return multa; }

    @Override
    public String toString() {
        return "Emprestimo{" +
                "\nidEmprestimo=" + idEmprestimo +
                "\nidCopiaLivro=" + idCopiaLivro +
                "\nidMembro=" + idMembro +
                "\ndataEmprestimo=" + dataEmprestimo +
                "\ndataDevolucao=" + dataDevolucao +
                "\nstatusEmprestimo='" + statusEmprestimo  +
                "\nmulta=" + multa +
                '}';
    }
}
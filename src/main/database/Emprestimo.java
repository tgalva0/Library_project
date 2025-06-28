package main.database;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class Emprestimo {

    private String titulo;
    private String emailMembro;
    private Timestamp dataEmprestimo;
    private Timestamp dataDevolucao;
    private String statusEmprestimo;
    private double multa;

    public Emprestimo(String titulo, String emailMembro, Timestamp dataEmprestimo, Timestamp dataDevolucao, String statusEmprestimo, double multa) {
        this.titulo = titulo;
        this.emailMembro = emailMembro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.statusEmprestimo = statusEmprestimo;
        this.multa = multa;
    }

    public String getTitulo() { return titulo; }
    public String getEmailMembro() { return emailMembro; }
    public Timestamp getDataEmprestimo() { return dataEmprestimo; }
    public Timestamp getDataDevolucao() { return dataDevolucao; }
    public String getStatusEmprestimo() { return statusEmprestimo; }
    public double getMulta() { return multa; }

    @Override
    public String toString() {
        return "Empréstimo{" +
                "\nTitulo= " + titulo +
                "\nEmail Membro= " + emailMembro +
                "\nData de Empréstimo =" + dataEmprestimo +
                "\nData Devolução=" + dataDevolucao +
                "\nStatus Empréstimo='" + statusEmprestimo  +
                "\nmulta=" + multa +"\n" +
                '}';
    }

}
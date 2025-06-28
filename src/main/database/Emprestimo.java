package main.database;
import main.database.state.EstadoAtivo;
import main.database.state.EstadoAtrasado;
import main.database.state.EstadoConcluido;
import main.database.state.EstadoEmprestimo;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class Emprestimo {

    private String titulo;
    private String emailMembro;
    private Timestamp dataEmprestimo;
    private Timestamp dataDevolucao;
    private EstadoEmprestimo estado;
    private double multa;

    public Emprestimo(String titulo, String emailMembro, Timestamp dataEmprestimo, Timestamp dataDevolucao, String statusEmprestimo, double multa) {
        this.titulo = titulo;
        this.emailMembro = emailMembro;
        this.dataEmprestimo = dataEmprestimo;
        this.dataDevolucao = dataDevolucao;
        this.setEstadoPeloNome(statusEmprestimo);
        this.multa = multa;
    }

    public String getTitulo() { return titulo; }
    public String getEmailMembro() { return emailMembro; }
    public Timestamp getDataEmprestimo() { return dataEmprestimo; }
    public Timestamp getDataDevolucao() { return dataDevolucao; }
    public double getMulta() { return multa; }
    public void setEstado(EstadoEmprestimo estado) {
        this.estado = estado;
    }

    public void setEstadoPeloNome(String nome) {
        switch (nome.toLowerCase()) {
            case "ativo": this.estado = new EstadoAtivo(); break;
            case "atrasado": this.estado = new EstadoAtrasado(); break;
            case "concluído": this.estado = new EstadoConcluido(); break;
            default: this.estado = new EstadoConcluido(); break;
        }
    }


    public void processarEstado() {
        if (estado != null) {
            estado.processar(this);
        }
    }


    public String getStatusEmprestimo() {
        return estado != null ? estado.getNome() : "indefinido";
    }

    public String getDataEmprestimoFormatada() {
        if (dataEmprestimo == null) return "";
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(dataEmprestimo);
    }

    public String getDataDevolucaoFormatada() {
        if (dataDevolucao == null) return "";
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(dataDevolucao);
    }


    @Override
    public String toString() {
        return "Empréstimo{" +
                "\nTitulo= " + titulo +
                "\nEmail Membro= " + emailMembro +
                "\nData de Empréstimo =" + dataEmprestimo +
                "\nData Devolução=" + dataDevolucao +
                "\nStatus Empréstimo='" + getStatusEmprestimo()  +
                "\nmulta=" + multa +"\n" +
                '}';
    }

}
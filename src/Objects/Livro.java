package Objects;

public class Livro {
    private final int id;
    private final String titulo;
    private final String autor;
    private final String isbn;
    private final int num_copias;

    public Livro(int id, String titulo, String isbn, int num_copias, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.num_copias = num_copias;
        this.autor = autor;
    }

    public int getId() {
        return id;
    }
    public String getTitulo() {
        return titulo;
    }
    public String getAutor() {
        return autor;
    }
    public int getNum_copias() {
        return num_copias;
    }

    @Override
    public String toString() {
        return ("\nTitulo = " + titulo + ",\nID = " + id + ",\nNum copias = " + num_copias + ",\nAutor = " + autor + ",\nisbn = " + isbn);
    }
}

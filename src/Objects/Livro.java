package Objects;

public class Livro {
    private final String titulo;
    private final String autor;
    private final String isbn;
    private final int num_copias;

    public Livro( String titulo, String isbn, int num_copias, String autor) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.num_copias = num_copias;
        this.autor = autor;
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
    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return ("\nTitulo = " + titulo + ",\nNum copias = " + num_copias + ",\nAutor = " + autor + ",\nisbn = " + isbn);
    }
}

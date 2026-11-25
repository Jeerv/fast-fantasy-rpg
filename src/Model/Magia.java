package Model;

public class Magia {
    private int idMagia;
    private String nome;
    private String descricao;
    private int idCriador;

    public Magia(int idMagia, String nome, String descricao, int idCriador) {
        this.idMagia = idMagia;
        this.nome = nome;
        this.descricao = descricao;
        this.idCriador = idCriador;
    }

    public int getIdMagia() {
        return idMagia;
    }

    public void setIdMagia(int idMagia) {
        this.idMagia = idMagia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getIdCriador() {
        return idCriador;
    }

    public void setIdCriador(int idCriador) {
        this.idCriador = idCriador;
    }
}

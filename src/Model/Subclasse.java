package Model;

public class Subclasse {
    private int idSubclasse;
    private String nome;
    private String descricao;
    private int idClasse;

    public Subclasse(int idSubclasse, String nome, String descricao, int idClasse) {
        this.idSubclasse = idSubclasse;
        this.nome = nome;
        this.descricao = descricao;
        this.idClasse = idClasse;
    }

    public int getIdSubclasse() {
        return idSubclasse;
    }

    public void setIdSubclasse(int idSubclasse) {
        this.idSubclasse = idSubclasse;
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

    public int getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(int idClasse) {
        this.idClasse = idClasse;
    }
}

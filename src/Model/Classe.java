package Model;

public class Classe {
    private int idClasse;
    private String nome;
    private String descricao;

    public Classe(int idClasse, String nome, String descricao) {
        this.idClasse = idClasse;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(int idClasse) {
        this.idClasse = idClasse;
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
}

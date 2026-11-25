package Model;

public class HabilidadeClasse {
    private int idHabilidadeClasse;
    private String nome;
    private String descricao;
    private int nivel;
    private int idClasse;

    public HabilidadeClasse(int idHabilidadeClasse, String nome, String descricao, int nivel, int idClasse) {
        this.idHabilidadeClasse = idHabilidadeClasse;
        this.nome = nome;
        this.descricao = descricao;
        this.nivel = nivel;
        this.idClasse = idClasse;
    }

    public int getIdHabilidadeClasse() {
        return idHabilidadeClasse;
    }

    public void setIdHabilidadeClasse(int idHabilidadeClasse) {
        this.idHabilidadeClasse = idHabilidadeClasse;
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

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(int idClasse) {
        this.idClasse = idClasse;
    }
}

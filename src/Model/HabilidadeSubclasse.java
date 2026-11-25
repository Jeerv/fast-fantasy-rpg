package Model;

public class HabilidadeSubclasse {
    private int idHabilidadeSubclasse;
    private String nome;
    private String descricao;
    private int nivel;
    private int idSubclasse;

    public HabilidadeSubclasse(int idHabilidadeSubclasse, String nome, String descricao, int nivel, int idSubclasse) {
        this.idHabilidadeSubclasse = idHabilidadeSubclasse;
        this.nome = nome;
        this.descricao = descricao;
        this.nivel = nivel;
        this.idSubclasse = idSubclasse;
    }

    public int getIdHabilidadeSubclasse() {
        return idHabilidadeSubclasse;
    }

    public void setIdHabilidadeSubclasse(int idHabilidadeSubclasse) {
        this.idHabilidadeSubclasse = idHabilidadeSubclasse;
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

    public int getIdSubclasse() {
        return idSubclasse;
    }

    public void setIdSubclasse(int idSubclasse) {
        this.idSubclasse = idSubclasse;
    }
}

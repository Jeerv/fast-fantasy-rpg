package Model;

public class Pericia {
    private int idPericia;
    private String nome;
    private Atributo atributo;

    public Pericia(int idPericia, String nome, Atributo atributo) {
        this.idPericia = idPericia;
        this.nome = nome;
        this.atributo = atributo;
    }

    public int getIdPericia() {
        return idPericia;
    }

    public void setIdPericia(int idPericia) {
        this.idPericia = idPericia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }
}

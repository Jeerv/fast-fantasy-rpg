package Model;

public class Mesa {
    private int idMesa;
    private boolean mesaPrivada;
    private String status;
    private String nome;
    private String descricao;
    private String codigoConvite;
    private int idMestre;

    public Mesa() {
    }

    public Mesa(int idMesa, boolean mesaPrivada, String status, String nome, String descricao, String codigoConvite, int idMestre) {
        this.idMesa = idMesa;
        this.mesaPrivada = mesaPrivada;
        this.status = status;
        this.nome = nome;
        this.descricao = descricao;
        this.codigoConvite = codigoConvite;
        this.idMestre = idMestre;
    }

    // Getters e Setters
    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public boolean isMesaPrivada() {
        return mesaPrivada;
    }

    public void setMesaPrivada(boolean mesaPrivada) {
        this.mesaPrivada = mesaPrivada;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getCodigoConvite() {
        return codigoConvite;
    }

    public void setCodigoConvite(String codigoConvite) {
        this.codigoConvite = codigoConvite;
    }

    public int getIdMestre() {
        return idMestre;
    }

    public void setIdMestre(int idMestre) {
        this.idMestre = idMestre;
    }
}

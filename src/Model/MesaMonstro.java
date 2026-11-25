package Model;

import java.sql.Timestamp;

public class MesaMonstro {
    private int idMesaMonstro;
    private int idMesa;
    private int idMonstro;
    private int idMestre;
    private String apelido;
    private int hpAtual;
    private int hpMaximo;
    private int iniciativa;
    private StatusMesaMonstro status;
    private Timestamp adicionadoEm;

    public MesaMonstro() {
    }

    // Getters e Setters
    public int getIdMesaMonstro() {
        return idMesaMonstro;
    }

    public void setIdMesaMonstro(int idMesaMonstro) {
        this.idMesaMonstro = idMesaMonstro;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getIdMonstro() {
        return idMonstro;
    }

    public void setIdMonstro(int idMonstro) {
        this.idMonstro = idMonstro;
    }

    public int getIdMestre() {
        return idMestre;
    }

    public void setIdMestre(int idMestre) {
        this.idMestre = idMestre;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public int getHpAtual() {
        return hpAtual;
    }

    public void setHpAtual(int hpAtual) {
        this.hpAtual = hpAtual;
    }

    public int getHpMaximo() {
        return hpMaximo;
    }

    public void setHpMaximo(int hpMaximo) {
        this.hpMaximo = hpMaximo;
    }

    public int getIniciativa() {
        return iniciativa;
    }

    public void setIniciativa(int iniciativa) {
        this.iniciativa = iniciativa;
    }

    public StatusMesaMonstro getStatus() {
        return status;
    }

    public void setStatus(StatusMesaMonstro status) {
        this.status = status;
    }

    public Timestamp getAdicionadoEm() {
        return adicionadoEm;
    }

    public void setAdicionadoEm(Timestamp adicionadoEm) {
        this.adicionadoEm = adicionadoEm;
    }
}

package Model;

import java.sql.Timestamp;

// Linha gravada pelo trigger trg_personagem_au_log quando nivel ou hp_atual mudam. Somente leitura.
public class LogPersonagem {
    private int idLog;
    private int idPersonagem;
    private int nivelAntigo;
    private int nivelNovo;
    private int hpAntigo;
    private int hpNovo;
    private String usuario;
    private Timestamp alteradoEm;

    public LogPersonagem() {
    }

    // Getters e Setters
    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public int getIdPersonagem() {
        return idPersonagem;
    }

    public void setIdPersonagem(int idPersonagem) {
        this.idPersonagem = idPersonagem;
    }

    public int getNivelAntigo() {
        return nivelAntigo;
    }

    public void setNivelAntigo(int nivelAntigo) {
        this.nivelAntigo = nivelAntigo;
    }

    public int getNivelNovo() {
        return nivelNovo;
    }

    public void setNivelNovo(int nivelNovo) {
        this.nivelNovo = nivelNovo;
    }

    public int getHpAntigo() {
        return hpAntigo;
    }

    public void setHpAntigo(int hpAntigo) {
        this.hpAntigo = hpAntigo;
    }

    public int getHpNovo() {
        return hpNovo;
    }

    public void setHpNovo(int hpNovo) {
        this.hpNovo = hpNovo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Timestamp getAlteradoEm() {
        return alteradoEm;
    }

    public void setAlteradoEm(Timestamp alteradoEm) {
        this.alteradoEm = alteradoEm;
    }
}

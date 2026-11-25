package Model;

public class AntecedentePericia {
    private int idAntecedente;
    private int idPericia;

    public AntecedentePericia(int idAntecedente, int idPericia) {
        this.idAntecedente = idAntecedente;
        this.idPericia = idPericia;
    }

    public int getIdAntecedente() {
        return idAntecedente;
    }

    public void setIdAntecedente(int v) {
        idAntecedente = v;
    }

    public int getIdPericia() {
        return idPericia;
    }

    public void setIdPericia(int v) {
        idPericia = v;
    }
}
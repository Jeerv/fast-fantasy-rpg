package Model;

public class PersonagemTalento {
    private int idPersonagem;
    private int idTalento;

    public PersonagemTalento(int idPersonagem, int idTalento) {
        this.idPersonagem = idPersonagem;
        this.idTalento = idTalento;
    }

    public int getIdPersonagem() {
        return idPersonagem;
    }

    public void setIdPersonagem(int v) {
        idPersonagem = v;
    }

    public int getIdTalento() {
        return idTalento;
    }

    public void setIdTalento(int v) {
        idTalento = v;
    }
}
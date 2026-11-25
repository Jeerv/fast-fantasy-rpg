package Model;

public class PersonagemPericia {
    private int idPersonagem;
    private int idPericia;
    private boolean proficiente;

    public PersonagemPericia(int idPersonagem, int idPericia, boolean proficiente) {
        this.idPersonagem = idPersonagem;
        this.idPericia = idPericia;
        this.proficiente = proficiente;
    }

    public int getIdPersonagem() {
        return idPersonagem;
    }

    public void setIdPersonagem(int idPersonagem) {
        this.idPersonagem = idPersonagem;
    }

    public int getIdPericia() {
        return idPericia;
    }

    public void setIdPericia(int idPericia) {
        this.idPericia = idPericia;
    }

    public boolean isProficiente() {
        return proficiente;
    }

    public void setProficiente(boolean proficiente) {
        this.proficiente = proficiente;
    }
}

package Model;

public class Mestre extends Usuario {
    private StatusParticipante status;

    public Mestre(int codigo, String nome, String email, String senha, boolean premium, int conquista, StatusParticipante status) {
        super(codigo, nome, email, senha, premium, conquista);
        this.status = status;
    }

    public StatusParticipante getStatus() {
        return status;
    }

    public void setStatus(StatusParticipante status) {
        this.status = status;
    }
}

package Model;

public class Player extends Usuario {
    private StatusParticipante status;

    public Player(int codigo, String nome, String email, String senha, boolean premium, int conquista, StatusParticipante status) {
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

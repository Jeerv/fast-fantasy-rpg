package Model;

public class MesaPlayer {
    private int idMesa;
    private int idPlayer;

    public MesaPlayer(int idMesa, int idPlayer) {
        this.idMesa = idMesa;
        this.idPlayer = idPlayer;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }
}

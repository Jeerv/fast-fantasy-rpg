package Model;

public class PersonagemMagia {
    private int idPersonagem;
    private int idMagia;

    public PersonagemMagia(int idPersonagem, int idMagia) {
        this.idPersonagem = idPersonagem;
        this.idMagia = idMagia;
    }

    public int getIdPersonagem() {
        return idPersonagem;
    }

    public void setIdPersonagem(int idPersonagem) {
        this.idPersonagem = idPersonagem;
    }

    public int getIdMagia() {
        return idMagia;
    }

    public void setIdMagia(int idMagia) {
        this.idMagia = idMagia;
    }
}

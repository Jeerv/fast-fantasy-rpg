package Model;

public class RacaAtributo {
    private int idRaca;
    private Atributo atributo;
    private int bonus;

    public RacaAtributo(int idRaca, Atributo atributo, int bonus) {
        this.idRaca = idRaca;
        this.atributo = atributo;
        this.bonus = bonus;
    }

    public int getIdRaca() {
        return idRaca;
    }

    public void setIdRaca(int idRaca) {
        this.idRaca = idRaca;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}

package Model;

public abstract class Usuario {
    protected int codigo;
    protected String nome;
    protected String email;
    protected String senha;
    protected Boolean premium;
    protected int conquista;

    public Usuario(int codigo, String nome, String email, String senha, boolean premium, int conquista) {
        this.codigo = codigo;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.premium = premium;
        this.conquista = conquista;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public int getConquista() {
        return conquista;
    }

    public void setConquista(int conquista) {
        this.conquista = conquista;
    }
}

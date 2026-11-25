package Controller;

import DAO.LogPersonagemDAO;
import DAO.AntecedenteDAO;
import DAO.AntecedentePericiaDAO;
import DAO.TalentoDAO;
import DAO.PersonagemTalentoDAO;
import DAO.MesaDAO;
import DAO.MesaMonstroDAO;
import DAO.MesaPlayerDAO;
import DAO.MonstroDAO;
import DAO.PersonagemDAO;
import Model.LogPersonagem;
import Model.Antecedente;
import Model.AntecedentePericia;
import Model.Talento;
import Model.PersonagemTalento;
import Model.Mesa;
import Model.MesaMonstro;
import Model.MesaPlayer;
import Model.Monstro;
import Model.Personagem;
import Model.StatusMesaMonstro;

import java.sql.SQLException;
import java.util.List;

public class RPGController {
    private MesaDAO mesaDAO;
    private PersonagemDAO personagemDAO;
    private MonstroDAO monstroDAO;
    private MesaMonstroDAO mesaMonstroDAO;
    private MesaPlayerDAO mesaPlayerDAO;
    private LogPersonagemDAO logPersonagemDAO;
    private AntecedenteDAO antecedenteDAO;
    private AntecedentePericiaDAO antecedentePericiaDAO;
    private TalentoDAO talentoDAO;
    private PersonagemTalentoDAO personagemTalentoDAO;

    public RPGController() {
        this.mesaDAO = new MesaDAO();
        this.personagemDAO = new PersonagemDAO();
        this.monstroDAO = new MonstroDAO();
        this.mesaMonstroDAO = new MesaMonstroDAO();
        this.mesaPlayerDAO = new MesaPlayerDAO();
        this.logPersonagemDAO = new LogPersonagemDAO();
        this.antecedenteDAO = new AntecedenteDAO();
        this.antecedentePericiaDAO = new AntecedentePericiaDAO();
        this.talentoDAO = new TalentoDAO();
        this.personagemTalentoDAO = new PersonagemTalentoDAO();
    }

    // --- Controladores de Mesa ---
    public void cadastrarMesa(boolean privada, String status, String nome, String desc, String codigo, int idMestre) {
        Mesa m = new Mesa(0, privada, status, nome, desc, codigo, idMestre);
        mesaDAO.inserir(m);
    }

    public List<Mesa> listarMesas() {
        return mesaDAO.listar();
    }

    public void atualizarMesa(int id, String nome, String desc, String status) {
        Mesa m = new Mesa();
        m.setIdMesa(id);
        m.setNome(nome);
        m.setDescricao(desc);
        m.setStatus(status);
        mesaDAO.atualizar(m);
    }

    public void deletarMesa(int id) {
        mesaDAO.deletar(id);
    }

    // --- Controladores de Personagem (Relacionado à Mesa) ---
    public void cadastrarPersonagem(String nome, int nivel, int classe, int raca, int player, int atributos, Integer idMesa, int hp, int ca, int deslocamento) {
        Personagem p = new Personagem();
        p.setNome(nome);
        p.setNivel(nivel);
        p.setIdClasse(classe);
        p.setIdRaca(raca);
        p.setIdPlayer(player);
        p.setIdAtributos(atributos);
        p.setIdMesa(idMesa);
        p.setHpMaximo(hp);
        p.setHpAtual(hp);
        p.setCa(ca);
        p.setDeslocamento(deslocamento);
        personagemDAO.inserir(p);
    }

    public void cadastrarPersonagemCompleto(String nome, int nivel, int idClasse, Integer idSubclasse, int idRaca, Integer idSubRaca, Integer idAntecedente, int idPlayer, int idAtributos, Integer idMesa, int hp, int ca, int deslocamento) {
        Personagem p = new Personagem();
        p.setNome(nome);
        p.setNivel(nivel);
        p.setIdClasse(idClasse);
        p.setIdSubclasse(idSubclasse);
        p.setIdRaca(idRaca);
        p.setIdSubRaca(idSubRaca);
        p.setIdAntecedente(idAntecedente);
        p.setIdPlayer(idPlayer);
        p.setIdAtributos(idAtributos);
        p.setIdMesa(idMesa);
        p.setHpMaximo(hp);
        p.setHpAtual(hp);
        p.setCa(ca);
        p.setDeslocamento(deslocamento);
        personagemDAO.inserir(p);
    }

    public List<Personagem> listarPersonagens() {
        return personagemDAO.listar();
    }

    public void atualizarPersonagem(int id, String nome, int nivel, int hpAtual, Integer idMesa) {
        Personagem p = new Personagem();
        p.setIdPersonagem(id);
        p.setNome(nome);
        p.setNivel(nivel);
        p.setHpAtual(hpAtual);
        p.setIdMesa(idMesa);
        personagemDAO.atualizar(p);
    }

    public void atualizarPersonagemCompleto(Personagem p) {
        personagemDAO.atualizarCompleto(p);
    }

    public void deletarPersonagem(int id) {
        personagemDAO.deletar(id);
    }

    // --- Jogadores da Mesa (tabela mesa_player) ---
    public void entrarNaMesa(int idMesa, int idPlayer) {
        try {
            mesaPlayerDAO.inserir(new MesaPlayer(idMesa, idPlayer));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao entrar na mesa: " + e.getMessage(), e);
        }
    }

    public void sairDaMesa(int idMesa, int idPlayer) {
        try {
            mesaPlayerDAO.deletar(idMesa, idPlayer);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao sair da mesa: " + e.getMessage(), e);
        }
    }

    public List<MesaPlayer> listarJogadoresDaMesa(int idMesa) {
        try {
            return mesaPlayerDAO.listarPorIdMesa(idMesa);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar jogadores da mesa: " + e.getMessage(), e);
        }
    }

    // --- PV durante a mesa (delta negativo = dano, positivo = cura) ---
    public boolean ajustarHpPersonagem(int idPersonagem, int idPlayer, int delta) {
        return personagemDAO.ajustarHp(idPersonagem, idPlayer, delta);
    }

    public boolean ajustarHpPersonagemPeloMestre(int idPersonagem, int idMestre, int delta) {
        return personagemDAO.ajustarHpPeloMestre(idPersonagem, idMestre, delta);
    }

    public boolean descansoLongo(int idPersonagem, int idPlayer) {
        return personagemDAO.descansoLongo(idPersonagem, idPlayer);
    }

    public List<LogPersonagem> historicoDoPersonagem(int idPersonagem) {
        return logPersonagemDAO.listarPorPersonagem(idPersonagem);
    }

    // --- Antecedentes e suas perícias ---
    public List<Antecedente> listarAntecedentes() {
        try {
            return antecedenteDAO.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar antecedentes", e);
        }
    }

    public Antecedente buscarAntecedente(int id) {
        try {
            return antecedenteDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar antecedente", e);
        }
    }

    public List<AntecedentePericia> listarPericiasDoAntecedente(int idAntecedente) {
        try {
            return antecedentePericiaDAO.listarPorAntecedente(idAntecedente);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar perícias do antecedente", e);
        }
    }

    // --- Talentos oficiais e personalizados ---
    public List<Talento> listarTalentos(int idUsuario) {
        try {
            return talentoDAO.listarVisiveis(idUsuario);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar talentos", e);
        }
    }

    public Talento buscarTalento(int id) {
        try {
            return talentoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar talento", e);
        }
    }

    public void cadastrarTalento(Talento talento, int idUsuario) {
        talento.setIdCriador(idUsuario);
        try {
            talentoDAO.inserir(talento);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar talento", e);
        }
    }

    public boolean atualizarTalento(Talento talento, int idUsuario) {
        try {
            return talentoDAO.atualizar(talento, idUsuario);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar talento", e);
        }
    }

    public boolean deletarTalento(int idTalento, int idUsuario) {
        try {
            return talentoDAO.deletar(idTalento, idUsuario);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover talento", e);
        }
    }

    public void adicionarTalentoAoPersonagem(int idPersonagem, int idTalento) {
        try {
            personagemTalentoDAO.vincular(idPersonagem, idTalento);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao associar talento ao personagem", e);
        }
    }

    public List<PersonagemTalento> listarTalentosDoPersonagem(int idPersonagem) {
        try {
            return personagemTalentoDAO.listarPorPersonagem(idPersonagem);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar talentos do personagem", e);
        }
    }

    public void removerTalentoDoPersonagem(int idPersonagem, int idTalento) {
        try {
            personagemTalentoDAO.desvincular(idPersonagem, idTalento);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover talento do personagem", e);
        }
    }

    // --- Monstros (catálogo do livro + os criados pelo usuário) ---
    public List<Monstro> listarMonstros(int idUsuario, String busca) {
        return monstroDAO.listarVisiveis(idUsuario, busca);
    }

    public Monstro buscarMonstro(int idMonstro) {
        return monstroDAO.buscarPorId(idMonstro);
    }

    public void cadastrarMonstro(Monstro m) {
        monstroDAO.inserir(m);
    }

    public boolean atualizarMonstro(Monstro m, int idUsuario) {
        return monstroDAO.atualizar(m, idUsuario);
    }

    public boolean deletarMonstro(int idMonstro, int idUsuario) {
        return monstroDAO.deletar(idMonstro, idUsuario);
    }

    // --- Campo de batalha da Mesa (monstros colocados pelo mestre) ---
    public boolean adicionarMonstroNaMesa(int idMesa, int idMonstro, int idMestre, String apelido) {
        return mesaMonstroDAO.adicionar(idMesa, idMonstro, idMestre, apelido);
    }

    public List<MesaMonstro> listarCampoDeBatalha(int idMesa) {
        return mesaMonstroDAO.listarPorMesa(idMesa);
    }

    public boolean ajustarHpMonstro(int idMesaMonstro, int idMestre, int delta) {
        return mesaMonstroDAO.ajustarHp(idMesaMonstro, idMestre, delta);
    }

    public boolean definirHpMonstro(int idMesaMonstro, int idMestre, int hp) {
        return mesaMonstroDAO.definirHp(idMesaMonstro, idMestre, hp);
    }

    public boolean definirIniciativaMonstro(int idMesaMonstro, int idMestre, int iniciativa) {
        return mesaMonstroDAO.definirIniciativa(idMesaMonstro, idMestre, iniciativa);
    }

    public boolean alterarStatusMonstro(int idMesaMonstro, int idMestre, StatusMesaMonstro status) {
        return mesaMonstroDAO.definirStatus(idMesaMonstro, idMestre, status);
    }

    public boolean removerMonstroDaMesa(int idMesaMonstro, int idMestre) {
        return mesaMonstroDAO.remover(idMesaMonstro, idMestre);
    }

    public MesaMonstro buscarMonstroDaMesa(int idMesaMonstro) {
        return mesaMonstroDAO.buscarPorId(idMesaMonstro);
    }
}

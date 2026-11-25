package DAO;

import Model.Monstro;
import Model.TamanhoMonstro;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonstroDAO {

    private Monstro montar(ResultSet rs) throws SQLException {
        Monstro m = new Monstro();
        m.setIdMonstro(rs.getInt("id_monstro"));
        m.setNome(rs.getString("nome"));
        m.setTamanho(TamanhoMonstro.doBanco(rs.getString("tamanho")));
        m.setTipo(rs.getString("tipo"));
        m.setTendencia(rs.getString("tendencia"));
        m.setCa(rs.getInt("ca"));
        m.setHpMaximo(rs.getInt("hp_maximo"));
        m.setDeslocamento(rs.getString("deslocamento"));
        m.setForca(rs.getInt("forca"));
        m.setDestreza(rs.getInt("destreza"));
        m.setConstituicao(rs.getInt("constituicao"));
        m.setInteligencia(rs.getInt("inteligencia"));
        m.setSabedoria(rs.getInt("sabedoria"));
        m.setCarisma(rs.getInt("carisma"));
        m.setDesafio(rs.getString("desafio"));
        m.setXp(rs.getInt("xp"));
        m.setDescricao(rs.getString("descricao"));
        m.setIdCriador(rs.getInt("id_criador")); // NULL vira 0 = oficial
        return m;
    }

    public void inserir(Monstro m) {
        String sql = "INSERT INTO monstro (nome, tamanho, tipo, tendencia, ca, hp_maximo, deslocamento, forca, destreza, constituicao, inteligencia, sabedoria, carisma, desafio, xp, descricao, id_criador) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, m.getNome());
            stmt.setString(2, m.getTamanho().getValorBanco());
            stmt.setString(3, m.getTipo());
            stmt.setString(4, m.getTendencia());
            stmt.setInt(5, m.getCa());
            stmt.setInt(6, m.getHpMaximo());
            stmt.setString(7, m.getDeslocamento());
            stmt.setInt(8, m.getForca());
            stmt.setInt(9, m.getDestreza());
            stmt.setInt(10, m.getConstituicao());
            stmt.setInt(11, m.getInteligencia());
            stmt.setInt(12, m.getSabedoria());
            stmt.setInt(13, m.getCarisma());
            stmt.setString(14, m.getDesafio());
            stmt.setInt(15, m.getXp());
            stmt.setString(16, m.getDescricao());
            if (m.getIdCriador() == 0) stmt.setNull(17, Types.INTEGER);
            else stmt.setInt(17, m.getIdCriador());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) m.setIdMonstro(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir monstro: " + e.getMessage(), e);
        }
    }

    public Monstro buscarPorId(int id) {
        String sql = "SELECT * FROM monstro WHERE id_monstro = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar monstro: " + e.getMessage(), e);
        }
    }

    // Catálogo do mestre: monstros oficiais do livro + os que ele mesmo criou. O trecho filtra pelo nome ("" lista todos).
    public List<Monstro> listarVisiveis(int idUsuario, String trechoNome) {
        List<Monstro> lista = new ArrayList<>();
        String sql = "SELECT * FROM monstro WHERE (id_criador IS NULL OR id_criador = ?) AND nome LIKE ? ORDER BY nome";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.setString(2, "%" + (trechoNome == null ? "" : trechoNome) + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar monstros: " + e.getMessage(), e);
        }
        return lista;
    }

    // Só o criador altera. Monstros oficiais (id_criador NULL) nunca casam. false = não existe ou não é dele.
    public boolean atualizar(Monstro m, int idUsuario) {
        String sql = "UPDATE monstro SET nome = ?, tamanho = ?, tipo = ?, tendencia = ?, ca = ?, hp_maximo = ?, deslocamento = ?, forca = ?, destreza = ?, constituicao = ?, inteligencia = ?, sabedoria = ?, carisma = ?, desafio = ?, xp = ?, descricao = ? WHERE id_monstro = ? AND id_criador = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, m.getNome());
            stmt.setString(2, m.getTamanho().getValorBanco());
            stmt.setString(3, m.getTipo());
            stmt.setString(4, m.getTendencia());
            stmt.setInt(5, m.getCa());
            stmt.setInt(6, m.getHpMaximo());
            stmt.setString(7, m.getDeslocamento());
            stmt.setInt(8, m.getForca());
            stmt.setInt(9, m.getDestreza());
            stmt.setInt(10, m.getConstituicao());
            stmt.setInt(11, m.getInteligencia());
            stmt.setInt(12, m.getSabedoria());
            stmt.setInt(13, m.getCarisma());
            stmt.setString(14, m.getDesafio());
            stmt.setInt(15, m.getXp());
            stmt.setString(16, m.getDescricao());
            stmt.setInt(17, m.getIdMonstro());
            stmt.setInt(18, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar monstro: " + e.getMessage(), e);
        }
    }

    // Só o criador apaga. mesa_monstro usa ON DELETE RESTRICT: monstro em uso numa mesa não pode ser apagado.
    public boolean deletar(int id, int idUsuario) {
        String sql = "DELETE FROM monstro WHERE id_monstro = ? AND id_criador = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, idUsuario);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar monstro (verifique se ele está em alguma mesa): " + e.getMessage(), e);
        }
    }
}

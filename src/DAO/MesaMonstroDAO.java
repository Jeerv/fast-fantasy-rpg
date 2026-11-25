package DAO;

import Model.MesaMonstro;
import Model.StatusMesaMonstro;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaMonstroDAO {

    private MesaMonstro montar(ResultSet rs) throws SQLException {
        MesaMonstro m = new MesaMonstro();
        m.setIdMesaMonstro(rs.getInt("id_mesa_monstro"));
        m.setIdMesa(rs.getInt("id_mesa"));
        m.setIdMonstro(rs.getInt("id_monstro"));
        m.setIdMestre(rs.getInt("id_mestre"));
        m.setApelido(rs.getString("apelido"));
        m.setHpAtual(rs.getInt("hp_atual"));
        m.setHpMaximo(rs.getInt("hp_maximo"));
        m.setIniciativa(rs.getInt("iniciativa"));
        m.setStatus(StatusMesaMonstro.doBanco(rs.getString("status")));
        m.setAdicionadoEm(rs.getTimestamp("adicionado_em"));
        return m;
    }

    // Coloca um monstro no campo de batalha. O HP vem da ficha do monstro e só o mestre da mesa consegue (o trigger também confere).
    // false = mesa não é desse mestre ou monstro inexistente.
    public boolean adicionar(int idMesa, int idMonstro, int idMestre, String apelido) {
        String sql = "INSERT INTO mesa_monstro (id_mesa, id_monstro, id_mestre, apelido, hp_atual, hp_maximo) "
                + "SELECT me.id_mesa, mo.id_monstro, me.id_mestre, ?, mo.hp_maximo, mo.hp_maximo "
                + "FROM mesa me JOIN monstro mo ON mo.id_monstro = ? WHERE me.id_mesa = ? AND me.id_mestre = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (apelido == null || apelido.trim().isEmpty()) stmt.setNull(1, Types.VARCHAR);
            else stmt.setString(1, apelido.trim());
            stmt.setInt(2, idMonstro);
            stmt.setInt(3, idMesa);
            stmt.setInt(4, idMestre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar monstro à mesa: " + e.getMessage(), e);
        }
    }

    public MesaMonstro buscarPorId(int idMesaMonstro) {
        String sql = "SELECT * FROM mesa_monstro WHERE id_mesa_monstro = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMesaMonstro);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar monstro da mesa: " + e.getMessage(), e);
        }
    }

    // Campo de batalha da mesa, na ordem de iniciativa (maior primeiro).
    public List<MesaMonstro> listarPorMesa(int idMesa) {
        List<MesaMonstro> lista = new ArrayList<>();
        String sql = "SELECT * FROM mesa_monstro WHERE id_mesa = ? ORDER BY iniciativa DESC, id_mesa_monstro";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMesa);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar monstros da mesa: " + e.getMessage(), e);
        }
        return lista;
    }

    // Dano (delta negativo) ou cura (delta positivo). O HP fica entre 0 e hp_maximo; chegar a 0 marca 'derrotado'
    // e curar um derrotado o devolve a 'ativo'. O status vem antes do hp_atual no SET porque o MySQL avalia da esquerda para a direita.
    // false = não existe ou não é do mestre informado.
    public boolean ajustarHp(int idMesaMonstro, int idMestre, int delta) {
        String sql = "UPDATE mesa_monstro SET "
                + "status = CASE WHEN GREATEST(0, LEAST(hp_maximo, hp_atual + ?)) = 0 THEN 'derrotado' "
                + "WHEN status = 'derrotado' THEN 'ativo' ELSE status END, "
                + "hp_atual = GREATEST(0, LEAST(hp_maximo, hp_atual + ?)) "
                + "WHERE id_mesa_monstro = ? AND id_mestre = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delta);
            stmt.setInt(2, delta);
            stmt.setInt(3, idMesaMonstro);
            stmt.setInt(4, idMestre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao ajustar PV do monstro: " + e.getMessage(), e);
        }
    }

    // Define o HP exato (limitado a 0..hp_maximo) com a mesma regra de status do ajuste.
    public boolean definirHp(int idMesaMonstro, int idMestre, int hp) {
        String sql = "UPDATE mesa_monstro SET "
                + "status = CASE WHEN GREATEST(0, LEAST(hp_maximo, ?)) = 0 THEN 'derrotado' "
                + "WHEN status = 'derrotado' THEN 'ativo' ELSE status END, "
                + "hp_atual = GREATEST(0, LEAST(hp_maximo, ?)) "
                + "WHERE id_mesa_monstro = ? AND id_mestre = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, hp);
            stmt.setInt(2, hp);
            stmt.setInt(3, idMesaMonstro);
            stmt.setInt(4, idMestre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao definir PV do monstro: " + e.getMessage(), e);
        }
    }

    public boolean definirIniciativa(int idMesaMonstro, int idMestre, int iniciativa) {
        String sql = "UPDATE mesa_monstro SET iniciativa = ? WHERE id_mesa_monstro = ? AND id_mestre = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, iniciativa);
            stmt.setInt(2, idMesaMonstro);
            stmt.setInt(3, idMestre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao definir iniciativa: " + e.getMessage(), e);
        }
    }

    public boolean definirStatus(int idMesaMonstro, int idMestre, StatusMesaMonstro status) {
        String sql = "UPDATE mesa_monstro SET status = ? WHERE id_mesa_monstro = ? AND id_mestre = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.getValorBanco());
            stmt.setInt(2, idMesaMonstro);
            stmt.setInt(3, idMestre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar status do monstro: " + e.getMessage(), e);
        }
    }

    public boolean remover(int idMesaMonstro, int idMestre) {
        String sql = "DELETE FROM mesa_monstro WHERE id_mesa_monstro = ? AND id_mestre = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMesaMonstro);
            stmt.setInt(2, idMestre);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover monstro da mesa: " + e.getMessage(), e);
        }
    }
}

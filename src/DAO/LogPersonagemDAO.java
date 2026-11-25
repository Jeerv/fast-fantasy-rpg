package DAO;

import Model.LogPersonagem;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogPersonagemDAO {

    // O trigger trg_personagem_au_log grava as linhas; o back end só consulta.
    public List<LogPersonagem> listarPorPersonagem(int idPersonagem) {
        List<LogPersonagem> lista = new ArrayList<>();
        String sql = "SELECT * FROM log_personagem WHERE id_personagem = ? ORDER BY alterado_em DESC, id_log DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPersonagem);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LogPersonagem l = new LogPersonagem();
                    l.setIdLog(rs.getInt("id_log"));
                    l.setIdPersonagem(rs.getInt("id_personagem"));
                    l.setNivelAntigo(rs.getInt("nivel_antigo"));
                    l.setNivelNovo(rs.getInt("nivel_novo"));
                    l.setHpAntigo(rs.getInt("hp_antigo"));
                    l.setHpNovo(rs.getInt("hp_novo"));
                    l.setUsuario(rs.getString("usuario"));
                    l.setAlteradoEm(rs.getTimestamp("alterado_em"));
                    lista.add(l);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar histórico do personagem: " + e.getMessage(), e);
        }
        return lista;
    }
}

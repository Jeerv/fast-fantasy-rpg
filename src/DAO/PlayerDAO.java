package DAO;

import Model.Player;
import Model.StatusParticipante;
import Util.ConnectionFactory;

import java.sql.*;

public class PlayerDAO {

    public Player buscarPorUsuario(int codigoUsuario) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome_usuario, u.email, u.senha, u.premium, u.id_conquista, p.status "
                + "FROM player p JOIN usuario u ON u.id_usuario = p.id_usuario WHERE u.id_usuario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigoUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Player(rs.getInt("id_usuario"), rs.getString("nome_usuario"), rs.getString("email"),
                        rs.getString("senha"), rs.getBoolean("premium"), rs.getInt("id_conquista"),
                        StatusParticipante.doBanco(rs.getString("status")));
            }
        }
    }

    public void inserir(Player p) throws SQLException {
        String sql = "INSERT INTO player (status, id_usuario) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, p.getStatus().getValorBanco());
            ps.setInt(2, p.getCodigo());
            ps.executeUpdate();
        }
    }

    // Personagem.id_player aponta para player.id_player (não para o id do usuário).
    public int buscarIdPlayer(int codigoUsuario) throws SQLException {
        String sql = "SELECT id_player FROM player WHERE id_usuario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigoUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}

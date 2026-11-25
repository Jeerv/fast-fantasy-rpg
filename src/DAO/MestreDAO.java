package DAO;

import Model.Mestre;
import Model.StatusParticipante;
import Util.ConnectionFactory;

import java.sql.*;

public class MestreDAO {

    public Mestre buscarPorUsuario(int codigoUsuario) throws SQLException {
        String sql = "SELECT u.id_usuario, u.nome_usuario, u.email, u.senha, u.premium, u.id_conquista, m.status "
                + "FROM mestre m JOIN usuario u ON u.id_usuario = m.id_usuario WHERE u.id_usuario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigoUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Mestre(rs.getInt("id_usuario"), rs.getString("nome_usuario"), rs.getString("email"),
                        rs.getString("senha"), rs.getBoolean("premium"), rs.getInt("id_conquista"),
                        StatusParticipante.doBanco(rs.getString("status")));
            }
        }
    }

    // O usuário já deve existir (UsuarioDAO.inserir) e m.getCodigo() deve estar preenchido.
    public void inserir(Mestre m) throws SQLException {
        String sql = "INSERT INTO mestre (status, id_usuario) VALUES (?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getStatus().getValorBanco());
            ps.setInt(2, m.getCodigo());
            ps.executeUpdate();
        }
    }

    // Mesa.id_mestre aponta para mestre.id_mestre (não para o id do usuário).
    public int buscarIdMestre(int codigoUsuario) throws SQLException {
        String sql = "SELECT id_mestre FROM mestre WHERE id_usuario = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, codigoUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }
}

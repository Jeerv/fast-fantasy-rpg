package DAO;

import Model.Usuario;
import Util.ConnectionFactory;
import Util.SenhaUtil;

import java.sql.*;

public class UsuarioDAO {

    // Insere em usuario (senha vira hash) e devolve o id gerado. Chame ANTES de MestreDAO/PlayerDAO.inserir.
    public int inserir(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario (nome_usuario, email, senha, premium, id_conquista) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, SenhaUtil.gerarHash(u.getSenha()));
            ps.setBoolean(4, u.getPremium());
            if (u.getConquista() == 0) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, u.getConquista());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setCodigo(keys.getInt(1));
                    return u.getCodigo();
                }
            }
        }
        throw new SQLException("Falha ao obter o id do usuário");
    }

    // Login: devolve o id do usuário ou 0 se e-mail/senha estiverem errados.
    public int autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT id_usuario, senha FROM usuario WHERE email = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && SenhaUtil.verificar(senha, rs.getString("senha"))) {
                    return rs.getInt("id_usuario");
                }
            }
        }
        return 0;
    }

    public boolean emailExiste(String email) throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE email = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void deletar(int idUsuario) throws SQLException {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }
}

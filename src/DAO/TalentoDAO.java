package DAO;

import Model.Talento;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TalentoDAO {
    private Talento montar(ResultSet r) throws SQLException {
        Object id = r.getObject("id_criador");
        return new Talento(r.getInt("id_talento"), r.getString("nome"), r.getString("pre_requisito"), r.getString("descricao"), id == null ? null : ((Number) id).intValue());
    }

    public Talento buscarPorId(int id) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM talento WHERE id_talento=?")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? montar(r) : null;
            }
        }
    }

    public List<Talento> listarVisiveis(int idUsuario) throws SQLException {
        List<Talento> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM talento WHERE id_criador IS NULL OR id_criador=? ORDER BY nome")) {
            p.setInt(1, idUsuario);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) out.add(montar(r));
            }
        }
        return out;
    }

    public void inserir(Talento t) throws SQLException {
        String q = "INSERT INTO talento(nome,pre_requisito,descricao,id_criador) VALUES(?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, t.getNome());
            p.setString(2, t.getPreRequisito());
            p.setString(3, t.getDescricao());
            if (t.getIdCriador() == null) p.setNull(4, Types.INTEGER);
            else p.setInt(4, t.getIdCriador());
            p.executeUpdate();
            try (ResultSet k = p.getGeneratedKeys()) {
                if (k.next()) t.setIdTalento(k.getInt(1));
            }
        }
    }

    public boolean atualizar(Talento t, int idUsuario) throws SQLException {
        String q = "UPDATE talento SET nome=?,pre_requisito=?,descricao=? WHERE id_talento=? AND id_criador=?";
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
            p.setString(1, t.getNome());
            p.setString(2, t.getPreRequisito());
            p.setString(3, t.getDescricao());
            p.setInt(4, t.getIdTalento());
            p.setInt(5, idUsuario);
            return p.executeUpdate() > 0;
        }
    }

    public boolean deletar(int idTalento, int idUsuario) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM talento WHERE id_talento=? AND id_criador=?")) {
            p.setInt(1, idTalento);
            p.setInt(2, idUsuario);
            return p.executeUpdate() > 0;
        }
    }
}
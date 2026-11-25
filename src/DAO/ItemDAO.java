package DAO;

import Model.*;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    private Item montar(ResultSet rs) throws SQLException {
        return new Item(rs.getInt("id_item"), rs.getString("nome"), rs.getString("descricao"), rs.getInt("id_criador"));
    }

    public void inserir(Item o) throws SQLException {
        String sql = "INSERT INTO item (nome, descricao, id_criador) VALUES (?, ?, ?)";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            if (o.getIdCriador() == 0) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, o.getIdCriador());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setIdItem(keys.getInt(1));
            }
        }
    }

    public Item buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM item WHERE id_item = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montar(rs) : null;
            }
        }
    }

    public List<Item> listarTodos() throws SQLException {
        List<Item> lista = new ArrayList<>();
        String sql = "SELECT * FROM item";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        }
        return lista;
    }

    // So o criador altera. Itens/magias oficiais (id_criador NULL) nunca casam. false = nao existe ou nao e dele.
    public boolean atualizar(Item o, int idUsuario) throws SQLException {
        String sql = "UPDATE item SET nome = ?, descricao = ? WHERE id_item = ? AND id_criador = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, o.getNome());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getIdItem());
            ps.setInt(4, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    // So o criador apaga. false = nao existe, e oficial ou nao e dele.
    public boolean deletar(int id, int idUsuario) throws SQLException {
        String sql = "DELETE FROM item WHERE id_item = ? AND id_criador = ?";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    // Oficiais + as criadas pelo proprio usuario.
    public List<Item> listarVisiveis(int idUsuario) throws SQLException {
        List<Item> lista = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE id_criador IS NULL OR id_criador = ? ORDER BY nome";
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        }
        return lista;
    }
}

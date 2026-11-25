package DAO;

import Model.Antecedente;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntecedenteDAO {
    private Antecedente montar(ResultSet rs) throws SQLException {
        return new Antecedente(rs.getInt("id_antecedente"), rs.getString("nome"), rs.getString("descricao"), rs.getString("caracteristica_nome"), rs.getString("caracteristica_descricao"), rs.getInt("idiomas_adicionais"), rs.getString("ferramentas_proficiencia"), rs.getString("equipamento_inicial"));
    }

    public Antecedente buscarPorId(int id) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM antecedente WHERE id_antecedente=?")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? montar(r) : null;
            }
        }
    }

    public List<Antecedente> listarTodos() throws SQLException {
        List<Antecedente> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM antecedente ORDER BY nome"); ResultSet r = p.executeQuery()) {
            while (r.next()) out.add(montar(r));
        }
        return out;
    }

    public void inserir(Antecedente a) throws SQLException {
        String q = "INSERT INTO antecedente(nome,descricao,caracteristica_nome,caracteristica_descricao,idiomas_adicionais,ferramentas_proficiencia,equipamento_inicial) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement(q, Statement.RETURN_GENERATED_KEYS)) {
            preencher(p, a);
            p.executeUpdate();
            try (ResultSet k = p.getGeneratedKeys()) {
                if (k.next()) a.setIdAntecedente(k.getInt(1));
            }
        }
    }

    public void atualizar(Antecedente a) throws SQLException {
        String q = "UPDATE antecedente SET nome=?,descricao=?,caracteristica_nome=?,caracteristica_descricao=?,idiomas_adicionais=?,ferramentas_proficiencia=?,equipamento_inicial=? WHERE id_antecedente=?";
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement(q)) {
            preencher(p, a);
            p.setInt(8, a.getIdAntecedente());
            p.executeUpdate();
        }
    }

    private void preencher(PreparedStatement p, Antecedente a) throws SQLException {
        p.setString(1, a.getNome());
        p.setString(2, a.getDescricao());
        p.setString(3, a.getCaracteristicaNome());
        p.setString(4, a.getCaracteristicaDescricao());
        p.setInt(5, a.getIdiomasAdicionais());
        p.setString(6, a.getFerramentasProficiencia());
        p.setString(7, a.getEquipamentoInicial());
    }

    public void deletar(int id) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM antecedente WHERE id_antecedente=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }
}
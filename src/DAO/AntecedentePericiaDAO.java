package DAO;

import Model.AntecedentePericia;
import Util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntecedentePericiaDAO {
    public void vincular(int idAntecedente, int idPericia) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("INSERT INTO antecedente_pericia(id_antecedente,id_pericia) VALUES(?,?)")) {
            p.setInt(1, idAntecedente);
            p.setInt(2, idPericia);
            p.executeUpdate();
        }
    }

    public List<AntecedentePericia> listarPorAntecedente(int idAntecedente) throws SQLException {
        List<AntecedentePericia> out = new ArrayList<>();
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("SELECT id_antecedente,id_pericia FROM antecedente_pericia WHERE id_antecedente=? ORDER BY id_pericia")) {
            p.setInt(1, idAntecedente);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) out.add(new AntecedentePericia(r.getInt("id_antecedente"), r.getInt("id_pericia")));
            }
        }
        return out;
    }

    public void desvincular(int idAntecedente, int idPericia) throws SQLException {
        try (Connection c = ConnectionFactory.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM antecedente_pericia WHERE id_antecedente=? AND id_pericia=?")) {
            p.setInt(1, idAntecedente);
            p.setInt(2, idPericia);
            p.executeUpdate();
        }
    }
}
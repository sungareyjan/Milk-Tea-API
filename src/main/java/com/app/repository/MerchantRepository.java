package com.app.repository;

import com.app.model.Merchant;
import com.app.repository.impl.MerchantRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MerchantRepository implements MerchantRepositoryImpl {

    private final Connection connection;

    public MerchantRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Merchant findDefault() {
        String sql = "SELECT * FROM merchants ORDER BY id ASC LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return map(rs);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch default merchant", e);
        }
    }

    @Override
    public Merchant findByPublicId(String publicId) {
        String sql = "SELECT * FROM merchants WHERE public_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, publicId);
            ResultSet rs = ps.executeQuery();

            return rs.next() ? map(rs) : null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Merchant save(Merchant merchant) {
        // your insert logic here
        return merchant;
    }

    @Override
    public boolean update(Merchant merchant) {
        // your update logic here
        return true;
    }

    private Merchant map(ResultSet rs) throws SQLException {
        return Merchant.builder()
                .publicId(rs.getString("public_id"))
                .name(rs.getString("name"))
                .branch(rs.getString("branch"))
                .address(rs.getString("address"))
                .contactNumber(rs.getString("contact_number"))
                .build();
    }
}

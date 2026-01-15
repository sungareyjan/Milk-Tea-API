package com.app.repository;

import com.app.model.Merchant;
import com.app.repository.impl.MerchantRepositoryImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MerchantRepository implements MerchantRepositoryImpl {

    private final Connection connection;

    public MerchantRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Merchant findMerchantById(String publicId) {

        String query = """
            SELECT id, public_id, name, branch, address, contact_number
            FROM merchants
            WHERE public_id = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, publicId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch merchant", e);
        }
    }


    @Override
    public Merchant findMerchantFirst() {

        String query = """
            SELECT id, public_id, name, branch, address, contact_number
            FROM merchants
            ORDER BY id ASC
            LIMIT 1
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return mapRow(resultSet);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch default merchant", e);
        }

    }

    @Override
    public boolean updateMerchant(Merchant merchant) {

        String query = """
            UPDATE merchants
            SET name = ?, branch = ?, address = ?, contact_number = ?
            WHERE public_id = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, merchant.getName());
            preparedStatement.setString(2, merchant.getBranch());
            preparedStatement.setString(3, merchant.getAddress());
            preparedStatement.setString(4, merchant.getContactNumber());
            preparedStatement.setString(5, merchant.getPublicId());

            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update merchant", e);
        }
    }

    private Merchant mapRow(ResultSet rs) throws SQLException {
        Merchant merchant = new Merchant();
        merchant.setId(rs.getLong("id"));
        merchant.setPublicId(rs.getString("public_id"));
        merchant.setName(rs.getString("name"));
        merchant.setBranch(rs.getString("branch"));
        merchant.setAddress(rs.getString("address"));
        merchant.setContactNumber(rs.getString("contact_number"));
        return merchant;
    }
}

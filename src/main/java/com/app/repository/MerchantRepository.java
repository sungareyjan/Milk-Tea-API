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
    public Merchant findDefault() {
        String query = "SELECT * FROM merchants ORDER BY id ASC LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return map(resultSet);
            }
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch default merchant", e);
        }
    }

    @Override
    public Merchant findByPublicId(String publicId) {
        String query = "SELECT * FROM merchants WHERE public_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, publicId);
            ResultSet resultSet = ps.executeQuery();

            return resultSet.next() ? map(resultSet) : null;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Merchant save(Merchant merchant) {
        return merchant;
    }

    @Override
    public boolean update(Merchant merchant) {
        // your update logic here
        return true;
    }

    private Merchant map(ResultSet resultSet) throws SQLException {
        return Merchant.builder()
            .publicId(resultSet.getString("public_id"))
            .name(resultSet.getString("name"))
            .branch(resultSet.getString("branch"))
            .address(resultSet.getString("address"))
            .contactNumber(resultSet.getString("contact_number"))
            .build();
    }
}

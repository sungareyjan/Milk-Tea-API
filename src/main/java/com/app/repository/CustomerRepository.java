package com.app.repository;

import com.app.exception.DuplicateResourceException;
import com.app.model.Address;
import com.app.model.Customer;
import com.app.repository.impl.CustomerRepositoryImpl;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository implements CustomerRepositoryImpl {

    private final Connection connection;

    public CustomerRepository(Connection connection) {
        this.connection = connection;
    }

    public Customer create(Customer customer) {
        String customerSql = """
            INSERT INTO customers
            (first_name, middle_name, last_name, phone, email, public_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        String addressSql = """
            INSERT INTO customer_addresses
            (customer_id, street, barangay, city, province, postal_code)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try {
            connection.setAutoCommit(false);

            // 1️⃣ Insert customer and get ID
            long customerId = insertCustomer(customerSql, customer);
            customer.setId(customerId);

            // 2️⃣ Insert address if present
            Address address = customer.getAddress();
            if (address != null) {
                insertAddress(addressSql, customerId, address);
            }

            // 3️⃣ Commit transaction
            connection.commit();
            return customer;

        } catch (SQLIntegrityConstraintViolationException e) {
            rollbackQuietly();
            throw handleDuplicate(e, customer);

        } catch (Exception e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to create customer", e);

        } finally {
            resetAutoCommit();
        }
    }

    // ===== Helper Methods =====
    private long insertCustomer(String sql, Customer customer) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getMiddleName());
            stmt.setString(3, customer.getLastName());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getEmail());
            stmt.setString(6, customer.getPublicId());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("Failed to retrieve customer ID");
            }
        }
    }

    private void insertAddress(String sql, long customerId, Address address) throws SQLException {

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, customerId);
            stmt.setString(2, address.getStreet());
            stmt.setString(3, address.getBarangay());
            stmt.setString(4, address.getCity());
            stmt.setString(5, address.getProvince());
            stmt.setString(6, address.getPostalCode());

            stmt.executeUpdate();
        }
    }

    private void rollbackQuietly() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private void resetAutoCommit() {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
        }
    }

    private RuntimeException handleDuplicate(SQLIntegrityConstraintViolationException e, Customer customer) {
        String msg = e.getMessage().toLowerCase();

        if (msg.contains("email")) {
            return new DuplicateResourceException(
                "Email '" + customer.getEmail() + "' already exists."
            );
        }

        if (msg.contains("phone")) {
            return new DuplicateResourceException(
                "Phone '" + customer.getPhone() + "' already exists."
            );
        }

        if (msg.contains("public_id")) {
            return new DuplicateResourceException(
                "Public ID already exists."
            );
        }

        return new RuntimeException("Duplicate resource", e);
    }
    @Override
    public Customer findByPublicId(String publicId) {
        String sql = """
        SELECT
            c.id, c.public_id, c.first_name, c.middle_name, c.last_name,
            c.phone, c.email,
            a.street, a.barangay, a.city, a.province, a.postal_code
        FROM customers c
        LEFT JOIN customer_addresses a ON a.customer_id = c.id
        WHERE c.public_id = ? AND c.deleted_at IS NULL
        LIMIT 1
    """;

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, publicId);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                return null; // or throw NotFoundException
            }
            return mapCustomer(rs);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customer with public id " + publicId, e);
        }
    }

    @Override
    public List<Customer> findAll() {
        String sql = """
        SELECT
            c.id, c.public_id, c.first_name, c.middle_name, c.last_name,
            c.phone, c.email,
            a.street, a.barangay, a.city, a.province, a.postal_code
        FROM customers c
        LEFT JOIN customer_addresses a ON a.customer_id = c.id
        WHERE c.deleted_at IS NULL
        ORDER BY c.created_at DESC
    """;

        List<Customer> customers = new ArrayList<>();

        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                customers.add(mapCustomer(rs));
            }

            return customers;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customers", e);
        }
    }

    private Customer mapCustomer(ResultSet rs) throws SQLException {

        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setPublicId(rs.getString("public_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setMiddleName(rs.getString("middle_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setPhone(rs.getString("phone"));
        customer.setEmail(rs.getString("email"));

        if (rs.getString("street") != null) {
            Address address = new Address();
            address.setStreet(rs.getString("street"));
            address.setBarangay(rs.getString("barangay"));
            address.setCity(rs.getString("city"));
            address.setProvince(rs.getString("province"));
            address.setPostalCode(rs.getString("postal_code"));
            customer.setAddress(address);
        }

        return customer;
    }

    @Override
    public Customer update(String publicId, Customer customer) {
        String customerSql = """
        UPDATE customers
        SET first_name = ?, middle_name = ?, last_name = ?, phone = ?, email = ?
        WHERE public_id = ?
    """;

        String addressSql = """
        UPDATE customer_addresses
        SET street = ?, barangay = ?, city = ?, province = ?, postal_code = ?
        WHERE customer_id = ?
    """;

        try {
            connection.setAutoCommit(false);

            // Update customer
            try (PreparedStatement stmt = connection.prepareStatement(customerSql)) {
                stmt.setString(1, customer.getFirstName());
                stmt.setString(2, customer.getMiddleName());
                stmt.setString(3, customer.getLastName());
                stmt.setString(4, customer.getPhone());
                stmt.setString(5, customer.getEmail());
                stmt.setString(6, publicId);

                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new RuntimeException("Customer not found with ID " + customer.getId());
                }
            }

            // Update address (if exists)
            Address address = customer.getAddress();
            if (address != null) {
                try (PreparedStatement stmt = connection.prepareStatement(addressSql)) {
                    stmt.setString(1, address.getStreet());
                    stmt.setString(2, address.getBarangay());
                    stmt.setString(3, address.getCity());
                    stmt.setString(4, address.getProvince());
                    stmt.setString(5, address.getPostalCode());
                    stmt.setLong(6, customer.getId());

                    stmt.executeUpdate();
                }
            }

            connection.commit();
            return customer;

        } catch (SQLIntegrityConstraintViolationException e) {
            rollbackQuietly();
            throw handleDuplicate(e, customer);

        } catch (Exception e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to update customer", e);

        } finally {
            resetAutoCommit();
        }
    }
}

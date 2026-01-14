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

    public Customer insertCustomer(Customer customer) {
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

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, customer.getFirstName());
            preparedStatement.setString(2, customer.getMiddleName());
            preparedStatement.setString(3, customer.getLastName());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setString(5, customer.getEmail());
            preparedStatement.setString(6, customer.getPublicId());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
                throw new SQLException("Failed to retrieve customer ID");
            }
        }

    }

    private void insertAddress(String sql, long customerId, Address address) throws SQLException {

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, customerId);
            preparedStatement.setString(2, address.getStreet());
            preparedStatement.setString(3, address.getBarangay());
            preparedStatement.setString(4, address.getCity());
            preparedStatement.setString(5, address.getProvince());
            preparedStatement.setString(6, address.getPostalCode());

            preparedStatement.executeUpdate();
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

        String message = e.getMessage().toLowerCase();

        if (message.contains("email")) {
            return new DuplicateResourceException(
                "Email '" + customer.getEmail() + "' already exists."
            );
        }

        if (message.contains("phone")) {
            return new DuplicateResourceException(
                "Phone '" + customer.getPhone() + "' already exists."
            );
        }

        if (message.contains("public_id")) {
            return new DuplicateResourceException(
                "Public ID already exists."
            );
        }

        return new RuntimeException("Duplicate resource", e);

    }
    @Override
    public Customer findCustomerById(String publicId) {

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
            PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, publicId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null; // or throw NotFoundException
            }
            return mapCustomer(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customer with public id " + publicId, e);
        }
    }

    @Override
    public List<Customer> findAllCustomers() {
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
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                customers.add(mapCustomer(resultSet));
            }

            return customers;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch customers", e);
        }
    }

    private Customer mapCustomer(ResultSet resultSet) throws SQLException {

        Customer customer = new Customer();
        customer.setId(resultSet.getLong("id"));
        customer.setPublicId(resultSet.getString("public_id"));
        customer.setFirstName(resultSet.getString("first_name"));
        customer.setMiddleName(resultSet.getString("middle_name"));
        customer.setLastName(resultSet.getString("last_name"));
        customer.setPhone(resultSet.getString("phone"));
        customer.setEmail(resultSet.getString("email"));

        if (resultSet.getString("street") != null) {
            Address address = new Address();
            address.setStreet(resultSet.getString("street"));
            address.setBarangay(resultSet.getString("barangay"));
            address.setCity(resultSet.getString("city"));
            address.setProvince(resultSet.getString("province"));
            address.setPostalCode(resultSet.getString("postal_code"));
            customer.setAddress(address);
        }

        return customer;
    }

    @Override
    public Customer updateCustomer(String publicId, Customer customer) {

        String customerQuery = """
            UPDATE customers
            SET first_name = ?, middle_name = ?, last_name = ?, phone = ?, email = ?
            WHERE public_id = ?
        """;

        String addressQuery = """
            UPDATE customer_addresses
            SET street = ?, barangay = ?, city = ?, province = ?, postal_code = ?
            WHERE customer_id = ?
        """;

        try {
            connection.setAutoCommit(false);

            // Update customer
            try (PreparedStatement preparedStatement = connection.prepareStatement(customerQuery)) {
                preparedStatement.setString(1, customer.getFirstName());
                preparedStatement.setString(2, customer.getMiddleName());
                preparedStatement.setString(3, customer.getLastName());
                preparedStatement.setString(4, customer.getPhone());
                preparedStatement.setString(5, customer.getEmail());
                preparedStatement.setString(6, publicId);

                int updated = preparedStatement.executeUpdate();
                if (updated == 0) {
                    throw new RuntimeException("Customer not found with ID " + customer.getId());
                }
            }

            // Update address (if exists)
            Address address = customer.getAddress();
            if (address != null) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(addressQuery)) {
                    preparedStatement.setString(1, address.getStreet());
                    preparedStatement.setString(2, address.getBarangay());
                    preparedStatement.setString(3, address.getCity());
                    preparedStatement.setString(4, address.getProvince());
                    preparedStatement.setString(5, address.getPostalCode());
                    preparedStatement.setLong(6, customer.getId());

                    preparedStatement.executeUpdate();
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

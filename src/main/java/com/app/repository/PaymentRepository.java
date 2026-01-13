package com.app.repository;

import com.app.model.Payment;
import com.app.model.enums.PaymentStatus;
import com.app.repository.impl.PaymentRepositoryImpl;

import java.sql.*;

public class PaymentRepository implements PaymentRepositoryImpl {

    private final Connection connection;

    public PaymentRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Payment save(Payment payment) {
        String sql = """
            INSERT INTO payments
            (public_id, order_id, payment_method_name, payment_method_description,
             amount_paid, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, payment.getPublicId());
            ps.setString(2, payment.getPublicOrderId());
            ps.setString(3, payment.getPaymentMethodName());
            ps.setString(4, payment.getPaymentMethodDescription());
            ps.setBigDecimal(5, payment.getAmountPaid());
            ps.setString(6, payment.getStatus().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    payment.setId(rs.getLong(1));
                }
            }

            return payment;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create payment", e);
        }
    }

    @Override
    public Payment findByPublicId(String publicId) {
        String sql = "SELECT * FROM payments WHERE public_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, publicId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            Payment payment = new Payment();
            payment.setId(rs.getLong("id"));
            payment.setPublicId(rs.getString("public_id"));
            payment.setPublicOrderId(rs.getString("order_id"));
            payment.setPaymentMethodName(rs.getString("payment_method_name"));
            payment.setPaymentMethodDescription(rs.getString("payment_method_description"));
            payment.setAmountPaid(rs.getBigDecimal("amount_paid"));
            payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));

            return payment;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch payment", e);
        }
    }

    @Override
    public Payment updateStatus(String publicId, String status) {
        String sql = "UPDATE payments SET status = ? WHERE public_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, publicId);

            int updated = ps.executeUpdate();
            if (updated == 0) return null;

            return findByPublicId(publicId);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update payment status", e);
        }
    }
}

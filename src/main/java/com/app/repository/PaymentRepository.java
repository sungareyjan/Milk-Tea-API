package com.app.repository;

import com.app.exception.BusinessException;
import com.app.model.Order;
import com.app.model.Payment;
import com.app.model.enums.PaymentStatus;
import com.app.repository.impl.OrderRepositoryImpl;
import com.app.repository.impl.PaymentRepositoryImpl;

import java.sql.*;

public class PaymentRepository implements PaymentRepositoryImpl {

    private final Connection connection;
    private final OrderRepositoryImpl orderRepository;

    public PaymentRepository(Connection connection, OrderRepositoryImpl orderRepository) {
        this.connection = connection;
        this.orderRepository = orderRepository;
    }

    @Override
    public Payment insertPayment(Payment payment) {

        String sql = """
        INSERT INTO payments
            (public_id, public_order_id, payment_method_name, payment_method_description,
             amount_paid, status)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        Order order = orderRepository.findOrderById(payment.getPublicOrderId());

        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        if (payment.getAmountPaid().compareTo(order.getTotalAmount()) != 0) {
            throw new BusinessException("Amount paid must be equal to order total amount");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, payment.getPublicId());
            preparedStatement.setString(2, payment.getPublicOrderId());
            preparedStatement.setString(3, payment.getPaymentMethodName());
            preparedStatement.setString(4, payment.getPaymentMethodDescription());
            preparedStatement.setBigDecimal(5, payment.getAmountPaid());
            preparedStatement.setString(6, payment.getStatus().name());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    payment.setId(resultSet.getLong(1));
                }
            }

            return payment;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create payment", e);
        }
    }


    @Override
    public Payment findPaymentById(String publicId) {

        String query = "SELECT * FROM payments WHERE public_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, publicId);
            ResultSet rs = preparedStatement.executeQuery();

            if (!rs.next()) return null;

            Payment payment = new Payment();
            payment.setId(rs.getLong("id"));
            payment.setPublicId(rs.getString("public_id"));
            payment.setPublicOrderId(rs.getString("public_order_id"));
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
    public Payment updatePaymentStatus(String publicId, String status) {

        String query = "UPDATE payments SET status = ? WHERE public_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, publicId);

            int updated = preparedStatement.executeUpdate();
            if (updated == 0) return null;

            return findPaymentById(publicId);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update payment status", e);
        }

    }
}

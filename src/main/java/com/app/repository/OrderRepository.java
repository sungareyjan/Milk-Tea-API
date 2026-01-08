package com.app.repository;

import com.app.model.Order;
import com.app.model.OrderItem;
import com.app.model.enums.OrderStatus;
import com.app.repository.impl.OrderRepositoryImpl;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderRepository implements OrderRepositoryImpl {

    private final Connection connection;

    public OrderRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Order create(Order order) {
        String orderSql = """
        INSERT INTO orders
        (public_id, public_customer_id, total_amount, status, created_by)
        VALUES (?, ?, ?, ?, ?)
    """;

        String itemSql = """
        INSERT INTO order_items
        (order_id, product_id, quantity, unit_price, subtotal, size, measure, measurement)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try {
            connection.setAutoCommit(false);

            // 1. Generate public ID and set default status
            order.setPublicId(UUID.randomUUID().toString());
            order.setStatus(OrderStatus.PENDING);

            // Initially, totalAmount is 0 for insertion
            order.setTotalAmount(BigDecimal.ZERO);

            // 2. Insert order into DB (total_amount = 0 for now)
            long orderId = insertOrder(orderSql, order);
            order.setId(orderId);

            BigDecimal total = BigDecimal.ZERO;

            // 3. Insert items and calculate subtotal
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    item.setOrderId(orderId);

                    // Calculate subtotal
                    BigDecimal subtotal = item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    item.setSubtotal(subtotal);

                    // Add to total
                    total = total.add(subtotal);

                    // Insert item into DB
                    insertOrderItem(itemSql, item);
                }
            }

            // 4. Update order total in DB
            updateOrderTotal(orderId, total);

            // 5. Set total in returned object
            order.setTotalAmount(total);

            connection.commit();
            return order;

        } catch (Exception e) {
            rollbackQuietly();
            throw new RuntimeException("Failed to create order", e);
        } finally {
            resetAutoCommit();
        }
    }


    @Override
    public Order findByPublicId(String publicId) {

        String orderSql = """
            SELECT id, public_id, public_customer_id, total_amount, status,
                   created_by, created_at, updated_at
            FROM orders
            WHERE public_id = ?
            LIMIT 1
        """;

        String itemsSql = """
            SELECT id, product_id, quantity, unit_price, subtotal,
                   created_at, updated_at
            FROM order_items
            WHERE order_id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(orderSql)) {
            stmt.setString(1, publicId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setPublicId(rs.getString("public_id"));
                order.setPublicCustomerId(rs.getString("public_customer_id"));
                order.setTotalAmount(rs.getBigDecimal("total_amount"));
                order.setStatus(OrderStatus.fromString(rs.getString("status")));
                order.setCreatedBy(rs.getString("created_by"));

                // Fetch order items
                try (PreparedStatement itemStmt = connection.prepareStatement(itemsSql)) {
                    itemStmt.setLong(1, order.getId());

                    try (ResultSet itemsRs = itemStmt.executeQuery()) {
                        List<OrderItem> items = new ArrayList<>();

                        while (itemsRs.next()) {
                            OrderItem item = new OrderItem();
                            item.setId(itemsRs.getLong("id"));
                            item.setOrderId(order.getId());
                            item.setProductId(itemsRs.getLong("product_id"));
                            item.setQuantity(itemsRs.getInt("quantity"));
                            item.setUnitPrice(itemsRs.getBigDecimal("unit_price"));
                            item.setSubtotal(itemsRs.getBigDecimal("subtotal"));

                            items.add(item);
                        }
                        order.setItems(items);
                    }
                }

                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch order", e);
        }
    }

    @Override
    public List<Order> findAll() {
        String orderSql = "SELECT public_id FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(orderSql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(findByPublicId(rs.getString("public_id")));
            }

            return orders;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all orders", e);
        }
    }

    @Override
    public Order updateStatus(String publicId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE public_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, publicId);
            int updated = stmt.executeUpdate();
            if (updated == 0) return null;
            return findByPublicId(publicId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status", e);
        }
    }

    /* --- Helper methods --- */
    private long insertOrder(String sql, Order order) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, order.getPublicId());
            stmt.setString(2, order.getPublicCustomerId());
            stmt.setBigDecimal(3, BigDecimal.ZERO);
            stmt.setString(4, order.getStatus().name());
            stmt.setString(5, order.getCreatedBy());

            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
                throw new SQLException("Failed to get order ID");
            }
        }
    }

    private void insertOrderItem(String sql, OrderItem item) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, item.getOrderId());
            stmt.setLong(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getUnitPrice());
            stmt.setBigDecimal(5, item.getSubtotal());
            stmt.setString(6, item.getSize());
            stmt.setString(7, item.getMeasure());
            stmt.setBigDecimal(8, item.getMeasurement());

            stmt.executeUpdate();
        }
    }

    private void updateOrderTotal(long orderId, BigDecimal total) throws SQLException {
        String sql = "UPDATE orders SET total_amount = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, total);
            stmt.setLong(2, orderId);
            stmt.executeUpdate();
        }
    }

    private void rollbackQuietly() {
        try { connection.rollback(); } catch (SQLException ignored) {}
    }

    private void resetAutoCommit() {
        try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
    }
}

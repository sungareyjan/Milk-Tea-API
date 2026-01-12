package com.app.repository;

import com.app.model.Merchant;
import com.app.model.Order;
import com.app.model.OrderItem;
import com.app.model.enums.OrderStatus;
import com.app.model.enums.Size;
import com.app.repository.impl.OrderRepositoryImpl;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
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
            (public_id, public_customer_id, created_by, status, total_amount, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        String itemSql = """
            INSERT INTO order_items
            (order_id, product_id, product_name, quantity, unit_price, subtotal, size, unit, measurement)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            connection.setAutoCommit(false);

            // ---------- CALCULATE SUBTOTALS & TOTAL ----------
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem item : order.getItems()) {
                BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                item.setSubtotal(subtotal);
                totalAmount = totalAmount.add(subtotal);
            }
            order.setTotalAmount(totalAmount);

            // ---------- INSERT ORDER ----------
            String publicId = UUID.randomUUID().toString();
            try (PreparedStatement ps = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, publicId);
                ps.setString(2, order.getPublicCustomerId());
                ps.setString(3, order.getCreatedBy());
                ps.setString(4, OrderStatus.PENDING.name());
                ps.setBigDecimal(5, totalAmount);
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        order.setId(rs.getLong(1));
                        order.setPublicId(publicId);
                        order.setStatus(OrderStatus.PENDING);
                        order.setCreatedAt(LocalDateTime.now());
                    }
                }
            }

            // ---------- INSERT ORDER ITEMS ----------
            try (PreparedStatement ps = connection.prepareStatement(itemSql)) {
                for (OrderItem item : order.getItems()) {
                    ps.setLong(1, order.getId());
                    ps.setLong(2, item.getProductId());
                    ps.setString(3, item.getProductName());
                    ps.setInt(4, item.getQuantity());
                    ps.setBigDecimal(5, item.getUnitPrice());
                    ps.setBigDecimal(6, item.getSubtotal());
                    ps.setString(7, item.getSize() != null ? item.getSize().name() : Size.LARGE.name());
                    ps.setString(8, item.getUnit());
                    ps.setBigDecimal(9, item.getMeasurement());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            connection.commit();
            return order;

        } catch (SQLException e) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("Failed to create order", e);
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
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
            SELECT id, product_id, product_name, quantity, unit_price, subtotal, size, unit, measurement
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
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

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
                            item.setProductName(itemsRs.getString("product_name"));
                            item.setQuantity(itemsRs.getInt("quantity"));
                            item.setUnitPrice(itemsRs.getBigDecimal("unit_price"));
                            item.setSubtotal(itemsRs.getBigDecimal("subtotal"));
                            item.setSize(Size.valueOf(itemsRs.getString("size")));
                            item.setUnit(itemsRs.getString("unit"));
                            item.setMeasurement(itemsRs.getBigDecimal("measurement"));

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
}

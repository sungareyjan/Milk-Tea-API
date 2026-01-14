package com.app.repository;

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
    public Order insertOrder(Order order) {

        String orderQuery = """
            INSERT INTO orders
            (public_id, public_customer_id, created_by, status,delivery_fee,service_fee,discount, total_amount, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String itemQuery = """
            INSERT INTO order_items
            (order_id, product_id, product_name, product_description,
                category_name, category_description,
                quantity, unit_price, subtotal, size, unit, measurement)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try {
            connection.setAutoCommit(false);

            // Use totalAmount and subtotals directly from frontend JSON
            BigDecimal totalAmount = order.getTotalAmount();

            // ---------- INSERT ORDER ----------
            String publicId = UUID.randomUUID().toString();
            try (PreparedStatement preparedStatement = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, publicId);
                preparedStatement.setString(2, order.getPublicCustomerId());
                preparedStatement.setString(3, order.getCreatedBy());
                preparedStatement.setString(4, OrderStatus.PENDING.name());
                preparedStatement.setBigDecimal(5, order.getDeliveryFee());
                preparedStatement.setBigDecimal(6, order.getServiceFee());
                preparedStatement.setBigDecimal(7, order.getDiscount());
                preparedStatement.setBigDecimal(8, totalAmount);
                preparedStatement.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

                preparedStatement.executeUpdate();

                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        order.setId(resultSet.getLong(1));
                        order.setPublicId(publicId);
                        order.setStatus(OrderStatus.PENDING);
                        order.setCreatedAt(LocalDateTime.now());
                    }
                }
            }

            // ---------- INSERT ORDER ITEMS ----------
            try (PreparedStatement preparedStatement = connection.prepareStatement(itemQuery)) {
                for (OrderItem item : order.getItems()) {
                    preparedStatement.setLong(1, order.getId());
                    preparedStatement.setLong(2, item.getProductId());
                    preparedStatement.setString(3, item.getProductName());
                    preparedStatement.setString(4, item.getProductDescription());
                    preparedStatement.setString(5, item.getProductCategory());
                    preparedStatement.setString(6, item.getProductCategoryDescription());
                    preparedStatement.setInt(7, item.getQuantity());
                    preparedStatement.setBigDecimal(8, item.getUnitPrice());
                    preparedStatement.setBigDecimal(9, item.getSubtotal()); // already computed
                    preparedStatement.setString(10, item.getSize().name());
                    preparedStatement.setString(11, item.getUnit());
                    preparedStatement.setBigDecimal(12, item.getMeasurement());
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
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
    public Order findOrderById(String publicId) {

        String orderQuery = """
            SELECT id, public_id, public_customer_id, total_amount, status,
                   created_by, created_at, updated_at
            FROM orders
            WHERE public_id = ?
            LIMIT 1
        """;

        String itemsQuery = """
            SELECT id, product_id, product_name, quantity, unit_price, subtotal, size, unit, measurement
            FROM order_items
            WHERE order_id = ?
        """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(orderQuery)) {
            preparedStatement.setString(1, publicId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) return null;

                Order order = new Order();
                order.setId(resultSet.getLong("id"));
                order.setPublicId(resultSet.getString("public_id"));
                order.setPublicCustomerId(resultSet.getString("public_customer_id"));
                order.setTotalAmount(resultSet.getBigDecimal("total_amount"));
                order.setStatus(OrderStatus.fromString(resultSet.getString("status")));
                order.setCreatedBy(resultSet.getString("created_by"));
                order.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());

                // Fetch order items
                try (PreparedStatement preparedStatementItem = connection.prepareStatement(itemsQuery)) {
                    preparedStatementItem.setLong(1, order.getId());
                    try (ResultSet itemsResultSet = preparedStatementItem.executeQuery()) {
                        List<OrderItem> items = new ArrayList<>();
                        while (itemsResultSet.next()) {
                            OrderItem item = new OrderItem();
                            item.setId(itemsResultSet.getLong("id"));
                            item.setOrderId(order.getId());
                            item.setProductId(itemsResultSet.getLong("product_id"));
                            item.setProductName(itemsResultSet.getString("product_name"));
                            item.setQuantity(itemsResultSet.getInt("quantity"));
                            item.setUnitPrice(itemsResultSet.getBigDecimal("unit_price"));
                            item.setSubtotal(itemsResultSet.getBigDecimal("subtotal"));
                            item.setSize(Size.valueOf(itemsResultSet.getString("size")));
                            item.setUnit(itemsResultSet.getString("unit"));
                            item.setMeasurement(itemsResultSet.getBigDecimal("measurement"));

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
    public List<Order> findAllOrders() {

        String orderQuery = "SELECT public_id FROM orders ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(orderQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                orders.add(findOrderById(resultSet.getString("public_id")));
            }

            return orders;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all orders", e);
        }

    }

    @Override
    public Order updateOrderStatus(String publicId, String status) {

        String sql = "UPDATE orders SET status = ? WHERE public_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status);
            preparedStatement.setString(2, publicId);
            int updated = preparedStatement.executeUpdate();
            if (updated == 0) return null;
            return findOrderById(publicId);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update order status", e);
        }

    }
}

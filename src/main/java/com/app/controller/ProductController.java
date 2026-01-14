//package com.app.controller;
//
//import com.app.model.Product;
//import com.app.service.ProductService;
//import io.javalin.http.Context;
//import io.javalin.http.HttpStatus;
//
//public class ProductController {
//
//    private final ProductService productService;
//
//    public ProductController(ProductService productService) {
//        this.productService = productService;
//    }
//
//    public void getAll(Context ctx) throws Exception {
//        ctx.json(productService.getAllProducts());
//    }
//
//    public void getById(Context ctx) throws Exception {
//        int id = Integer.parseInt(ctx.pathParam("id"));
//
//        Product product = productService.product(id);
//        if (product == null) {
//            ctx.status(HttpStatus.NOT_FOUND)
//                    .json("Product not found");
//            return;
//        }
//
//        ctx.json(product);
//    }
//
//    public void create(Context ctx) throws Exception {
//        Product product = ctx.bodyAsClass(Product.class);
//        Product created = productService.create(product);
//
//        ctx.status(HttpStatus.CREATED).json(created);
//    }
//
//    public void update(Context ctx) throws Exception {
//        int id = Integer.parseInt(ctx.pathParam("id"));
//        Product product = ctx.bodyAsClass(Product.class);
//        product.setId(id);
//
//        productService.update(product);
//        ctx.json(product);
//    }
//}

package com.app.controller;

import com.app.model.Product;
import com.app.service.ProductService;
import io.javalin.http.Context;

public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    public void createProduct(Context context) {
        Product product = context.bodyAsClass(Product.class);
        context.status(201).json(service.createProduct(product));
    }

    public void getAllProducts(Context ctx) {
        ctx.json(service.getAllProducts());
    }

    public void getProductById(Context context) {
        long id = Long.parseLong(context.pathParam("id"));
        Product product = service.getProductById(id);
        if (product == null) {
            context.status(404).json("Product not found");
            return;
        }
        context.json(product);
    }

    public void updateProduct(Context context) {
        long id = Long.parseLong(context.pathParam("id"));
        Product product = context.bodyAsClass(Product.class);
        context.json(service.updateProduct(id, product));
    }

    public void delete(Context context) {
        long id = Long.parseLong(context.pathParam("id"));
        service.delete(id);
        context.status(204);
    }
}

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

    public void create(Context ctx) {
        Product product = ctx.bodyAsClass(Product.class);
        ctx.status(201).json(service.create(product));
    }

    public void getAll(Context ctx) {
        ctx.json(service.getAll());
    }

    public void getById(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Product product = service.getById(id);
        if (product == null) {
            ctx.status(404).json("Product not found");
            return;
        }
        ctx.json(product);
    }

    public void update(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Product product = ctx.bodyAsClass(Product.class);
        ctx.json(service.update(id, product));
    }

    public void delete(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        service.delete(id);
        ctx.status(204);
    }
}

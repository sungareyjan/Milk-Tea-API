lone the repository:

git clone <repo-url>
cd milktea-api


Install dependencies:

npm install


Create .env file:

PORT=8000
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=yourpassword
DB_NAME=milktea_db
JWT_SECRET=your_jwt_secret


Run database migrations & seeders:

npx sequelize db:migrate
npx sequelize db:seed:all


Start server:

npm run dev


API will run on http://localhost:8000.

Authentication
Login

POST /login

Body (x-www-form-urlencoded):

Key	Value
username	admin
password	milktea

Response:

{
"token": "<JWT_TOKEN>"
}

Logout

GET /logout

Requires Bearer token.

Users
Get all users

GET /api/users

Headers: Authorization: Bearer <JWT_TOKEN>

Response: Array of user objects.

Get specific user

GET /api/users/:id

Headers: Authorization: Bearer <JWT_TOKEN>

Response: User object.

Create user

POST /api/users

Headers: Authorization: Bearer <JWT_TOKEN>

Body (JSON):

{
"username": "Tea Lattes",
"password": "password",
"email": "email@example.com",
"firstName": "firstName",
"middleName": "middleName",
"lastName": "lastName",
"roleId": 1
}

Update user

PUT /api/users/:id

Headers: Authorization: Bearer <JWT_TOKEN>

Body (JSON): same as create.

Product Categories
Get all categories

GET /api/product-categories

Headers: Authorization: Bearer <JWT_TOKEN>

Get specific category

GET /api/product-categories/:id

No auth required

Create category

POST /api/product-categories

Body (JSON):

{
"name": "Tea Lattes",
"description": "Special tea latte drinks"
}

Update category

PUT /api/product-categories/:id

Body (JSON):

{
"name": "Classic Milk Tea",
"description": "Traditional milk tea flavors (Organic)"
}

Delete category (soft delete)

PATCH /api/product-categories/:id

Products
Get all products

GET /api/products

Headers: Authorization: Bearer <JWT_TOKEN>

Get specific product

GET /api/products/:id

Create product

POST /api/products

Headers: Authorization: Bearer <JWT_TOKEN>

Update product

PUT /api/products/:id

Body (JSON):

{
"name": "Classic Milk Tea",
"description": "Black tea with milk",
"categoryId": 1,
"sizeId": 1,
"price": 89.00,
"availability": true
}

Customers
Create customer

POST /api/customer

Body (JSON):

{
"firstName": "Juan3",
"lastName": "Dela Cruz",
"phone": "091712345673",
"email": "juan3@email.com",
"address": {
"street": "Guiao",
"barangay": "Brgy. Sto. Rosario",
"city": "Angeles City",
"province": "Pampanga",
"postalCode": "2009"
}
}

Get all customers

GET /api/customer

Get specific customer

GET /api/customer/:id

Update customer

PUT /api/customer/:id

Body (JSON): same as create.

Orders
Create order

POST /api/orders

Body (JSON):

{
"publicCustomerId": "<UUID>",
"createdBy": "<UUID>",
"items": [
{
"productId": 1,
"quantity": 1,
"productName": "Ssss",
"productDescription": "product_description",
"productCategory": "categoryName",
"productCategoryDescription": "categoryDescription",
"unitPrice": 150.50,
"size": "LARGE",
"unit": "ml",
"measurement": 500,
"subtotal": 301.0
}
],
"deliveryFee": 50.0,
"serviceFee": 10.0,
"discount": 10.0,
"totalAmount": 660.99,
"merchant": {
"name": "RJ Codes Elit Milk Tea",
"branch": "Main Branch",
"address": "123 Street, City",
"contactNumber": "+63 912 345 6789"
}
}

Get orders

GET /api/orders

Get specific order

GET /api/orders/:id

Update order status

PATCH /api/orders/:id/status

Body (JSON):

{
"status": "PAID"
}

Payments
Create payment

POST /api/payments

Body (JSON):

{
"publicOrderId": "<UUID>",
"paymentMethodName": "CASH",
"paymentMethodDescription": "GCash mobile wallet",
"amountPaid": 400.0,
"status": "PAID"
}

Get specific payment

GET /api/payments/:id

Update payment status

PATCH /api/payments/:id/status

Body (JSON):

{
"status": "REFUNDED"
}

Test / Hello

GET /api/hello

No auth required

Test endpoint for server connectivity.
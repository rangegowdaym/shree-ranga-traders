# Shree Ranga Traders - Spring Boot Application

A complete Java Spring Boot web-based application for managing trading operations including purchases, sales, and payment tracking.

## Features

### 1. Purchase Management
- Add, update, and delete purchases
- Track shop transactions with date, items, bags, amount, and discount
- Search purchases by date range and shop name
- Auto-calculate net amount (amount - discount)
- Display totals for bags and net amount
- Auto-load recent 100 purchases

### 2. Sales Management
- Add, update, and delete sales
- Record customer transactions with date, item, bags, and amount
- Support for CREDIT and CASH payment types
- Search sales by date range and customer name
- Automatic balance updates for CREDIT sales
- Display totals for bags and amount

### 3. Sales Payment Management
- View all customer balances
- View payment transaction history
- Filter history by customer
- Record customer payments
- Real-time balance updates
- Transaction type indicators (SALE/PAYMENT/ADJUSTMENT)

### 4. Purchase Payment Management
- View all shop balances
- View payment transaction history
- Filter history by shop
- Record shop payments
- Real-time balance updates
- Transaction type indicators (PURCHASE/PAYMENT/ADJUSTMENT)

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: MySQL 8.0+
- **ORM**: Spring Data JPA / Hibernate
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Frontend**: Bootstrap 5, JavaScript

## Project Structure

```
shree-ranga-traders/
├── src/
│   ├── main/
│   │   ├── java/com/shreerangatraders/
│   │   │   ├── controller/         # REST and Web controllers
│   │   │   ├── entity/             # JPA entities
│   │   │   ├── repository/         # Spring Data repositories
│   │   │   ├── service/            # Business logic
│   │   │   └── ShreeRangaTradersApplication.java
│   │   └── resources/
│   │       ├── templates/          # Thymeleaf HTML templates
│   │       ├── application.properties
│   │       └── schema.sql          # Database schema
│   └── test/
├── pom.xml
└── README.md
```

## Database Schema

### Tables
- **customer**: Customer information
- **shop**: Shop information
- **item**: Item master data
- **purchase**: Purchase transactions
- **sales**: Sales transactions
- **salespayment**: Customer balance tracking
- **payment_history**: Customer payment history
- **payment**: Shop balance tracking
- **purchasepayment_history**: Shop payment history

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/rangegowdaym/shree-ranga-traders.git
cd shree-ranga-traders
```

### 2. Database Setup

#### Option A: Automatic Database Creation (Recommended)
The application is configured to automatically create the database. Just ensure MySQL is running.

#### Option B: Manual Database Creation
```sql
CREATE DATABASE shreerangatraders;
```

Then run the schema file:
```bash
mysql -u root -p shreerangatraders < src/main/resources/schema.sql
```

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shreerangatraders?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

Update the username and password according to your MySQL configuration.

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:
```bash
java -jar target/shree-ranga-traders-1.0.0.jar
```

### 6. Access the Application

Open your web browser and navigate to:
```
http://localhost:8080
```

## Application Usage

### Home Page
- Navigate to different modules from the home page
- Access Purchases, Sales, Sales Payments, and Purchase Payments

### Purchase Management
1. Click on "Purchases" from the navigation menu
2. Fill in purchase details (date, shop name, items, bags, amount, discount)
3. Click "Save" to create a purchase
4. Use the search form to filter purchases by date range or shop name
5. Click "Edit" to modify a purchase
6. Click "Delete" to remove a purchase (with confirmation)

### Sales Management
1. Click on "Sales" from the navigation menu
2. Fill in sale details (date, customer name, item, bags, amount, payment type)
3. Select payment type (CREDIT or CASH)
4. Click "Save" to create a sale
5. For CREDIT sales, customer balance is automatically updated
6. Use the search form to filter sales
7. Edit or delete sales as needed

### Sales Payment Management
1. Click on "Sales Payments" from the navigation menu
2. View all customer balances in the top table
3. Click on a customer row to filter their transaction history
4. To record a payment:
   - Enter customer name, amount, payment date, and optional note
   - Click "Record Payment"
   - Customer balance is automatically updated
5. View complete payment history in the bottom table

### Purchase Payment Management
1. Click on "Purchase Payments" from the navigation menu
2. View all shop balances in the top table
3. Click on a shop row to filter their transaction history
4. To record a payment:
   - Enter shop name, amount, payment date, and optional note
   - Click "Record Payment"
   - Shop balance is automatically updated
5. View complete payment history in the bottom table

## API Endpoints

### Purchase APIs
- `POST /api/purchases` - Create purchase
- `PUT /api/purchases/{id}` - Update purchase
- `DELETE /api/purchases/{id}` - Delete purchase
- `GET /api/purchases/{id}` - Get purchase by ID
- `GET /api/purchases/recent` - Get recent 100 purchases
- `GET /api/purchases/search` - Search purchases

### Sales APIs
- `POST /api/sales` - Create sale
- `PUT /api/sales/{id}` - Update sale
- `DELETE /api/sales/{id}` - Delete sale
- `GET /api/sales/{id}` - Get sale by ID
- `GET /api/sales/recent` - Get recent 100 sales
- `GET /api/sales/search` - Search sales

### Sales Payment APIs
- `GET /api/sales-payments/balances` - Get all customer balances
- `GET /api/sales-payments/history` - Get all payment history
- `GET /api/sales-payments/history/{customerName}` - Get history by customer
- `POST /api/sales-payments/payment` - Record payment

### Purchase Payment APIs
- `GET /api/purchase-payments/balances` - Get all shop balances
- `GET /api/purchase-payments/history` - Get all payment history
- `GET /api/purchase-payments/history/{shopName}` - Get history by shop
- `POST /api/purchase-payments/payment` - Record payment

## Business Logic

### Credit Sales
- When a CREDIT sale is created, customer balance increases
- When a CREDIT sale is updated, balance is adjusted by the difference
- When a CREDIT sale is deleted, balance is reversed
- All changes are recorded in payment_history

### Purchases
- When a purchase is created, shop balance increases by net amount (amount - discount)
- When a purchase is updated, balance is adjusted by the difference
- When a purchase is deleted, balance is reversed
- All changes are recorded in purchasepayment_history

### Payments
- Payments decrease the respective balances (customer or shop)
- All payments are recorded in history tables
- Payment history shows transaction type, amount, and balance after transaction

## Configuration

### Server Port
Default port is 8080. To change, edit `application.properties`:
```properties
server.port=9090
```

### Database
Configure JPA settings in `application.properties`:
```properties
spring.jpa.hibernate.ddl-auto=update  # Can be: create, update, validate, none
spring.jpa.show-sql=true              # Show SQL queries in logs
```

### Logging
Configure logging levels in `application.properties`:
```properties
logging.level.org.springframework.web=INFO
logging.level.com.shreerangatraders=DEBUG
```

## Troubleshooting

### Database Connection Issues
- Ensure MySQL is running
- Verify database credentials in `application.properties`
- Check if the database exists

### Port Already in Use
- Change the server port in `application.properties`
- Or stop the application using the port

### Build Failures
- Ensure Java 17 is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Clear Maven cache: `mvn clean`

## Development

### Adding New Features
1. Create entity in `entity` package
2. Create repository interface in `repository` package
3. Implement service in `service` package
4. Create controller in `controller` package
5. Add Thymeleaf template in `templates` folder

### Running Tests
```bash
mvn test
```

## License

This project is proprietary software for Shree Ranga Traders.

## Support

For issues or questions, please contact the development team.

## Version History

### Version 1.0.0 (Initial Release)
- Complete purchase management
- Complete sales management
- Sales payment tracking
- Purchase payment tracking
- Responsive web interface
- REST API support

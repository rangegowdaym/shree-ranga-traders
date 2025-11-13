# Understanding Controllers in Spring Boot

## What Are Controllers?

**Controllers** are the "traffic managers" of your web application. They act as the **bridge between the user interface (frontend) and your business logic (backend)**.

Think of them like a **restaurant waiter**:
- 🍽️ Takes orders from customers (receives HTTP requests)
- 🏃 Brings orders to the kitchen (calls service layer)
- 📦 Delivers food to customers (returns responses)

---

## How Controllers Work in Your Application

### **The Request-Response Flow**

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER BROWSER                             │
│  http://localhost:8090/sales                                     │
│  User clicks "Search" button with customer name                  │
└────────────────────┬────────────────────────────────────────────┘
                     │ HTTP Request
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      🎯 CONTROLLER LAYER                         │
│  @GetMapping("/api/sales/search")                               │
│  • Receives the HTTP request                                     │
│  • Extracts parameters (customerName, dates)                     │
│  • Validates input                                               │
└────────────────────┬────────────────────────────────────────────┘
                     │ Calls method
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      ⚙️ SERVICE LAYER                            │
│  salesService.searchSales(...)                                  │
│  • Contains business logic                                       │
│  • Processes the request                                         │
└────────────────────┬────────────────────────────────────────────┘
                     │ Queries database
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      💾 DATABASE LAYER                           │
│  SELECT * FROM sales WHERE customer_name = ?                     │
│  • Retrieves data from MySQL                                     │
└────────────────────┬────────────────────────────────────────────┘
                     │ Returns data
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                      🎯 CONTROLLER LAYER                         │
│  return ResponseEntity.ok(sales);                               │
│  • Converts data to JSON                                         │
│  • Sends HTTP response                                           │
└────────────────────┬────────────────────────────────────────────┘
                     │ HTTP Response (JSON)
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                         USER BROWSER                             │
│  Displays search results in table                                │
└─────────────────────────────────────────────────────────────────┘
```

---

## Types of Controllers in Your Application

### 1️⃣ **WebController** - Page Navigation

**Purpose**: Serves HTML pages to users

```java
@Controller  // ← Serves HTML pages
public class WebController {
    
    @GetMapping("/sales")
    public String salesView() {
        return "sales";  // ← Returns sales.html template
    }
}
```

**What it does**:
- When user visits `http://localhost:8090/sales`
- Controller returns the `sales.html` page
- User sees the Sales interface in their browser

**All Pages Managed**:
- `/` → index.html (Home)
- `/sales` → sales.html
- `/purchases` → purchases.html
- `/sales-payments` → sales-payments.html
- `/purchase-payments` → purchase-payments.html
- `/master-data` → master-data.html

---

### 2️⃣ **REST Controllers** - Data Management

**Purpose**: Handle data operations (CRUD) via API endpoints

#### **Example: SalesController**

```java
@RestController  // ← Returns JSON data (not HTML)
@RequestMapping("/api/sales")
public class SalesController {
    
    private final SalesService salesService;
    
    // CREATE - Add new sale
    @PostMapping
    public ResponseEntity<Sales> createSale(@RequestBody Sales sale) {
        Sales created = salesService.createSale(sale);
        return ResponseEntity.ok(created);
    }
    
    // READ - Get sale by ID
    @GetMapping("/{id}")
    public ResponseEntity<Sales> getSaleById(@PathVariable Long id) {
        Sales sale = salesService.getSaleById(id);
        return ResponseEntity.ok(sale);
    }
    
    // UPDATE - Modify existing sale
    @PutMapping("/{id}")
    public ResponseEntity<Sales> updateSale(@PathVariable Long id, 
                                           @RequestBody Sales sale) {
        Sales updated = salesService.updateSale(id, sale);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE - Remove sale
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        salesService.deleteSale(id);
        return ResponseEntity.ok().build();
    }
    
    // SEARCH - Find sales by criteria
    @GetMapping("/search")
    public ResponseEntity<List<Sales>> searchSales(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String customerName) {
        List<Sales> sales = salesService.searchSales(startDate, endDate, customerName);
        return ResponseEntity.ok(sales);
    }
}
```

---

## Real Examples from Your Application

### **Example 1: Creating a Sale**

**Frontend (sales.html)**:
```javascript
// User fills form and clicks "Save"
const saleData = {
    customerName: "John Doe",
    item: "Rice",
    bags: 10,
    amount: 5000
};

fetch('/api/sales', {
    method: 'POST',
    body: JSON.stringify(saleData)
});
```

**Controller (SalesController.java)**:
```java
@PostMapping
public ResponseEntity<Sales> createSale(@RequestBody Sales sale) {
    Sales created = salesService.createSale(sale);  // ← Calls service
    return ResponseEntity.ok(created);  // ← Returns JSON response
}
```

**What happens**:
1. ✅ Browser sends POST request to `/api/sales`
2. ✅ Controller receives the sale data
3. ✅ Controller calls `salesService.createSale()`
4. ✅ Service saves to database
5. ✅ Controller returns saved sale as JSON
6. ✅ Browser shows "Sale saved successfully!"

---

### **Example 2: Searching Sales**

**Frontend (sales.html)**:
```javascript
// User searches for customer "John Doe" from 2025-11-01 to 2025-11-13
fetch('/api/sales/search?startDate=2025-11-01&endDate=2025-11-13&customerName=John Doe')
```

**Controller (SalesController.java)**:
```java
@GetMapping("/search")
public ResponseEntity<List<Sales>> searchSales(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(required = false) String customerName) {
    
    // Controller extracts parameters automatically:
    // startDate = 2025-11-01
    // endDate = 2025-11-13
    // customerName = "John Doe"
    
    List<Sales> sales = salesService.searchSales(startDate, endDate, customerName);
    return ResponseEntity.ok(sales);
}
```

**What happens**:
1. ✅ Browser sends GET request with query parameters
2. ✅ Controller extracts `startDate`, `endDate`, `customerName`
3. ✅ Controller calls service to search database
4. ✅ Service returns filtered sales list
5. ✅ Controller converts list to JSON
6. ✅ Browser displays results in table

---

### **Example 3: Exporting to PDF**

**Frontend (sales.html)**:
```javascript
// User clicks "Export to PDF"
window.open('/api/sales/export/pdf?customerName=John Doe');
```

**Controller (SalesController.java)**:
```java
@GetMapping("/export/pdf")
public ResponseEntity<byte[]> exportSalesToPdf(
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(required = false) String customerName) {
    
    // Get sales data
    List<Sales> sales = salesService.searchSales(startDate, endDate, customerName);
    
    // Generate PDF
    byte[] pdfBytes = pdfExportService.generateSalesPdf(sales, customerName, startDate, endDate);
    
    // Set response headers for PDF download
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "sales_report.pdf");
    
    return ResponseEntity.ok().headers(headers).body(pdfBytes);
}
```

**What happens**:
1. ✅ Browser requests PDF
2. ✅ Controller fetches data from service
3. ✅ Controller generates PDF using PdfExportService
4. ✅ Controller sets proper headers (Content-Type: application/pdf)
5. ✅ Browser downloads the PDF file

---

## Your Application's Controllers

### **Controllers in Your Project**:

| Controller | Type | Purpose | Example Endpoints |
|------------|------|---------|-------------------|
| **WebController** | @Controller | Serves HTML pages | `/`, `/sales`, `/purchases` |
| **SalesController** | @RestController | Manage sales | `/api/sales`, `/api/sales/search` |
| **CustomerController** | @RestController | Manage customers | `/api/customers` |
| **ItemController** | @RestController | Manage items | `/api/items` |
| **ShopController** | @RestController | Manage shops | `/api/shops` |
| **PurchaseController** | @RestController | Manage purchases | `/api/purchases` |
| **SalesPaymentController** | @RestController | Manage payments | `/api/sales-payments` |
| **PurchasePaymentController** | @RestController | Manage shop payments | `/api/purchase-payments` |

---

## Key Annotations Explained

### **@Controller vs @RestController**

```java
// @Controller - Returns HTML pages (views)
@Controller
public class WebController {
    @GetMapping("/sales")
    public String salesView() {
        return "sales";  // ← Returns sales.html
    }
}

// @RestController - Returns JSON data
@RestController
@RequestMapping("/api/sales")
public class SalesController {
    @GetMapping("/recent")
    public ResponseEntity<List<Sales>> getRecentSales() {
        return ResponseEntity.ok(sales);  // ← Returns JSON
    }
}
```

### **HTTP Method Annotations**

```java
@GetMapping      // ← READ data (SELECT)
@PostMapping     // ← CREATE data (INSERT)
@PutMapping      // ← UPDATE data (UPDATE)
@DeleteMapping   // ← DELETE data (DELETE)
```

### **Parameter Annotations**

```java
@PathVariable    // ← Extract from URL path: /api/sales/{id}
@RequestParam    // ← Extract from query: /api/sales?customerName=John
@RequestBody     // ← Extract from request body (JSON)
```

---

## Benefits of Controllers

### ✅ **1. Separation of Concerns**
- Controller handles HTTP requests/responses
- Service handles business logic
- Repository handles database operations

### ✅ **2. Reusability**
- Same controller method can be called from multiple pages
- Different frontends (web, mobile) can use same API

### ✅ **3. Testability**
- Easy to test controller methods independently
- Can mock service layer for unit tests

### ✅ **4. Maintainability**
- Changes to UI don't affect controller logic
- Changes to database don't affect controller

### ✅ **5. Security**
- Controllers can validate input before processing
- Can add authentication/authorization checks

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      FRONTEND (Browser)                      │
│  • sales.html, purchases.html, etc.                          │
│  • JavaScript code making API calls                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ HTTP Requests (GET, POST, PUT, DELETE)
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                   CONTROLLER LAYER                           │
│  WebController     → Serves HTML pages                       │
│  SalesController   → Handles sales API                       │
│  CustomerController→ Handles customer API                    │
│  • Receives requests                                         │
│  • Validates input                                           │
│  • Calls appropriate service                                 │
│  • Returns response                                          │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ Method Calls
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                    SERVICE LAYER                             │
│  SalesService      → Business logic for sales                │
│  CustomerService   → Business logic for customers            │
│  • Processes business rules                                  │
│  • Coordinates multiple operations                           │
│  • Calls repositories                                        │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ Database Queries
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                   REPOSITORY LAYER                           │
│  SalesRepository   → Database operations for sales           │
│  CustomerRepository→ Database operations for customers       │
│  • Executes SQL queries                                      │
│  • Returns data from database                                │
└────────────────────┬────────────────────────────────────────┘
                     │
                     │ SQL Queries
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE (MySQL)                        │
│  Tables: sales, customer, purchase, payment_history, etc.   │
└─────────────────────────────────────────────────────────────┘
```

---

## Summary

**Controllers are essential because they**:
1. 🎯 **Route requests** to the right business logic
2. 📥 **Receive data** from frontend (forms, search queries)
3. ✅ **Validate input** before processing
4. 🔄 **Call services** to perform operations
5. 📤 **Return responses** (HTML pages or JSON data)
6. 🛡️ **Protect** your application layer structure
7. 🧩 **Make code modular** and maintainable

**In your Shree Ranga Traders application**, controllers are the reason you can:
- Click "Search" and get filtered results
- Submit a sale form and save it to database
- Export data to PDF
- Navigate between different pages
- View payment history for specific customers

Without controllers, there would be no way for your beautiful frontend to communicate with the database! 🚀


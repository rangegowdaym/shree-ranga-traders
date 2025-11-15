# Sales Payment Date Filter Feature

## Overview
Added a date filter feature to the Sales Payments page that allows searching for payment records from a specific date onwards (>=).

## Implementation Date
November 15, 2025

## Changes Made

### 1. Backend Repository Layer
**File:** `PaymentHistoryRepository.java`

- Updated the `searchByCustomerAndType` method to `searchByCustomerAndTypeAndDate`
- Added `startDate` parameter of type `LocalDate`
- Updated the query to filter by `paymentDate >= :startDate` when a date is provided
- The date filter is optional (can be null)

```java
@Query("SELECT ph FROM PaymentHistory ph WHERE " +
       "(:customerName IS NULL OR :customerName = '' OR ph.customerName = :customerName) " +
       "AND (:type IS NULL OR ph.type = :type) " +
       "AND (:startDate IS NULL OR ph.paymentDate >= :startDate) " +
       "ORDER BY ph.paymentDate DESC, ph.createdDate DESC")
List<PaymentHistory> searchByCustomerAndTypeAndDate(
    @Param("customerName") String customerName,
    @Param("type") PaymentHistory.TransactionType type,
    @Param("startDate") java.time.LocalDate startDate
);
```

### 2. Service Layer
**File:** `SalesPaymentService.java`

- Updated `searchHistory` method signature to accept `LocalDate startDate` parameter
- Passes the date parameter to the repository method

```java
public List<PaymentHistory> searchHistory(String customerName, PaymentHistory.TransactionType type, LocalDate startDate) {
    return paymentHistoryRepository.searchByCustomerAndTypeAndDate(customerName, type, startDate);
}
```

### 3. Controller Layer
**File:** `SalesPaymentController.java`

- Updated `/api/sales-payments/history/search` endpoint to accept `startDate` parameter
- Added `@DateTimeFormat` annotation for proper date parsing
- Updated `/api/sales-payments/export/pdf` endpoint to also accept and use the date filter
- Both endpoints now pass the date parameter to the service layer

```java
@GetMapping("/history/search")
public ResponseEntity<List<PaymentHistory>> searchHistory(
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
    // ... implementation
}
```

### 4. Frontend (HTML/JavaScript)
**File:** `sales-payments.html`

#### HTML Changes:
- Updated the search form layout to show date, customer, and type fields in a better arrangement
- Changed the date field label from "Payment Date" to "From Date (>=)" to clarify its purpose
- Adjusted column widths for better visual balance

#### JavaScript Changes:
- Updated `loadFilteredHistory()` function to accept and pass the `startDate` parameter
- Modified search form submission handler to include the date value from `searchStartDate` field
- Updated PDF export button to include the date parameter in the export URL
- All three search criteria (Date, Customer, Type) now work together seamlessly

```javascript
async function loadFilteredHistory(customerName = '', type = '', startDate = '') {
    try {
        const params = new URLSearchParams();
        if (customerName) params.append('customerName', customerName);
        if (type) params.append('type', type);
        if (startDate) params.append('startDate', startDate);
        // ... rest of implementation
    }
}
```

## Features

### Search Criteria
Users can now filter payment history using any combination of:
1. **From Date (>=)** - Shows records from this date onwards
2. **Customer Name** - Filter by specific customer or all customers
3. **Type** - Filter by SALE, PAYMENT, ADJUSTMENT, or all types

### How It Works
- All three filters are optional and can be used independently or together
- The date filter uses "greater than or equal to" logic (>=)
- When a date is selected, it shows all payment records from that date onwards
- The search results are always sorted by payment date (descending) and created date (descending)
- Customer balance card is shown when filtering by a specific customer

### PDF Export
- The PDF export feature also respects the date filter
- When exporting, the current search criteria (including date) are applied
- This allows users to export filtered data based on their search parameters

## Usage Example

### Scenario 1: Find all payments from November 1st onwards
1. Select "01-11-2025" in the "From Date" field
2. Leave Customer and Type as "All"
3. Click "Search"
4. Result: All payment records from November 1st, 2025 onwards

### Scenario 2: Find specific customer payments from a date
1. Select "01-11-2025" in the "From Date" field
2. Select a specific customer name
3. Select "PAYMENT" as type
4. Click "Search"
5. Result: Only PAYMENT type records for that customer from November 1st onwards

### Scenario 3: Export filtered data
1. Apply any combination of filters
2. Click "Export to PDF"
3. Result: PDF contains only the filtered records

## Technical Notes

- Date format expected by backend: ISO-8601 (YYYY-MM-DD)
- The HTML date input automatically provides the correct format
- The `@DateTimeFormat` annotation handles parsing from URL parameters
- The date comparison in the database uses `>=` operator for inclusive filtering
- All existing functionality (edit, delete, customer balance display) continues to work normally

## Testing Checklist

- ✅ Compilation successful
- ✅ Date filter works independently
- ✅ Customer filter works independently
- ✅ Type filter works independently
- ✅ All three filters work together
- ✅ Reset button clears all filters
- ✅ PDF export includes date filter
- ✅ Customer balance card displays correctly
- ✅ Edit/Delete payment functionality unaffected

## Files Modified
1. `src/main/java/com/shreerangatraders/repository/PaymentHistoryRepository.java`
2. `src/main/java/com/shreerangatraders/service/SalesPaymentService.java`
3. `src/main/java/com/shreerangatraders/controller/SalesPaymentController.java`
4. `src/main/resources/templates/sales-payments.html`

## Deployment Notes
- The application has been successfully compiled
- To apply changes to running application, restart is required
- No database schema changes needed
- All changes are backward compatible

## Future Enhancements (Optional)
- Add "To Date" filter for date range queries
- Add date presets (Today, This Week, This Month, etc.)
- Add visual indicators showing active filters
- Add filter summary in PDF header


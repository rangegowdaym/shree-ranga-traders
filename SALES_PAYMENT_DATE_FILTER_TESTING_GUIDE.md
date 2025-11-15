# Sales Payment Date Filter - Testing Guide

## Testing the Date Filter Feature

### Prerequisites
1. Application must be running
2. Access the Sales Payments page at: `http://localhost:8080/sales-payments`
3. Have some test data in the database

### Test Cases

#### Test Case 1: Date Filter Only
**Steps:**
1. Open Sales Payments page
2. In the search section, select a date in the "From Date (>=)" field
3. Leave "Customer Name" as "All Customers"
4. Leave "Type" as "All Types"
5. Click "Search"

**Expected Result:**
- Table should display all payment records from the selected date onwards
- Records should be sorted by payment date (most recent first)
- All record types (SALE, PAYMENT, ADJUSTMENT) should be visible

#### Test Case 2: Customer Filter Only
**Steps:**
1. Open Sales Payments page
2. Leave "From Date (>=)" empty
3. Select a specific customer from "Customer Name" dropdown
4. Leave "Type" as "All Types"
5. Click "Search"

**Expected Result:**
- Table should display only records for the selected customer
- Customer balance card should appear showing current balance
- All dates and types should be visible for that customer

#### Test Case 3: Type Filter Only
**Steps:**
1. Open Sales Payments page
2. Leave "From Date (>=)" empty
3. Leave "Customer Name" as "All Customers"
4. Select "PAYMENT" from "Type" dropdown
5. Click "Search"

**Expected Result:**
- Table should display only PAYMENT type records
- Records from all customers and all dates should be visible
- Only green "PAYMENT" badges should be shown

#### Test Case 4: Date + Customer Filter
**Steps:**
1. Open Sales Payments page
2. Select a date in "From Date (>=)"
3. Select a specific customer
4. Leave "Type" as "All Types"
5. Click "Search"

**Expected Result:**
- Table should show only records for the selected customer from the selected date onwards
- Customer balance card should be visible
- All transaction types should be visible for that customer

#### Test Case 5: Date + Type Filter
**Steps:**
1. Open Sales Payments page
2. Select a date in "From Date (>=)"
3. Leave "Customer Name" as "All Customers"
4. Select "SALE" from "Type"
5. Click "Search"

**Expected Result:**
- Table should show only SALE type records from the selected date onwards
- Records from all customers should be visible
- Only yellow "SALE" badges should be shown

#### Test Case 6: Customer + Type Filter
**Steps:**
1. Open Sales Payments page
2. Leave "From Date (>=)" empty
3. Select a specific customer
4. Select "ADJUSTMENT" from "Type"
5. Click "Search"

**Expected Result:**
- Table should show only ADJUSTMENT type records for the selected customer
- Customer balance card should be visible
- Only blue "ADJUSTMENT" badges should be shown
- All dates should be visible

#### Test Case 7: All Three Filters Combined
**Steps:**
1. Open Sales Payments page
2. Select a date in "From Date (>=)"
3. Select a specific customer
4. Select "PAYMENT" from "Type"
5. Click "Search"

**Expected Result:**
- Table should show only PAYMENT records for the selected customer from the selected date onwards
- This is the most restrictive search - only records matching all three criteria
- Customer balance card should be visible
- Only green "PAYMENT" badges should be shown

#### Test Case 8: Reset Filters
**Steps:**
1. Apply any combination of filters
2. Click the "Reset" button

**Expected Result:**
- All filter fields should be cleared
- Table should display all records (no filters applied)
- Customer balance card should disappear (if it was visible)
- All records should be shown in chronological order

#### Test Case 9: PDF Export with Date Filter
**Steps:**
1. Select a date in "From Date (>=)"
2. Select a specific customer
3. Select "PAYMENT" from "Type"
4. Click "Search" to verify results
5. Click "Export to PDF" button

**Expected Result:**
- PDF file should download
- PDF should contain only the filtered records (matching the table display)
- PDF filename should include current date
- PDF should open in a new tab/window

#### Test Case 10: PDF Export without Filters
**Steps:**
1. Click "Reset" to clear all filters
2. Click "Export to PDF" button

**Expected Result:**
- PDF file should download with all payment history records
- All customers, all types, all dates should be included

### Edge Cases to Test

#### Edge Case 1: Future Date
**Steps:**
1. Select a future date in "From Date (>=)"
2. Click "Search"

**Expected Result:**
- Table should be empty or show "No records found"
- No errors should occur

#### Edge Case 2: Very Old Date
**Steps:**
1. Select a date from many years ago (e.g., 2020-01-01)
2. Click "Search"

**Expected Result:**
- Table should show all historical records
- Performance should be acceptable

#### Edge Case 3: Today's Date
**Steps:**
1. Select today's date
2. Click "Search"

**Expected Result:**
- Should show only today's records and future dated records
- Uses >= logic, so includes the selected date

#### Edge Case 4: Customer with No Data
**Steps:**
1. Select a customer that has no payment history
2. Add any date filter
3. Click "Search"

**Expected Result:**
- Table should be empty
- Customer balance card should show balance (may be zero)
- No errors should occur

### Functional Tests

#### Test: Edit Payment with Active Filter
**Steps:**
1. Apply a date filter that shows some PAYMENT records
2. Click "Edit" on a payment record
3. Modify the payment details
4. Save the changes

**Expected Result:**
- Payment should be updated successfully
- Table should refresh with the same filters still applied
- Modified record should still appear if it matches the filter

#### Test: Delete Payment with Active Filter
**Steps:**
1. Apply filters to show some PAYMENT records
2. Click "Delete" on a payment record
3. Confirm deletion

**Expected Result:**
- Payment should be deleted successfully
- Table should refresh with the same filters still applied
- Deleted record should disappear from the table

#### Test: Record New Payment with Active Filter
**Steps:**
1. Apply a date filter
2. Record a new payment with today's date
3. Submit the payment

**Expected Result:**
- Payment should be recorded successfully
- If the payment date matches the filter, it should appear in the table
- If it doesn't match, table should remain unchanged

### Browser Compatibility Tests
Test the date filter functionality in:
- ✅ Chrome/Edge (Chromium-based)
- ✅ Firefox
- ✅ Safari (if available)

### API Endpoint Tests

#### Direct API Test 1: Search with Date
```
GET http://localhost:8080/api/sales-payments/history/search?startDate=2025-11-01
```
**Expected:** JSON array of payment records from Nov 1, 2025 onwards

#### Direct API Test 2: Search with All Parameters
```
GET http://localhost:8080/api/sales-payments/history/search?startDate=2025-11-01&customerName=CustomerA&type=PAYMENT
```
**Expected:** JSON array of PAYMENT records for CustomerA from Nov 1, 2025 onwards

#### Direct API Test 3: PDF Export with Date
```
GET http://localhost:8080/api/sales-payments/export/pdf?startDate=2025-11-01&customerName=CustomerA&type=PAYMENT
```
**Expected:** PDF file download with filtered records

### Performance Tests

#### Test: Large Date Range
**Steps:**
1. Select a very old date (e.g., 2020-01-01)
2. Click "Search"
3. Measure response time

**Expected Result:**
- Query should complete within 2-3 seconds
- Table should load smoothly

#### Test: Rapid Filter Changes
**Steps:**
1. Change date filter multiple times quickly
2. Click "Search" after each change

**Expected Result:**
- Application should handle rapid requests gracefully
- No errors or hanging should occur

### Database Verification

#### SQL Query to Verify Date Filter
```sql
SELECT * FROM payment_history 
WHERE payment_date >= '2025-11-01'
ORDER BY payment_date DESC, created_date DESC;
```

#### SQL Query to Verify All Filters
```sql
SELECT * FROM payment_history 
WHERE customer_name = 'CustomerA'
  AND type = 'PAYMENT'
  AND payment_date >= '2025-11-01'
ORDER BY payment_date DESC, created_date DESC;
```

## Verification Checklist

### Frontend
- ✅ Date input field is visible and labeled "From Date (>=)"
- ✅ Date input accepts date selection
- ✅ Search button triggers search with date parameter
- ✅ Reset button clears the date field
- ✅ Export PDF button includes date in URL

### Backend
- ✅ Controller accepts `startDate` parameter
- ✅ Service passes date to repository
- ✅ Repository query includes date filter with >= operator
- ✅ Compilation successful with no errors

### Integration
- ✅ Date filter works independently
- ✅ Date filter works with customer filter
- ✅ Date filter works with type filter
- ✅ All three filters work together
- ✅ PDF export respects date filter
- ✅ Existing functionality not affected

## Common Issues and Solutions

### Issue 1: Date not filtering correctly
**Solution:** Check that the date format is YYYY-MM-DD in the URL parameters

### Issue 2: No results shown for valid date
**Solution:** Verify the payment_date column in database has the correct dates

### Issue 3: PDF export not including date filter
**Solution:** Check browser console for the export URL - ensure startDate parameter is present

### Issue 4: Application won't start after changes
**Solution:** Rebuild the application with `mvn clean package` and restart

## Success Criteria
The feature is considered successfully implemented when:
1. ✅ Date filter works independently and shows records >= selected date
2. ✅ Date filter works in combination with customer and type filters
3. ✅ PDF export includes date filter
4. ✅ Reset button clears the date field
5. ✅ No regressions in existing functionality
6. ✅ Application compiles without errors
7. ✅ All test cases pass


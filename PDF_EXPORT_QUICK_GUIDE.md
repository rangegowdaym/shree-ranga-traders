# PDF Export Feature - Quick Reference Guide

## Visual Overview

### Sales Payments Page - Search Section

```
┌─────────────────────────────────────────────────────────────────┐
│  Search Payment History                                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Customer Name: [Dropdown ▼]    Type: [Dropdown ▼]            │
│                                                                 │
│  [🔍 Search]  [🔄 Reset]  [📄 PDF]                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Purchase Payments Page - Search Section

```
┌─────────────────────────────────────────────────────────────────┐
│  Search Payment History                                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  Shop Name: [Dropdown ▼]    Type: [Dropdown ▼]                │
│                                                                 │
│  [🔍 Search]  [🔄 Reset]  [📄 PDF]                            │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Button Details

### PDF Export Button
- **Color**: Red (btn-danger)
- **Icon**: 📄 (fas fa-file-pdf)
- **Text**: "PDF"
- **Position**: Right side, after Reset button
- **Action**: Downloads filtered payment history as PDF

## Sample PDF Output Structure

```
═════════════════════════════════════════════════════════════════
                    Shree Ranga Traders
                  Sales Payment History
─────────────────────────────────────────────────────────────────

Customer: Ramesh Kumar
Type: PAYMENT
Generated on: 15-11-2025 15:30:45

─────────────────────────────────────────────────────────────────
ID  Customer     Type      Amount    Balance   Date        Note
                                     After
─────────────────────────────────────────────────────────────────
1   Ramesh       SALE      ₹5000.00  ₹5000.00  01-11-2025  Invoice
2   Ramesh       PAYMENT   ₹2000.00  ₹3000.00  05-11-2025  Part Pay
3   Ramesh       PAYMENT   ₹1000.00  ₹2000.00  10-11-2025  Received
─────────────────────────────────────────────────────────────────

Summary:
Total Records: 3
Total Sales: ₹5000.00
Total Payments: ₹3000.00

═════════════════════════════════════════════════════════════════
```

## Usage Scenarios

### Scenario 1: Export All Payment History
1. Go to Sales/Purchase Payments page
2. Don't select any filters
3. Click **PDF** button
4. Downloads: `Sales_Payments_2025-11-15.pdf` or `Purchase_Payments_2025-11-15.pdf`

### Scenario 2: Export for Specific Customer/Shop
1. Go to Sales/Purchase Payments page
2. Select customer/shop from dropdown
3. Click **Search** (optional)
4. Click **PDF** button
5. Downloads: `Sales_Payments_Ramesh_Kumar_2025-11-15.pdf`

### Scenario 3: Export by Type
1. Go to Sales/Purchase Payments page
2. Select Type (SALE, PAYMENT, PURCHASE, ADJUSTMENT)
3. Click **Search** (optional)
4. Click **PDF** button
5. Downloads: `Sales_Payments_2025-11-15.pdf` (with filtered data)

### Scenario 4: Export with Combined Filters
1. Go to Sales/Purchase Payments page
2. Select both Customer/Shop AND Type
3. Click **Search**
4. Click **PDF** button
5. Downloads: PDF with both filters applied and shown in header

## File Naming Convention

### Sales Payments
- **With Customer**: `Sales_Payments_{CustomerName}_{YYYY-MM-DD}.pdf`
  - Example: `Sales_Payments_Ramesh_Kumar_2025-11-15.pdf`
- **Without Customer**: `Sales_Payments_{YYYY-MM-DD}.pdf`
  - Example: `Sales_Payments_2025-11-15.pdf`

### Purchase Payments
- **With Shop**: `Purchase_Payments_{ShopName}_{YYYY-MM-DD}.pdf`
  - Example: `Purchase_Payments_ABC_Store_2025-11-15.pdf`
- **Without Shop**: `Purchase_Payments_{YYYY-MM-DD}.pdf`
  - Example: `Purchase_Payments_2025-11-15.pdf`

## PDF Content Details

### Header Section
- Company name (centered, bold, 18pt)
- Page title (centered, 14pt)
- Filter information (left-aligned, 10pt)
- Generation timestamp (left-aligned, 10pt)

### Table Section
- Striped rows for readability
- Blue header (RGB: 66, 139, 202)
- Right-aligned amounts
- Compact 8pt font
- Automatic column widths
- Multi-page support

### Summary Section
- Bold heading
- Total record count
- Total sales/purchases amount
- Total payments amount

## Tips & Best Practices

### For Sales Payments
1. **Monthly Reports**: Filter by customer and export monthly
2. **Outstanding Balances**: Export all records to track dues
3. **Payment Tracking**: Filter by PAYMENT type to see all receipts
4. **Sales Analysis**: Filter by SALE type to analyze sales

### For Purchase Payments
1. **Vendor Reports**: Filter by shop and export for reconciliation
2. **Payment History**: Export all records for accounting
3. **Purchase Analysis**: Filter by PURCHASE type to track buying
4. **Settlement Tracking**: Filter by PAYMENT type for settlements

## Troubleshooting

### PDF Not Downloading?
- Check browser's download settings
- Ensure pop-ups are allowed
- Try a different browser
- Check console for errors (F12)

### PDF Contains No Data?
- Verify filters are correct
- Check if data exists in the table
- Click Search before exporting
- Try resetting filters

### Incorrect Data in PDF?
- Ensure you've applied the correct filters
- Click Search to refresh data
- Verify the table shows correct data before export

### Filename Issues?
- Special characters in names may be modified
- Long names may be truncated
- Date is always appended

## Browser Shortcuts

- **Chrome**: Ctrl+J to view downloads
- **Firefox**: Ctrl+Shift+Y to view downloads
- **Edge**: Ctrl+J to view downloads

## Feature Benefits

✅ **Quick Export**: One-click PDF generation
✅ **Filter Support**: Exports only filtered data
✅ **Professional Format**: Clean, business-ready PDFs
✅ **Auto-Summary**: Automatic calculations and totals
✅ **Smart Naming**: Descriptive filenames with dates
✅ **No Server Load**: Client-side generation
✅ **Instant Download**: No waiting for processing
✅ **Mobile Friendly**: Works on all devices

---

**Need Help?** Check the main PDF_EXPORT_FEATURE.md for detailed documentation.


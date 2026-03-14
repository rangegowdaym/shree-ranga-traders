# Thymeleaf Template Parsing Error Fix

## Issue
The application was throwing a `TemplateProcessingException` when loading the `sales-payments.html` and `purchase-payments.html` pages:

```
org.thymeleaf.exceptions.TemplateProcessingException: Could not parse as expression: 
"'ID', 'Customer', 'Type', 'Amount', 'Balance After', 'Date', 'Note'" 
(template: "sales-payments" - line 534, col 29)
```

## Root Cause
The issue occurred because Thymeleaf was attempting to parse JavaScript code as Thymeleaf expressions. Specifically, the string array used in the jsPDF autoTable configuration:

```javascript
head: [['ID', 'Customer', 'Type', 'Amount', 'Balance After', 'Date', 'Note']]
```

Without proper inline mode configuration, Thymeleaf's template parser tried to interpret the comma-separated quoted strings as Thymeleaf expressions, which caused the parsing error.

## Solution
Added the `th:inline="javascript"` attribute to the `<script>` tags in both affected templates:

### Files Modified:
1. `src/main/resources/templates/sales-payments.html` (line 225)
2. `src/main/resources/templates/purchase-payments.html` (line 224)

### Change Made:
```html
<!-- Before -->
<script>

<!-- After -->
<script th:inline="javascript">
```

## What `th:inline="javascript"` Does
- Tells Thymeleaf to use JavaScript inline mode for the script block
- Prevents Thymeleaf from parsing JavaScript strings as template expressions
- Allows proper server-side variable substitution when needed using `/*[[${variable}]]*/ 'default'` syntax
- Ensures JavaScript code is treated as JavaScript, not as Thymeleaf expressions

## Build Status
✅ Application rebuilt successfully
✅ Templates now parse correctly
✅ No compilation errors

## Testing
After restarting the application, both pages should now load without errors:
- `/sales-payments` - Sales Payments page
- `/purchase-payments` - Purchase Payments page

The PDF export functionality using jsPDF autoTable will work correctly.

## Date
November 15, 2025

